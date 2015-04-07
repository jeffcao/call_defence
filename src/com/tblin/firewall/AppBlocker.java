package com.tblin.firewall;

import java.util.Map;

import android.content.Context;

public class AppBlocker implements Blocker {
	public static final String NOTIFICATION_TAG = "notification_tag";
	public static final int BLOCK_TYPE_UNKNOW = -1;
	public static final int BLOCK_TYPE_BLOCK_BLACK_LIST = 1;
	public static final int BLOCK_TYPE_BLOCK_NOT_IN_CONTACTER = 2;
	public static final int BLOCK_TYPE_BLOCK_NOT_BLOCK = 3;
	public static final int BLOCK_KILL = 1;
	public static final int BLOCK_SILIENCE = 2;
	private static AppBlocker APP;
	private Context mContext;
	
	private AppBlocker(Context context) {
		//
		mContext = context;
	
	}

 		
	public static AppBlocker getInstance(Context context) {
		if (APP == null) {
			APP = new AppBlocker(context);
		}
		return APP;
	}
	
	@Override
	public boolean needBlock(String number) {
		
	
		return true;
	}

	@Override
	public void block(String number) {
		
		
	}

	@Override
	public boolean needRecord(String number) {
		// TODO Auto-generated method stub
		return SettingPreferenceHandler.getInstance().getRecordCondition();
	}

	@Override
	public void record(String number) {
		long time = System.currentTimeMillis();
		BlockListDBHelper db = BlockListDBHelper.getInstance(mContext);
		BlackListDBHelper db2 = BlackListDBHelper.getInstance(mContext);
		String name = db2.getName(number);
		db.insertCall(name, number, time);
		Logger.i("AppBlocker", "insertCall------->name="+name+"***number="+number+"***time="+time+"***");
		
	}

	@Override
	public void record(String number, Map<String, Object> infos) {
		long time = System.currentTimeMillis();
		BlockListDBHelper db = BlockListDBHelper.getInstance(mContext);
		BlackListDBHelper db2 = BlackListDBHelper.getInstance(mContext);
		String name = db2.getName(number);
		String content=(String) infos.get("content");
		db.insertSMS(name, number, time,  content);
		Logger.i("AppBlocker", "insertSMS------->name="+name+"***number="+number+"***time="+time+"***content="+content);
		
	}

	@Override
	public boolean needNotification() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void notification(Context context, Map<String, Object> infos) {
		
		
	}
	
}
