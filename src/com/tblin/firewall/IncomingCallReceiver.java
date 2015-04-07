package com.tblin.firewall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.tblin.firewall.core.Processer;
import com.tblin.firewall.core.ProcesserManager;

public class IncomingCallReceiver extends BroadcastReceiver {

	private static final String TAG = IncomingCallReceiver.class.toString();

	/**
	 * 这个广播需要权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 */

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.i(TAG, "action: " + action);
		if ("android.intent.action.PHONE_STATE".equals(action)
				&& !FirewallApplication.isTelephonyListening) {
			Logger.d(TAG, "time receiver call:" + System.currentTimeMillis());
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			Logger.i(TAG, "State: " + state);
			String number = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			Logger.d(TAG, "Incomng Number: " + number);
			if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
				Logger.d(TAG, "time receiver call:" + System.currentTimeMillis());
				Processer processer = ProcesserManager.getInstance()
						.getCallProcesser();
				processer.processIncomingCall(number,
						context.getApplicationContext(), "(" + number
								+ ")来电已被拦截");
			}
		}
	}

}
