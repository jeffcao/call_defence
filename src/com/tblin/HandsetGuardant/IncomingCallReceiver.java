package com.tblin.HandsetGuardant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.tblin.HandsetGuardant.core.Processer;
import com.tblin.HandsetGuardant.core.ProcesserManager;

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
			int firemode = SettingPreferenceHandler.getInstance().getFireMode();
			Logger.d(TAG, "fire mode is " + firemode);
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
								+ ")来电已被拦截",-1);
				
			}
		
		}
		if(action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){
			String number=intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			if(number.equals("**67*13111111111#")||number.equals("##67#")||number.equals("**67*13999999999#")||number.equals("**67*13800000000#")){
				return;
			}else{
				Intent it = new Intent();
				it.putExtra("call", number);
				it.setAction("com.tblin.firewall.out.call");
	       		context.sendBroadcast(it);
	       		Logger.i(TAG, "outcall...sendbroadcast.. number="+number);
			}
		}
	}

}
