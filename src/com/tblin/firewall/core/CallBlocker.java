package com.tblin.firewall.core;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

import com.tblin.firewall.CallEnder;
import com.tblin.firewall.ContactInformationGetter;
import com.tblin.firewall.Logger;

public class CallBlocker implements Blocker {

	private static final String TAG = CallBlocker.class.toString();

	/**
	 * "context":Context(context) "type"-int(type) "number"-String(block number)
	 */
	@Override
	public void block(Map<String, Object> data) {
		Context context = (Context) data.get("context");
		final String phone = (String) data.get("number");
		int type = (Integer) data.get("type");
		final AudioManager mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		final int origin = mAudioManager.getRingerMode();
		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		switch (type) {
		case Decider.CALL_BLOCK_SILENCE:
			BroadcastReceiver receiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String state = intent
							.getStringExtra(TelephonyManager.EXTRA_STATE);
					String number = intent
							.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
					Logger.i(TAG, "kill or be killed number: " + number);
					if ((state
							.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK) || state
							.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE))
							&& phone.equals(number)) {
						mAudioManager.setRingerMode(origin);
						context.unregisterReceiver(this);
					}
				}
			};
			IntentFilter intentFilter = new IntentFilter(
					"android.intent.action.PHONE_STATE");
			context.registerReceiver(receiver, intentFilter);
			break;
		case Decider.CALL_BLOCK_END:
			endCall(context, phone);
			mAudioManager.setRingerMode(origin);
			break;
		case Decider.WHITE_DEFULAT_BLOCK_TYPE:
			endCall(context, phone);
			mAudioManager.setRingerMode(origin);
			break;
		}
	}

	private void endCall(Context context, String number) {
		Logger.d(TAG, "time end call:" + System.currentTimeMillis());
		try {
			CallEnder.endCall(context);
		} catch (RemoteException e) {
			Logger.e(TAG, e == null ? "NullPointException" : e.getMessage());
		}
		deleteIncomingRecord(context, number);
	}

	private void deleteIncomingRecord(final Context context, final String number) {
		// 延迟3秒去删除这个联系人最近的来电记录，因为手机记录来电也要花一点时间
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {

				}
				ContactInformationGetter.deleteLastCallRecord(context, number);
			}
		}).start();
	}

}
