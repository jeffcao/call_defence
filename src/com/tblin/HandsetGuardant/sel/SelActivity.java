package com.tblin.HandsetGuardant.sel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.tblin.HandsetGuardant.BlackListActivity;
import com.tblin.HandsetGuardant.BlackListDBHelper;
import com.tblin.HandsetGuardant.BlackUser;
import com.tblin.HandsetGuardant.FirewallApplication;
import com.tblin.HandsetGuardant.R;
import com.tblin.HandsetGuardant.WhiteListActivity;
import com.tblin.HandsetGuardant.WhiteListDBHelper;
import com.tblin.HandsetGuardant.WhiteUser;

public class SelActivity extends Activity {
	private List<Contacter> cs;
	private Button confirm_btn, cancel_btn;
	private EditText search;
	private CheckBox sel_call, sel_sms;
	private ListView list_view;
	private ImageView sel_title;
	private View sel_cb_layout, sel_bg_line;
	private ContactAdapter adapter;
	private List<Contacter> contacters;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sel);

		prepareViews();
		init();
		queryContacts();

	}

	private void prepareViews() {
		confirm_btn = (Button) findViewById(R.id.sel_confirm);
		search = (EditText) findViewById(R.id.sel_search);
		sel_call = (CheckBox) findViewById(R.id.sel_block_call);
		sel_sms = (CheckBox) findViewById(R.id.sel_block_sms);
		list_view = (ListView) findViewById(R.id.list);
		cancel_btn = (Button) findViewById(R.id.sel_cancel);
		sel_title = (ImageView) findViewById(R.id.sel_title_img);
		sel_cb_layout = findViewById(R.id.sel_cb_layout);
		sel_bg_line = findViewById(R.id.sel_bg_line);
	}

	private void init() {
		contacters = new ArrayList<Contacter>();
		adapter = new ContactAdapter(contacters, this);
		list_view.setAdapter(adapter);
		TextWatcher tw = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (null == cs)
					return;
				List<Contacter> fills = Contacter.filter(cs, s.toString());
				contacters.clear();
				contacters.addAll(fills);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		search.addTextChangedListener(tw);
		confirm_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onConfirm();
			}
		});
		cancel_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		String type = getIntent().getStringExtra("type");
		if (!"black".equals(type)) {
			sel_cb_layout.setVisibility(View.GONE);
			sel_title.setImageResource(R.drawable.baimingdan_biaoti);
			RelativeLayout.LayoutParams param = (LayoutParams) sel_bg_line.getLayoutParams();
			param.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.sel_title);
		}
	}

	private void queryContacts() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				String source = getIntent().getStringExtra("source");
				if ("contact".equals(source)) {
					cs = ContactInformationGetter.transfer(
							ContactInformationGetter
									.getContacters(getApplicationContext()),
							getApplicationContext());
				} else {
					cs = ContactInformationGetter.transfer(
							ContactInformationGetter
									.getContactRecord(getApplicationContext()),
							getApplicationContext());
				}
				Runnable r2 = new Runnable() {

					@Override
					public void run() {
					//	contacters.clear();
					//	contacters.addAll(cs);
						removeBlackAndWhite();
						adapter.notifyDataSetChanged();
					}
				};
				runOnUiThread(r2);
			}
		};
		new Thread(r).start();
	}

	private void onConfirm() {
		List<Contacter> sels = adapter.getSels();
		if (sels.isEmpty()) {
			Toast.makeText(this, getString(R.string.notify_sel_one),
					Toast.LENGTH_LONG).show();
			return;
		}
		String type = getIntent().getStringExtra("type");
		if ("black".equals(type)) {
			addBlack(sels);
		} else {
			addWhite(sels);
		}
	}

	private void addBlack(List<Contacter> sels) {
		for (Contacter contacter : sels)
			BlackListActivity.switchType(this, sel_call.isChecked(),
					sel_sms.isChecked(), contacter.name, contacter.mobile,
					false);
		Toast.makeText(this, getString(R.string.notify_add_black_ok),
				Toast.LENGTH_LONG).show();
		finishDelay();
	}

	private void addWhite(List<Contacter> sels) {
		for (Contacter contacter : sels)
			WhiteListActivity.switchType(this, contacter.name, contacter.mobile,
					false);
		Toast.makeText(this, getString(R.string.notify_add_white_ok),
				Toast.LENGTH_LONG).show();
		finishDelay();
	}
	
	private void finishDelay() {
		confirm_btn.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				finish();
			}
		}, 500);
	}
	
	private void removeBlackAndWhite() {
		BlackListDBHelper blackListDB = BlackListDBHelper.getInstance(FirewallApplication.CONTEXT);
		WhiteListDBHelper whiteListDB = WhiteListDBHelper.getInstance(FirewallApplication.CONTEXT);;
		List<BlackUser> blackUsers = blackListDB.getAllUsers();
		List<WhiteUser> whiteusers = whiteListDB.getAllUsers();
		Map<String, Contacter> contacts = new HashMap<String, Contacter>();
		for(Contacter ctc : cs) {
			if (!contacts.containsKey(ctc.mobile))
				contacts.put(ctc.mobile, ctc);
		}
		//open this if need remove black and whites
		/*for (BlackUser usr : blackUsers) {
			contacts.remove(usr.getMobile());
		}
		for (WhiteUser usr : whiteusers) {
			contacts.remove(usr.getMobile());
		}*/
		cs.clear();
		cs.addAll(contacts.values());
		Contacter.sortReverse(cs);
		contacters.clear();
		contacters.addAll(cs);
	}

}
