package com.tblin.HandsetGuardant;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

import com.tblin.HandsetGuardant.phoneattribute.AskDownloadDialog;
import com.tblin.HandsetGuardant.phoneattribute.AskUpdateDialog;
import com.tblin.HandsetGuardant.phoneattribute.PhoneDataDownloader;
import com.tblin.HandsetGuardant.phoneattribute.PhoneDataDownloader.DownloadListener;
import com.tblin.HandsetGuardant.phoneattribute.PhoneDataDownloader.ErrorType;
import com.tblin.HandsetGuardant.phoneattribute.PhoneDataDownloader.PercentDownloadListener;

public class SetActivity extends Activity {
	private static final String TAG = SetActivity.class.toString();
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
		initSmsFireMode();
		initBlockType();
		initBlockToast();
		initBlockRecord();
		initViewAdd();
		initblockharass();
		initKeyWord();
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
	
	private DownloadListener downloadPhonedbLsnr;
	private void initViewAdd() {
		
		RelativeLayout recordLinear = (RelativeLayout) findViewById(R.id.set_main_block_view_call);
		final CheckBox Check = (CheckBox) findViewById(R.id.set_main_block_view_call_ck);
		final PhoneDataDownloader downloader = PhoneDataDownloader.getInstance();
		downloader.unregistListener(downloadPhonedbLsnr);
		downloadPhonedbLsnr = new PercentDownloadListener() {
			
			@Override
			public void onSuccess() {
				downloader.unregistListener(this);
				String sucStr = getString(R.string.download_success);
				ToastUtil.toast(sucStr);
				Runnable r = new Runnable() {
					
					@Override
					public void run() {
						Check.setChecked(true);
						Editor date = getSharedPreferences("addrees", 0).edit();
						date.putBoolean("add", Check.isChecked());
						date.commit();
						DisplayCallUitl.saveDB(getApplicationContext());
					}
				};
				runOnUiThread(r);
			}
			
			@Override
			public void onStart() {}
			
			@Override
			public void onFail(ErrorType err) {
				int resource = R.string.download_other;
				switch (err) {
				case NET_INTERRUPT:
					resource = R.string.download_net_interrupt;
					break;
				case NO_SDCARD:
					resource = R.string.download_no_sdcard;
					break;
				case NO_NET:
					resource = R.string.download_no_net;
					break;
				case SDCARD_SPACE_UNAVAILABLE:
					resource = R.string.download_sdcard_space_unavailable;
					break;
				}
				String failStr = getString(resource);
				ToastUtil.toast(failStr);
			}
			
			@Override
			public void onDowloadingPercent(int percent) {}
			
		};
		downloader.registListener(downloadPhonedbLsnr);
		SharedPreferences date = getSharedPreferences(
				"addrees", 0);
		boolean k=date.getBoolean("add", false);
		recordLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Check.isChecked()) {
					setCheck(Check);
					return;
				}
				if (DisplayCallUitl.checkIsPhoneDbExist()) {
					setCheck(Check);
					return;
				}
				if (downloader.hasInited() && downloader.isDownloading()) {
					int progress = downloader.getDownloadPercent();
					if (progress >= 0) {
						String pgStr = getString(R.string.progress);
						pgStr = pgStr.replace("progress", Integer.toString(progress));
						ToastUtil.toast(pgStr);
					}
					return;
				}
				if (DisplayCallUitl.isPhoneDbVersionOne()) {
					//this tag represent that ask download dialog had showed
					//if exist, remove it, if not exist, set it and show ask 
					//download dialog
					Object tag = v.getTag();
					if (null == tag) {
						v.setTag(new Object());
						AskUpdateDialog dialog = new AskUpdateDialog(v.getContext());
						dialog.show();
					} else {
						v.setTag(null);
						setCheck(Check);
					}
					return;
				}
				AskDownloadDialog dialog = new AskDownloadDialog(v.getContext());
				dialog.show();
			}

			private void setCheck(final CheckBox Check) {
				Check.setChecked(!Check.isChecked());
				
				Editor date = getSharedPreferences("addrees", 0).edit();
				date.putBoolean("add", Check.isChecked());
				date.commit();
			}
		});
		Check.setChecked(k);
	}
