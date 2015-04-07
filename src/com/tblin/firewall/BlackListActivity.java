package com.tblin.firewall;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tblin.firewall.R;
import com.tblin.firewall.BlackListDBHelper.OnDataChanged;
import com.tblin.firewall.FunctionListDialog.Function;
import com.tblin.market.ui.EmbedHome;

public class BlackListActivity extends Activity {

	private ListView blackList;
	private Button addBlack;
	private TextView noBlack;
	private BaseAdapter blackAdapter;
	private BlackListDBHelper blackListDB;
	private List<BlackUser> blackUsers;
	private OnDataChanged blackUserListLsnr;
//	private ImageView adView;
	private static int SOURCE_TYPE_CONTACTERS = 1;
	private static int SOURCE_TYPE_CALL_RECORD = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.black_list_main);
		init();
	}

	private void init() {
//		adView = (ImageView) findViewById(R.id.black_list_main_ad);
		blackList = (ListView) findViewById(R.id.black_list_main_list_view);
		addBlack = (Button) findViewById(R.id.black_list_main_add);
		noBlack = (TextView) findViewById(R.id.black_list_main_no_black);
		blackListDB = BlackListDBHelper.getInstance(this);
		blackUsers = blackListDB.getAllUsers();
		
		blackAdapter = new BlackListAdapter(this, blackUsers);
		blackList.setAdapter(blackAdapter);
		if (blackUsers.isEmpty()) {
			blackList.setVisibility(View.GONE);
			noBlack.setVisibility(View.VISIBLE);
		}
//		adView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent it = new Intent(BlackListActivity.this, EmbedHome.class);
//				it.putExtra("start_class", MainTabActivity.class);
//				startActivity(it);
//			}
//		});
		addBlack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addBlack();
			}
		});
		blackUserListLsnr = new OnDataChanged() {

			@Override
			public void onDataChange() {
				Logger.i("2321321321", "call back");
				List<BlackUser> users = blackListDB.getAllUsers();
				if (users.isEmpty()) {
					if (noBlack != null) {
						noBlack.setVisibility(View.VISIBLE);
					}
					if (blackList != null) {
						blackList.setVisibility(View.GONE);
					}
				} else {
					if (noBlack != null) {
						noBlack.setVisibility(View.GONE);
					}
					if (blackList != null) {
						blackList.setVisibility(View.VISIBLE);
					}
				}
				blackUsers.clear();
				for (BlackUser user : users) {
					blackUsers.add(user);
				}
				if (blackAdapter != null) {
					Logger.i("2321321321", "notify data set changed");
					blackAdapter.notifyDataSetChanged();
				} else {
					Logger.i("2321321321",
							"notify data set changed adapter is null");
				}
			}
		};
		blackListDB.registDataLsnr(blackUserListLsnr);
	}

	private void addBlack() {
		Function manualAdd = new Function() {

			@Override
			public void onClick() {
				confirmAddBlack(null, null);
			}

			@Override
			public String myName() {
				return "手动添加号码";
			}
		};
		Function fromContact = new Function() {

			@Override
			public void onClick() {
				selectFromSource(SOURCE_TYPE_CONTACTERS);
			}

			@Override
			public String myName() {
				return "从联系人选择";
			}
		};
		Function fromSpeakHistory = new Function() {

			@Override
			public void onClick() {
				selectFromSource(SOURCE_TYPE_CALL_RECORD);
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
						.getFirstContacter(BlackListActivity.this);
				if (contact == null) {
					Toast.makeText(BlackListActivity.this, "没有通话记录",
							Toast.LENGTH_LONG).show();
				} else {
					BlackUser user =new BlackUser();
					user.setIskillcall(true);
					user.setIskillsms(true);
					user.setMobile(contact[1]);
					user.setName(contact[0]);
					
					BlackListAdapter.editOnClick(user);
//					addBlack(contact[0], contact[1],01);
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
				BlackListActivity.this, functions);
		dialog.setTitle(getResources().getString(R.string.add_blacklist));
		dialog.show();
	}

	private void confirmAddBlack(String userName, String userMobile) {
		if (userName == null) {
			userName = blackListDB.getName(userMobile);
		}
		LayoutInflater inflater = BlackListActivity.this.getLayoutInflater();
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.manual_add_black, null);
		final EditText name = (EditText) view
				.findViewById(R.id.manual_add_black_user_name);
		final EditText mobile = (EditText) view
				.findViewById(R.id.manul_add_black_user_mobile);
		final CheckBox	call =(CheckBox) view.findViewById(R.id.black_call);
		final CheckBox	sms =(CheckBox) view.findViewById(R.id.black_sms);
		
		name.setText(userName);
		name.setHint("可选");
		mobile.setText(userMobile);
		mobile.setHint("必填");
		final YesNoDialog ynd = new YesNoDialog(BlackListActivity.this);
		ynd.setView(view);
		ynd.setYesButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String saveName = name.getText().toString();
				String saveMobile = mobile.getText().toString();
				if (saveMobile == null || "".equals(saveMobile)) {
					Toast.makeText(BlackListActivity.this, "请输入电话",
							Toast.LENGTH_LONG).show();
				} else {
					ynd.dismiss();
			
					switchType(call.isChecked(), sms.isChecked(),saveName,saveMobile);
					//TODO
					
				}
			}
		}, YesNoDialog.MANUAL_DISMISS);
		ynd.setTitle(getResources().getString(R.string.manual_add_blacklist));
		ynd.show();
	}

	private void addBlack(String userName, String userMobile,int type) {
		if (userMobile == null || "".equals(userMobile)) {
			Toast.makeText(BlackListActivity.this, "不能添加空号吗", Toast.LENGTH_LONG)
					.show();
		}
		if (MainTabActivity.nativePrefix.equals(userMobile)) {

			YesNoDialog ynd = new YesNoDialog(BlackListActivity.this);
			String area = PhonePrefixHelper
					.getAreaName(MainTabActivity.nativePrefix);
			final String name = (userName == null || "".equals(userName)) ? area
					: userName;
			ynd.setMessage("您的号码所在地为：" + area + "\n是否拦截所有来自" + area + "的号码？");
			
			ynd.setYesButton(new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					SettingPreferenceHandler sph = SettingPreferenceHandler.getInstance();
					sph.setBlockNatvie(true);
					insertToDB(name, MainTabActivity.nativePrefix,01);
				}
			});
			ynd.show();
			return;
		}
		insertToDB(userName, userMobile,type);
	}

	private void insertToDB(String userName, String userMobile,int type) {
		if (userName == null) {
			userName = blackListDB.getName(userMobile);
		}
		long re = blackListDB.insert(userName, userMobile,type);
		if (re == -1) {
			Toast.makeText(BlackListActivity.this, "不能添加空号码", Toast.LENGTH_LONG)
					.show();
		} else if (re == -2) {
			Toast.makeText(BlackListActivity.this, "号码已添加过", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(BlackListActivity.this, "添加成功", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void selectFromSource(int sourceType) {
		String message = (sourceType == SOURCE_TYPE_CALL_RECORD ? "正在读取通话记录..."
				: "正在读取联系人列表...");
		final int type = sourceType;
		final ProgressDialog dialog;
		dialog = new ProgressDialog(BlackListActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage(message);
		dialog.setCancelable(false);
		dialog.show();
		final List<String[]> contacts = new ArrayList<String[]>();
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
				final CommonDialog dialog = new CommonDialog(
						BlackListActivity.this);
				final ListAdapter adapter = new ContactListAdapter(
						BlackListActivity.this, contacts);
				LayoutInflater inflater = BlackListActivity.this
						.getLayoutInflater();
				LinearLayout view = (LinearLayout) inflater.inflate(
						R.layout.contact_list, null);
				ListView list = (ListView) view
						.findViewById(R.id.contact_list_view);
				list.setAdapter(adapter);
			
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String[] contacter = (String[]) adapter
								.getItem(position);
						if (contacter != null && contacter.length == 2) {
							dialog.dismiss();
								BlackUser user =new BlackUser();
							user.setIskillcall(true);
							user.setIskillsms(true);
							user.setMobile(contacter[1]);
							user.setName(contacter[0]);
							
							BlackListAdapter.editOnClick(user);
//							addBlack(contacter[0], contacter[1],01);
						}
					}
				});
				dialog.setTitle(getResources()
						.getString(R.string.add_blacklist));
				dialog.setView(view);
				dialog.show();
			}

		};
		Runnable r = new Runnable() {

			@Override
			public void run() {
				List<String[]> contact = null;
				if (type == SOURCE_TYPE_CALL_RECORD) {
					contact = ContactInformationGetter
							.getContactRecord(BlackListActivity.this);
				} else {
					ContacterDBHelper db = ContacterDBHelper
							.getInstance(BlackListActivity.this);
					contact = db.getAllContacts();
				}
				for (String[] temp : contact) {
					contacts.add(temp);
				}
				Message msg = handler.obtainMessage();
				handler.sendMessage(msg);
			}
		};
		new Thread(r).start(); 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	private void switchType(boolean isCall,boolean isSMS,String saveName,String saveMobile){
		if(isCall){
			if(isSMS){
				insertToDB(saveName, saveMobile,800);
			}else {
				//禁电话，不禁短信
				insertToDB(saveName, saveMobile,801);
			}
		}else{
			if(isSMS){
				//不禁电话，禁短信
				insertToDB(saveName, saveMobile,810);
			}else{
				//两个不禁
				insertToDB(saveName, saveMobile,811);
			}
		}
	}
}
