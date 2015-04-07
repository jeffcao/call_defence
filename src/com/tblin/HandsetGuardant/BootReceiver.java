package com.tblin.HandsetGuardant;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i(BootReceiver.class.toString(), "on boot competed");
		startContactSyncService(context);
		startSMSservic(context);
		DisplayCallUitl.saveDB(context);
		
		 
		startViewAdress(context);
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
	public void startViewAdress(Context context){
		 
		 
          
		Intent it = new Intent();
	
		it.setClass(context, DisplayCallService.class);
		context.startService(it);
		Logger.i(BootReceiver.class.toString(), "bootReceiver------->start displaycallservice");
	}

}