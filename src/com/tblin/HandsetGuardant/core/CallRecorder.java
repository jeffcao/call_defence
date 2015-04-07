package com.tblin.HandsetGuardant.core;

import java.util.Map;

import android.content.Context;

import com.tblin.HandsetGuardant.BlackListDBHelper;
import com.tblin.HandsetGuardant.BlockListDBHelper;
import com.tblin.HandsetGuardant.DisplayCallService;
import com.tblin.HandsetGuardant.DisplayCallUitl;
import com.tblin.HandsetGuardant.SettingPreferenceHandler;

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
		
		  String num=DisplayCallUitl.proNumber(number);
		  String str= DisplayCallService.getInCommingNUm(num);
		db.insert(name, number, time,str);
	}

}
