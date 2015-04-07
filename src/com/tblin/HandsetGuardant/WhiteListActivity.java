package com.tblin.HandsetGuardant;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tblin.HandsetGuardant.FunctionListDialog.Function;
import com.tblin.HandsetGuardant.WhiteListDBHelper.OnDataChanged;
import com.tblin.HandsetGuardant.sel.SelActivity;

public class WhiteListActivity extends Activity {

	private ListView white_list;
	private Button add_white;
	private TextView no_white;
	private BaseAdapter white_adapter;
	private static WhiteListDBHelper white_list_db = WhiteListDBHelper.getInstance(FirewallApplication.CONTEXT);;
	private List<WhiteUser> white_users;
	private OnDataChanged whiteUserListLsnr;
	private static String TAG="WhiteListActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.white_list_main);
		DisplayCallUitl.saveDB(this);
		boolean isser=isServiceExisted(this, "com.tblin.HandsetGuardant.DisplayCallService");
	
		if(!isser){
			Intent it = new Intent();
			it.setClass(this, DisplayCallService.class);
			startService(it);
			Logger.i(TAG, "Activity------->start displaycallservice");
		}
		init();
	}

	private void init() {

		white_list = (ListView) findViewById(R.id.white_list_main_list_view);
		add_white = (Button) findViewById(R.id.white_list_main_add);
		no_white = (TextView) findViewById(R.id.white_list_main_no_white);
		
		
		white_users = white_list_db.getAllUsers();
		
		white_adapter = new WhiteListAdapter(this, white_users);
		white_list.setAdapter(white_adapter);
		if (white_users.isEmpty()) {
			white_list.setVisibility(View.GONE);
			no_white.setVisibility(View.VISIBLE);
		}

		add_white.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addWhite();
			}
		});
		whiteUserListLsnr = new OnDataChanged() {

			@Override
			public void onDataChange() {
				Logger.i("2321321321", "call back");
				List<WhiteUser> users = white_list_db.getAllUsers();
				if (users.isEmpty()) {
					if (no_white != null) {
						no_white.setVisibility(View.VISIBLE);
					}
					if (white_list != null) {
						white_list.setVisibility(View.GONE);
					}
				} else {
					if (no_white != null) {
						no_white.setVisibility(View.GONE);
					}
					if (white_list != null) {
						white_list.setVisibility(View.VISIBLE);
					}
				}
				white_users.clear();
				for (WhiteUser user : users) {
					white_users.add(user);
				}
				if (white_adapter != null) {
					Logger.i("2321321321", "notify data set changed");
					white_adapter.notifyDataSetChanged();
				} else {
					Logger.i("2321321321",
							"notify data set changed adapter is null");
				}
			}
		};
		white_list_db.registDataLsnr(whiteUserListLsnr);
	}

	private void addWhite() {
		Function manualAdd = new Function() {

			@Override
			public void onClick() {
				confirmAddWhite(null, null);
			}

			@Override
			public String myName() {
				return "手动添加号码";
			}
		};
		Function fromContact = new Function() {

			@Override
			public void onClick() {
				Intent it = new Intent(WhiteListActivity.this, SelActivity.class);
				it.putExtra("source", "contact");
				it.putExtra("type", "white");
				startActivity(it);
			//	selectFromSource(SOURCE_TYPE_CONTACTERS);
			}

			@Override
			public String myName() {
				return "从联系人选择";
			}
		};
		Function fromSpeakHistory = new Function() {

			@Override
			public void onClick() {
				Intent it = new Intent(WhiteListActivity.this, SelActivity.class);
				it.putExtra("source", "call_logs");
				it.putExtra("type", "white");
				startActivity(it);
			//	selectFromSource(SOURCE_TYPE_CALL_RECORD);
			}

			@Override
			public String myName() {
				return "从通话记录选择";
			}
		};
		Function fromLastSpeak = new Function() {

			@Override
			public void onClick() {
				String[] contact = ContactInformationGetter
						.getFirstContacter(WhiteListActivity.this);
				if (contact == null) {
					Toast.makeText(WhiteListActivity.this, "没有通话记录",
							Toast.LENGTH_LONG).show();
				} else {
					WhiteUser user =new WhiteUser();
					user.setMobile(contact[1]);
					user.setName(contact[0]);
					
					WhiteListAdapter.editOnClick(user);
				}
			}

			@Override
			public String myName() {
				return "从上一次来电";
			}
		};
		List<Function> functions = new ArrayList<FunctionListDialog.Function>();
		functions.add(manualAdd);
		functions.add(fromContact);
		functions.add(fromSpeakHistory);
		functions.add(fromLastSpeak);
		FunctionListDialog dialog = new FunctionListDialog(
				WhiteListActivity.this, functions);
		dialog.setTitle(getResources().getString(R.string.add_whitelist));
		dialog.show();
	}

	private void confirmAddWhite(String userName, String userMobile) {
		if (userName == null) {
			userName = white_list_db.getName(userMobile);
		}
		LayoutInflater inflater = WhiteListActivity.this.getLayoutInflater();
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.manual_add_white, null);
		final EditText name = (EditText) view
				.findViewById(R.id.manual_add_white_user_name);
		final EditText mobile = (EditText) view
				.findViewById(R.id.manul_add_white_user_mobile);
		
		name.setText(userName);
		name.setHint("可选");
		mobile.setText(userMobile);
		mobile.setHint("必填");
		final YesNoDialog ynd = new YesNoDialog(WhiteListActivity.this);
		ynd.setView(view);
		ynd.setYesButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String saveName = name.getText().toString();
				String saveMobile = mobile.getText().toString();
				if (saveMobile == null || "".equals(saveMobile)) {
					Toast.makeText(WhiteListActivity.this, "请输入电话",
							Toast.LENGTH_LONG).show();
				} else {
					ynd.dismiss();
			
					switchType(WhiteListActivity.this, saveName,saveMobile, true);
					//TODO
					
				}
			}
		}, YesNoDialog.MANUAL_DISMISS);
		ynd.setTitle(getResources().getString(R.string.manual_add_whitelist));
		ynd.show();
	}

	
	public static void insertToDB(Context context, String userName, String userMobile) {
		insertToDB(context, userName, userMobile, true);
	}

	public static void insertToDB(Context context, String userName, String userMobile, boolean need_notify) {
		if (userName == null) {
			userName = white_list_db.getName(userMobile);
		}
		String num=DisplayCallUitl.proNumber(userMobile);
		String str= DisplayCallService.getInCommingNUm(num);
		long re = white_list_db.insert(userName, userMobile,str);
		if (re > 0) {
			//加入白名单时，如果在黑名单中存在，直接删除
			BlackListDBHelper.getInstance(context).delete(userMobile);
		}
		if (!need_notify) return;
		if (re == -1) {
			Toast.makeText(context, "不能添加空号码", Toast.LENGTH_LONG)
					.show();
		} else if (re == -2) {
			Toast.makeText(context, "号码已添加过", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(context, "添加成功", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	public static void switchType(Context context, String saveName,String saveMobile, boolean need_notify){
		insertToDB(context, saveName, saveMobile, need_notify);
		Logger.i(TAG, "-------------->call="+saveMobile+"***name="+saveName);
	}
	
	//判断服务是否在运行.
	 public  boolean isServiceExisted(Context context, String className) {
	        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

	        if(!(serviceList.size() > 0)) {
	            return false;
	        }

	        for(int i = 0; i < serviceList.size(); i++) {
	            RunningServiceInfo serviceInfo = serviceList.get(i);
	            ComponentName serviceName = serviceInfo.service;
	           
	            if(serviceName.getClassName().equals(className)) {
	                return true;
	            }
	        }
	        return false;
	    }
}
