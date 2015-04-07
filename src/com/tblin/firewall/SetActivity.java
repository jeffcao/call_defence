package com.tblin.firewall;

import com.tblin.firewall.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class SetActivity extends Activity {

	private SettingPreferenceHandler sph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.set_main);
		init();
	}

	private void init() {
		sph = SettingPreferenceHandler.getInstance();
		initFireMode();
		initBlockType();
		initBlockToast();
		initBlockRecord();
	}

	private void initBlockRecord() {
		LinearLayout recordLinear = (LinearLayout) findViewById(R.id.set_main_block_record_linear);
		final CheckBox recordCheck = (CheckBox) findViewById(R.id.set_main_block_record);
		recordLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				recordCheck.setChecked(!recordCheck.isChecked());
				sph.setRecord(recordCheck.isChecked());
			}
		});
		recordCheck.setChecked(sph.getRecordCondition());
	}

	private void initBlockToast() {
		LinearLayout toastLinear = (LinearLayout) findViewById(R.id.set_main_block_toast_linear);
		final CheckBox toastCheck = (CheckBox) findViewById(R.id.set_main_block_toast);
		toastLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toastCheck.setChecked(!toastCheck.isChecked());
				sph.setToastWhenBlock(toastCheck.isChecked());
			}
		});
		toastCheck.setChecked(sph.getToastWhenBlock());
	}

	private void initFireMode() {
		RelativeLayout blackLinear = (RelativeLayout) findViewById(R.id.set_main_black_linear);
		RelativeLayout contactLinear = (RelativeLayout) findViewById(R.id.set_main_contact_linear);
		RelativeLayout allLinear = (RelativeLayout) findViewById(R.id.set_main_all_linear);
		RelativeLayout[] linears = { blackLinear, contactLinear, allLinear };
		RadioButton black = (RadioButton) findViewById(R.id.set_main_fire_mode_black);
		RadioButton contact = (RadioButton) findViewById(R.id.set_main_fire_mode_contact);
		RadioButton all = (RadioButton) findViewById(R.id.set_main_fire_mode_all);
		final RadioButton[] radios = { black, contact, all };
		for (int i = 0; i < linears.length; i++) {
			final int ii = i;
			linears[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int j = 0; j < radios.length; j++) {
						radios[j].setChecked(j == ii);
					}
					sph.setFireMode(ii + 1);
				}
			});
		}
		int checked = sph.getFireMode();
		for (int i = 0; i < radios.length; i++) {
			radios[i].setChecked(i == (checked - 1));
		}
	}

	private void initBlockType() {
		RelativeLayout busyLinear = (RelativeLayout) findViewById(R.id.set_main_busy_linear);
		RelativeLayout closeLinear = (RelativeLayout) findViewById(R.id.set_main_close_linear);
		RelativeLayout numberLinear = (RelativeLayout) findViewById(R.id.set_main_number_linear);
		RelativeLayout dieLinear = (RelativeLayout) findViewById(R.id.set_main_die_linear);
		RelativeLayout[] linears = { busyLinear, closeLinear, numberLinear,
				dieLinear };
		RadioButton busy = (RadioButton) findViewById(R.id.set_main_busy_busy);
		RadioButton close = (RadioButton) findViewById(R.id.set_main_busy_close);
		RadioButton number = (RadioButton) findViewById(R.id.set_main_busy_number);
		RadioButton die = (RadioButton) findViewById(R.id.set_main_busy_die);
		final RadioButton[] radios = { busy, close, number, die };
		final int[] type = { BusySetter.TYPE_NORMAL,
				BusySetter.TYPE_MOBILE_CLOSE, BusySetter.TYPE_ERROR_NUMBER,
				BusySetter.TYPE_SIM_DIE };
		for (int i = 0; i < linears.length; i++) {
			final int ii = i;
			linears[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int j = 0; j < radios.length; j++) {
						radios[j].setChecked(j == ii);
					}
					setBlockType(type[ii], radios);
				}
			});
		}
		int checked = sph.getBlockType();
		int index = -1;
		for (int i = 0; i < type.length; i++) {
			if (checked == type[i]) {
				index = i;
				break;
			}
		}
		for (int i = 0; i < radios.length; i++) {
			radios[i].setChecked(i == index);
		}
	}

	private void setBlockType(int type, final RadioButton[] radios) {
		final int ty = type;
		YesNoDialog ynd = new YesNoDialog(SetActivity.this);
		ynd.setMessage(getResources().getString(R.string.block_type_guide));
		ynd.setYesButton("继续", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				BusySetter.setBusy(ty, SetActivity.this);
				sph.setBlockType(ty);
			}
		}, YesNoDialog.AUTO_DISMISS);
		ynd.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				refreshBlockType(radios);
			}
		});
		ynd.show();
	}

	private void refreshBlockType(RadioButton[] radios) {
		int type = sph.getBlockType();
		int[] types = { BusySetter.TYPE_NORMAL, BusySetter.TYPE_MOBILE_CLOSE,
				BusySetter.TYPE_ERROR_NUMBER, BusySetter.TYPE_SIM_DIE };
		for (int i = 0; i < radios.length && i < types.length; i++) {
			if (types[i] == type) {
				radios[i].setChecked(true);
			} else {
				radios[i].setChecked(false);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		init();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
