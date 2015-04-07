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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tblin.HandsetGuardant.FunctionListDialog.Function;

public class KeywordListAdapter extends BaseAdapter {
	private Context context;
	private List<KeyWorder> keywords;
	private LayoutInflater inflater;

	public KeywordListAdapter(Context context, List<KeyWorder> KeyWords) {
		this.keywords = KeyWords;

		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return keywords.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return keywords.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.sms_keyword_item, null);
		TextView content = (TextView) convertView
				.findViewById(R.id.sms_keyword_content);
		final KeyWorder keyword = keywords.get(position);

		String name = keyword.getName();
		content.setText(name);

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<Function> functions = new ArrayList<FunctionListDialog.Function>();
				FunctionListDialog.Function delete = new FunctionListDialog.Function() {

					@Override
					public void onClick() {
						deleteOnClick(keyword);
					}

					@Override
					public String myName() {
						return "删除";
					}
				};
				FunctionListDialog.Function edit = new FunctionListDialog.Function() {

					@Override
					public void onClick() {
						editOnClick(keyword);
					}

					@Override
					public String myName() {
						return "编辑";
					}
				};

				functions.add(delete);
				functions.add(edit);
				CommonDialog dialog = new FunctionListDialog(context, functions);
				dialog.setTitle(keyword.getName());
				dialog.show();

			}

			private void deleteOnClick(final KeyWorder keyword) {
				YesNoDialog dialog = new YesNoDialog(context);
				dialog.setTitle("删除");
				dialog.setMessage("确定删除？");
				dialog.setYesButton(new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DisplayCallUitl.delKeyword(context, keyword.getName());
						Toast.makeText(context, "已成功删除", Toast.LENGTH_LONG)
								.show();

					}
				});
				dialog.show();

			}

			private void editOnClick(KeyWorder keyword) {
				YesNoDialog dialog = new YesNoDialog(context);
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout layout = (LinearLayout) inflater.inflate(
						R.layout.sms_keyword_add, null);
				dialog.setView(layout);
				dialog.setTitle("编辑短信关键字");
				final EditText keywordedit = (EditText) layout
						.findViewById(R.id.keyword);
				final String name = keyword.getName();
				keywordedit.setText(name);
				dialog.setYesButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String keyword = keywordedit.getText()
										.toString();
								if (keyword.trim().equals("")
										|| keyword == null) {
									Toast.makeText(context, "请输入关键字",
											Toast.LENGTH_LONG).show();
								} else {

									if (inKeyWord(context, keyword)) {
										Toast.makeText(context, "关键字已经存在.",
												Toast.LENGTH_LONG).show();
									} else {
										DisplayCallUitl.updateKey(context,
												name, keyword);

										dialog.dismiss();
										Toast.makeText(context, "编辑成功.",
												Toast.LENGTH_LONG).show();
									}

								}
							}

						}, YesNoDialog.MANUAL_DISMISS);

				dialog.setNoButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();

			}

			private boolean inKeyWord(Context context, String newkeyword) {

				ArrayList<KeyWorder> keys = DisplayCallUitl.getKeyWord();
				for (KeyWorder k : keys) {
					if (newkeyword.equals(k.getName())) {

						return true;

					}
				}
				return false;
			}
		});
		return convertView;
	}

}
