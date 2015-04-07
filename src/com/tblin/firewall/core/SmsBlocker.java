package com.tblin.firewall.core;

import java.util.Map;

import android.content.BroadcastReceiver;

public class SmsBlocker implements Blocker {

	// private static final String TAG = SmsBlocker.class.toString();

	@Override
	public void block(Map<String, Object> data) {
		BroadcastReceiver receiver = (BroadcastReceiver) data.get("receiver");
		receiver.abortBroadcast();
	}

}
