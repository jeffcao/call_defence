package com.tblin.HandsetGuardant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class CallHandler {

	private Context mContext;
	public static final int BLOCK_KILL = 1;
	public static final int BLOCK_SILIENCE = 2;
	public static final String NOTIFICATION_TAG = "notification_tag";
	private static final String TAG = CallHandler.class.toString();
	public static final int ISCALL=0;
	public static final int ISSMS=1;
	public String  shortNum=null;
	private AppBlocker ker;
	public CallHandler(Context context) {
		mContext = context;
		ker=AppBlocker.getInstance(context);
	}

	public void handleIncomingCall(String number,String content) {
		final Map<String, Object> sms=new HashMap<String, Object>();
		 sms.put("content", content);
			if(content==null){
				ker.record(number);
			}else{
			ker.record(number,sms);
			}
	}
	
	public  boolean isInBlackList(String number) {
		if (needBlockByNative(number)) {
			return true;
		}
		BlackListDBHelper db = BlackListDBHelper.getInstance(mContext);
		
		List<BlackUser> blackUsers = db.getAllUsers();
		List<String> blackNumbers = new ArrayList<String>();
		for (BlackUser u : blackUsers) {
			blackNumbers.add(u.getMobile());
		}
		shortNum=PhoneNumberHelper.gettype(number, blackNumbers);
	
		return PhoneNumberHelper.isPrefixMatch(number, blackNumbers);
	}
	public String getShortNum(String number){
			BlackListDBHelper db = BlackListDBHelper.getInstance(mContext);
		
		List<BlackUser> blackUsers = db.getAllUsers();
		List<String> blackNumbers = new ArrayList<String>();
		for (BlackUser u : blackUsers) {
			blackNumbers.add(u.getMobile());
		}
		shortNum=PhoneNumberHelper.gettype(number, blackNumbers);
		return shortNum;
	}
	private boolean needBlockByNative(String number) {
		SettingPreferenceHandler sph = SettingPreferenceHandler.getInstance();
		Logger.i(TAG, "sph.getBlockNative() :" + sph.getBlockNative());
		if (sph.getBlockNative()) {
			Logger.i(TAG, "number :" + number);
			if (!number.startsWith("+")) {
				Logger.i(TAG, "return true");
				return true;
			}
		}
		return false;
	}

}
