package com.tblin.HandsetGuardant;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SmsKeywordActivity extends Activity {

	private ListView keywordList;
	private Button addkeyword;
	
	private BaseAdapter keywordAdapter;
	private BroadcastReceiver Receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_keyword_list);
		init();

		Receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				init();

			}
		};
		IntentFilter fi = new IntentFilter();
		fi.addAction("com.tblin.firewall.key");
		registerReceiver(Receiver, fi);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(Receiver);
		super.onDestroy();
	}

	private void init() {
		keywordList = (ListView) findViewById(R.id.black_list_main_list_view);
		addkeyword = (Button) findViewById(R.id.keyword_list_main_add);
	//	keyword = (TextView) findViewById(R.id.sms_keyword);

		ArrayList<KeyWorder> keys = DisplayCallUitl.getKeyWord();

		keywordAdapter = new KeywordListAdapter(this, keys);
		keywordList.setAdapter(keywordAdapter);
		addkeyword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addkeyword();
			}
		});
	}

	protected void addkeyword() {
		YesNoDialog dialog = new YesNoDialog(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.sms_keyword_add, null);
		dialog.setView(layout);
		dialog.setTitle("添加短信关键字");
		final EditText keywordedit = (EditText) layout
				.findViewById(R.id.keyword);

		dialog.setYesButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String keyword = keywordedit.getText().toString();
				if (keyword.trim().equals("") || keyword == null) {
					Toast.makeText(SmsKeywordActivity.this, "请输入关键字",
							Toast.LENGTH_LONG).show();

				} else {
					if (inKeyWord(SmsKeywordActivity.this, keyword)) {
						Toast.makeText(SmsKeywordActivity.this, "关键字已经存在.",
								Toast.LENGTH_LONG).show();
					} else {
						DisplayCallUitl.insertKeyWord(SmsKeywordActivity.this,
								keyword);

						dialog.dismiss();
						Toast.makeText(SmsKeywordActivity.this, "成功添加.",
								Toast.LENGTH_LONG).show();
					}
				}

			}
		}, YesNoDialog.MANUAL_DISMISS);

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
}
