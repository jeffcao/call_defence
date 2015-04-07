package com.tblin.firewall;

import java.util.Map;

import android.content.Context;

public interface Blocker {
	
	boolean needBlock(String number);
	
	void block(String number);
	
	boolean needRecord(String number);
	
	void record(String number);
	
	void record(String number, Map<String, Object> infos);
	
	boolean needNotification();
	
	/**
	 * 
	 * @param context if you want to show dialog, the context must be instance of Activity
	 * @param infos
	 */
	void notification(Context context, Map<String, Object> infos);
}

