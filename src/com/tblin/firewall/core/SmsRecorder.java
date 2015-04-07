package com.tblin.firewall.core;

import java.util.Map;

import android.content.Context;

import com.tblin.firewall.BlackListDBHelper;
import com.tblin.firewall.BlockListDBHelper;
import com.tblin.firewall.Logger;
import com.tblin.firewall.SettingPreferenceHandler;

public class SmsRecorder implements Recorder {

	@Override
	public void recordIfNeed(Map<String, Object> data) {
		if (!SettingPreferenceHandler.getInstance().getRecordCondition())
			return;
		Context context = (Context) data.get("context");
		String number = (String) data.get("number");
		String content = (String) data.get("content");
		long time = System.currentTimeMillis();
		BlockListDBHelper db = BlockListDBHelper.getInstance(context);
		BlackListDBHelper db2 = BlackListDBHelper.getInstance(context);
		String name = db2.getName(number);
		db.insertSMS(name, number, time, content);
		Logger.i("AppBlocker", "insertSMS------->name=" + name + "***number="
				+ number + "***time=" + time + "***content=" + content);
	}

}
