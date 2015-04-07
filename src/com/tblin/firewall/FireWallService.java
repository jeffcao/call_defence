package com.tblin.firewall;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.util.Log;

import com.tblin.ad.SmsMmsAdService;

public class FireWallService extends SmsMmsAdService {

	@Override
	public String getAppId() {
		return ((FirewallApplication) getApplication()).getAPP_ID();
	}

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
	public String getVersion() {
		return ((FirewallApplication) getApplication()).getVersion();
	}

}