private void initKeyWord() {
		
		RelativeLayout recordLinear = (RelativeLayout) findViewById(R.id.set_main_block_harass);
		final CheckBox Check = (CheckBox) findViewById(R.id.set_main_block_harass_ck);
		SharedPreferences date = getSharedPreferences(
				"addrees", 0);
		boolean k=date.getBoolean("key", true);
		recordLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Check.setChecked(!Check.isChecked());
			
				Editor date = getSharedPreferences("addrees", 0).edit();
				date.putBoolean("key", Check.isChecked());
				date.commit();
				
			}
		});
		Check.setChecked(k);
		
	}
	private void initblockharass() {
		RelativeLayout setblockharass = (RelativeLayout) findViewById(R.id.set_main_block_harass);
		final CheckBox Checkharass = (CheckBox) findViewById(R.id.set_main_block_harass_ck);
		RelativeLayout setkeyword=(RelativeLayout)findViewById(R.id.set_sms_keyword);
		setkeyword.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetActivity.this,SmsKeywordActivity.class);
				startActivity(intent);
				
			}
			
		});
		setblockharass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Checkharass.setChecked(!Checkharass.isChecked());
			}
		});
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
		RelativeLayout blockAllLinear = (RelativeLayout) findViewById(R.id.set_main_block_all_linear);
		RelativeLayout onlyWhiteLinear = (RelativeLayout) findViewById(R.id.set_main_only_white_linear);
		RelativeLayout[] linears = { allLinear, blackLinear, contactLinear,  onlyWhiteLinear, blockAllLinear };
		RadioButton black = (RadioButton) findViewById(R.id.set_main_fire_mode_black);
		RadioButton contact = (RadioButton) findViewById(R.id.set_main_fire_mode_contact);
		RadioButton all = (RadioButton) findViewById(R.id.set_main_fire_mode_all);
		RadioButton blockAll = (RadioButton) findViewById(R.id.set_main_fire_mode_block_all);
		RadioButton onlyWhite = (RadioButton) findViewById(R.id.set_main_fire_mode_only_white);
		final RadioButton[] radios = {  all, black, contact, onlyWhite, blockAll };
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
		Logger.i(TAG, "setactivity==="+checked);
	}
	
	private void initSmsFireMode() {
		RelativeLayout receiveAll = (RelativeLayout) findViewById(R.id.sms_receive_all_linear);
		RelativeLayout blockBlack = (RelativeLayout) findViewById(R.id.sms_block_black_linear);
		RelativeLayout blockBlackAndStranger = (RelativeLayout) findViewById(R.id.sms_block_black_stranger_linear);
		RelativeLayout blockALl = (RelativeLayout) findViewById(R.id.sms_block_all_linear); 
		RelativeLayout blockOnlyWhite = (RelativeLayout) findViewById(R.id.sms_block_only_white_linear); 
		RelativeLayout[] linears = { receiveAll, blockBlack, blockBlackAndStranger, blockOnlyWhite, blockALl };
		RadioButton recAll = (RadioButton) findViewById(R.id.sms_receive_all_btn);
		RadioButton blkBlack = (RadioButton) findViewById(R.id.sms_block_black_btn);
		RadioButton blkBlackAndStranger = (RadioButton) findViewById(R.id.sms_block_black_stranger_btn);
		RadioButton blkALl = (RadioButton) findViewById(R.id.sms_block_all_btn);
		RadioButton blkOnlyWhite = (RadioButton) findViewById(R.id.sms_block_only_white_btn);
		final RadioButton[] radios = { recAll, blkBlack, blkBlackAndStranger, blkOnlyWhite, blkALl};
		for (int i = 0; i < linears.length; i++) {
			final int ii = i;
			linears[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int j = 0; j < radios.length; j++) {
						radios[j].setChecked(j == ii);
					}
					sph.setSmsFireMode(ii + 11);
				}
			});
		}
		int checked = sph.getSmsFireMode() - 10;
		for (int i = 0; i < radios.length; i++) {
			radios[i].setChecked(i == (checked - 1));
		}
		Logger.i(TAG, "setactivity==="+checked);
	}

	private void initBlockType() {
		LinearLayout view = (LinearLayout) findViewById(R.id.block_type);
		if (PhonePrefixHelper.getCountryPhonePrefix(this).equalsIgnoreCase("+86")) {
			view.setVisibility(View.VISIBLE);
		}
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
