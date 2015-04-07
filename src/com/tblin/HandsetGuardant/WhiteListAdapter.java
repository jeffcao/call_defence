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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tblin.HandsetGuardant.FunctionListDialog.Function;

public class WhiteListAdapter extends BaseAdapter {

	private static Context mContext;
	private List<WhiteUser> WhiteUsers;

	private static LayoutInflater inflater;


	private static final String TAG = WhiteListAdapter.class.toString();


	public WhiteListAdapter(Context context, List<WhiteUser> users) {
		mContext = context;
		WhiteUsers = users;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return WhiteUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return WhiteUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		RelativeLayout view = (RelativeLayout) inflater.inflate(
				R.layout.white_list_item, null);
		final WhiteUser user = (WhiteUser) getItem(position);
		TextView name = (TextView) view.findViewById(R.id.white_list_item_name);
		TextView mobile = (TextView) view
				.findViewById(R.id.white_list_item_mobile);
		ImageView header = (ImageView) view
				.findViewById(R.id.white_list_item_header);
		TextView address=(TextView) view.findViewById(R.id.white_list_item_adress);
		address.setText(user.getAddress());
		name.setText(user.getName());
		mobile.setText(user.getMobile());
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
						return "删除白名单";
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
				functions.add(delete);
				functions.add(edit);
				CommonDialog dialog = new FunctionListDialog(mContext,
						functions);
				dialog.setTitle(user.getMobile());
				dialog.show();
			}
		});
		return view;
	}

	private void deleteOnClick(final WhiteUser user) {
		YesNoDialog dialog = new YesNoDialog(mContext);
		dialog.setTitle("删除");
		dialog.setMessage("确定删除？");
		dialog.setYesButton(new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				WhiteListDBHelper db = WhiteListDBHelper.getInstance(mContext);
				boolean result = db.delete(user.getMobile());
				if (result) {
					Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
				}
			}
		});
		dialog.show();
	}

	public static void editOnClick(final WhiteUser user) {
		final YesNoDialog dialog = new YesNoDialog(mContext);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.modify_white_user, null);
		final EditText name = (EditText) layout
				.findViewById(R.id.modify_white_user_name);
		final EditText mobile = (EditText) layout
				.findViewById(R.id.modify_white_user_mobile);
		final String updateMobile = user.getMobile();
		dialog.setTitle("编辑");
		dialog.setView(layout);
		name.setText(user.getName());
		mobile.setText(user.getMobile());
		
		dialog.setYesButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				editProcess(name, mobile, updateMobile);
				dialog.dismiss();
				
			}

		}, YesNoDialog.MANUAL_DISMISS);
		dialog.show();
		
	}

	public static void editProcess(EditText name, EditText mobile, String updateMobile) {
		WhiteListDBHelper wldbh = WhiteListDBHelper.getInstance(mContext);
		wldbh.delete(updateMobile);
		String saveName = name.getText().toString();
		String saveMobile = mobile.getText().toString();
		if (saveName == null || saveName.equals("")) {
			Toast.makeText(mContext, "请输入名称", Toast.LENGTH_LONG).show();
		} else if (saveMobile == null || saveMobile.equals("")) {
			Toast.makeText(mContext, "请输入电话", Toast.LENGTH_LONG).show();
		} else {
			WhiteListActivity.switchType(mContext, saveName, saveMobile, false);
			Toast.makeText(mContext, "编辑成功", Toast.LENGTH_LONG).show();
		}
	}
}
