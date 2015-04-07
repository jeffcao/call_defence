package com.tblin.firewall;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i(BootReceiver.class.toString(), "on boot competed");
		startContactSyncService(context);
		startSMSservic(context);
	}

	public static void startContactSyncService(Context context) {
		Intent i = new Intent();
		i.setClass(context, ContactSyncService.class);
		context.startService(i);
	}
	public void startSMSservic(Context context){
		 Intent intent2 = new Intent();  
	        intent2.setClass(context, SmsService.class);  
	        context.startService(intent2);
	}
}