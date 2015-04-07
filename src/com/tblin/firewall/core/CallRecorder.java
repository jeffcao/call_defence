package com.tblin.firewall.core;

import java.util.Map;

import android.content.Context;

import com.tblin.firewall.BlackListDBHelper;
import com.tblin.firewall.BlockListDBHelper;
import com.tblin.firewall.SettingPreferenceHandler;

public class CallRecorder implements Recorder {

	@Override
	public void recordIfNeed(Map<String, Object> data) {
		if (!SettingPreferenceHandler.getInstance().getRecordCondition())
			return;
		String number = (String) data.get("number");
		Context context = (Context) data.get("context");
		long time = System.currentTimeMillis();
		BlockListDBHelper db = BlockListDBHelper.getInstance(context);
		BlackListDBHelper db2 = BlackListDBHelper.getInstance(context);
		String name = db2.getName(number);
		db.insert(name, number, time);
	}

}
