package com.tblin.HandsetGuardant.core;

import java.util.Map;

import android.content.Context;

import com.tblin.HandsetGuardant.BlackListDBHelper;
import com.tblin.HandsetGuardant.BlockListDBHelper;
import com.tblin.HandsetGuardant.DisplayCallService;
import com.tblin.HandsetGuardant.DisplayCallUitl;
import com.tblin.HandsetGuardant.Logger;
import com.tblin.HandsetGuardant.SettingPreferenceHandler;

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
		 String num=DisplayCallUitl.proNumber(number);
		  String str= DisplayCallService.getInCommingNUm(num);
		db.insertSMS(name, number, time, content,str);
		Logger.i("AppBlocker", "insertSMS------->name=" + name + "***number="
				+ number + "***time=" + time + "***content=" + content+"***str=" + str);
	}

}
