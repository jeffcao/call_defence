package com.tblin.HandsetGuardant;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tblin.HandsetGuardant.FunctionListDialog.Function;

public class BlackListAdapter extends BaseAdapter {

	private static Context mContext;
	private List<BlackUser> blackUsers;

	private static LayoutInflater inflater;


	private static final String TAG = BlackListAdapter.class.toString();


	public BlackListAdapter(Context context, List<BlackUser> users) {
		mContext = context;
		blackUsers = users;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return blackUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return blackUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		RelativeLayout view = (RelativeLayout) inflater.inflate(
				R.layout.black_list_item, null);
		final BlackUser user = (BlackUser) getItem(position);
		TextView name = (TextView) view.findViewById(R.id.black_list_item_name);
		TextView mobile = (TextView) view
				.findViewById(R.id.black_list_item_mobile);
		ImageView header = (ImageView) view
				.findViewById(R.id.black_list_item_header);
		TextView typetext=(TextView) view.findViewById(R.id.black_list_item_type);
		TextView address=(TextView) view.findViewById(R.id.black_list_item_adress);
		int type = BlackListDBHelper.getInstance(mContext).queryBlock(
				user.getMobile());
		address.setText(user.getAddress());
		name.setText(user.getName());
		mobile.setText(user.getMobile());
		int k=user.getType();
		switch(k){
		case 800: typetext.setText("拦截来电＋短信");break;
		case 801:typetext.setText("拦截来电");break;
		case 810:typetext.setText("拦截短信");break;
		case 811:typetext.setText("未作拦截");break;
		}
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<Function> functions = new ArrayList<FunctionListDialog.Function>();
				FunctionListDialog.Function delete = new FunctionListDialog.Function() {

					@Override
					public void onClick() {
						deleteOnClick(user);
					}

					@Override
					public String myName() {
						return "删除黑名单";
					}
				};
				FunctionListDialog.Function edit = new FunctionListDialog.Function() {

					@Override
					public void onClick() {
						editOnClick(user);
					}

					@Override
					public String myName() {
						return "编辑详情";
					}
				};
				FunctionListDialog.Function modifyBlock = new FunctionListDialog.Function() {

					@Override
					public void onClick() {
						modifyBlock(user);
					}

					@Override
					public String myName() {
						return "来电拦截方式";
					}
				};
				functions.add(delete);
				functions.add(edit);
				functions.add(modifyBlock);
				CommonDialog dialog = new FunctionListDialog(mContext,
						functions);
				dialog.setTitle(user.getMobile());
				dialog.show();
			}
		});
		return view;
	}

	private void modifyBlock(final BlackUser user) {
		final CommonDialog dialog = new CommonDialog(mContext);
		dialog.setTitle("拦截方式");
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.block_type, null);
		LinearLayout kill = (LinearLayout) view
				.findViewById(R.id.block_type_end);
		LinearLayout silence = (LinearLayout) view
				.findViewById(R.id.block_type_silence);
		int type = BlackListDBHelper.getInstance(mContext).queryBlock(
				user.getMobile());
		Logger.i(TAG, "block:" + type);
		if (type == CallHandler.BLOCK_KILL) {
			RadioButton killButton = (RadioButton) view
					.findViewById(R.id.block_type_kill_radio);
			killButton.setChecked(true);
		} else {
			RadioButton silenceButton = (RadioButton) view
					.findViewById(R.id.block_type_silence_radio);
			silenceButton.setChecked(true);
		}
		kill.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setBlock(user.getMobile(), CallHandler.BLOCK_KILL);
				dialog.dismiss();
			}
		});
		silence.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setBlock(user.getMobile(), CallHandler.BLOCK_SILIENCE);
				dialog.dismiss();
			}
		});
		dialog.setView(view);
		dialog.show();
	}

	private void setBlock(String mobile, int type) {
		BlackListDBHelper db = BlackListDBHelper.getInstance(mContext);
		long result = db.updateBlock(mobile, type);
		if (result != -1) {
			Toast.makeText(mContext, "设置成功", Toast.LENGTH_LONG).show();
		}
	}

	private void deleteOnClick(final BlackUser user) {
		YesNoDialog dialog = new YesNoDialog(mContext);
		dialog.setTitle("删除");
		dialog.setMessage("确定删除？");
		dialog.setYesButton(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				BlackListDBHelper db = BlackListDBHelper.getInstance(mContext);
				boolean result = db.delete(user.getMobile());
				if (result) {
					Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
				}
			}
		});
		dialog.show();
	}

	public static void editOnClick(final BlackUser user) {
		final YesNoDialog dialog = new YesNoDialog(mContext);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.modify_black_user, null);
		final EditText name = (EditText) layout
				.findViewById(R.id.modify_black_user_name);
		final EditText mobile = (EditText) layout
				.findViewById(R.id.modify_black_user_mobile);
		final String updateMobile = user.getMobile();
		final CheckBox call=(CheckBox) layout.findViewById(R.id.ed_black_call);
		final CheckBox sms=(CheckBox) layout.findViewById(R.id.ed_black_sms);
		dialog.setTitle("编辑");
		dialog.setView(layout);
		call.setChecked(user.isIskillcall());
		sms.setChecked(user.isIskillsms());
		name.setText(user.getName());
		mobile.setText(user.getMobile());
		
		dialog.setYesButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(call.isChecked()){
					if(sms.isChecked()){
						editProcess(name, mobile, updateMobile,800);
					}else{
						editProcess(name, mobile, updateMobile,801);
					}
				}else{
					if(sms.isChecked()){
						editProcess(name, mobile, updateMobile,810);
					}else{
						editProcess(name, mobile, updateMobile,811);
					}
				}
				
				dialog.dismiss();
				
			}

		}, YesNoDialog.MANUAL_DISMISS);
		dialog.show();
		
	}

	public static void editProcess(EditText name, EditText mobile, String updateMobile,int type) {
		BlackListDBHelper blackListDB = BlackListDBHelper.getInstance(FirewallApplication.CONTEXT);
		blackListDB.delete(updateMobile);
		String saveName = name.getText().toString();
		String saveMobile = mobile.getText().toString();
		if (saveName == null || saveName.equals("")) {
			Toast.makeText(mContext, "请输入名称", Toast.LENGTH_LONG).show();
		} else if (saveMobile == null || saveMobile.equals("")) {
			Toast.makeText(mContext, "请输入电话", Toast.LENGTH_LONG).show();
		} else {
			BlackListActivity.insertToDB(mContext, saveName, saveMobile, type, false);
			Toast.makeText(mContext, "编辑成功", Toast.LENGTH_LONG).show();
		}
	}
}
