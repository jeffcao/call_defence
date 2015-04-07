package com.tblin.HandsetGuardant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FireWallService extends Service {

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent == null) {
			return;
		}
		 
		final String number = intent.getStringExtra("number");
		final String content = intent.getStringExtra("content");
		
		
		if (null != number && !"".equals(number)) {
			Runnable r = new Runnable() {

				@Override
				public void run() {
					CallHandler handler = new CallHandler(
							getApplicationContext());
					
					handler.handleIncomingCall(number,content);
				}
			};
			new Thread(r).start();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
