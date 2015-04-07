package com.tblin.firewall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tblin.firewall.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallHandler {

	private Context mContext;
	public static final int BLOCK_TYPE_UNKNOW = -1;
	public static final int BLOCK_TYPE_BLOCK_BLACK_LIST = 1;
	public static final int BLOCK_TYPE_BLOCK_NOT_IN_CONTACTER = 2;
	public static final int BLOCK_TYPE_BLOCK_NOT_BLOCK = 3;
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
	private boolean needRecord() {
		return SettingPreferenceHandler.getInstance().getRecordCondition();
	}

	private boolean needBlock(String number, int blockType) {
		boolean result = false;
		switch (blockType) {
		case BLOCK_TYPE_BLOCK_BLACK_LIST:
			result = isInBlackList(number);
		
			break;

		case BLOCK_TYPE_BLOCK_NOT_IN_CONTACTER:
			result = isOutOfContacter(number);
			break;

		case BLOCK_TYPE_BLOCK_NOT_BLOCK:
			break;

		case BLOCK_TYPE_UNKNOW:
			break;
		}
	
		return result;
	}

	private void block(String number, int blockType,boolean issms,boolean iscall) {
		final AudioManager mAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
		final int origin = mAudioManager.getRingerMode();
		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		
		final String phone = number;
		
		if (needEndCall(number, blockType)) {
			try {
				CallEnder.endCall(mContext);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mAudioManager.setRingerMode(origin);
		} else {
			BroadcastReceiver receiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String state = intent
							.getStringExtra(TelephonyManager.EXTRA_STATE);
					String number = intent
							.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
					Logger.i(TAG, "kill or be killed number: " + number);
					if ((state
							.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK) || state
							.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE))
							&& phone.equals(number)) {
						mAudioManager.setRingerMode(origin);
						mContext.unregisterReceiver(this);
					}
				}
			};
			IntentFilter intentFilter = new IntentFilter(
					"android.intent.action.PHONE_STATE");
			mContext.registerReceiver(receiver, intentFilter);
		}
		
		notifyUserIfNeeded(number,issms,iscall);
		
	}

	private boolean needEndCall(String number, int blockType) {
		boolean need = false;
		switch (blockType) {
		case BLOCK_TYPE_BLOCK_BLACK_LIST:
			BlackListDBHelper db = BlackListDBHelper.getInstance(mContext);
			if (needBlockByNative(number)) {
				int k = db.queryCall(SettingPreferenceHandler.getInstance()
						.getNativePrefix()) ;
				  if(k==801 || k==800 ){
					  need=true;
					 
				  }
			} else {
				int n = db.queryCall(number) ;
				 if(n==801 || n==800 ){
					  need=true;
					 
				  }
			}
			
			break;

		case BLOCK_TYPE_BLOCK_NOT_IN_CONTACTER:
			need = true;
			break;

		case BLOCK_TYPE_BLOCK_NOT_BLOCK:
			break;

		case BLOCK_TYPE_UNKNOW:
			break;
		}
		return need;
	}
	

	private void record(String number,int type,int call) {
		long time = System.currentTimeMillis();
		BlockListDBHelper db = BlockListDBHelper.getInstance(mContext);
		BlackListDBHelper db2 = BlackListDBHelper.getInstance(mContext);
		String name = db2.getName(number);
	//	db.insert(name, number, time,type,call,null);
	}
	private void recordSms(String number,int type,int sms,String content){
		long time = System.currentTimeMillis();
		BlockListDBHelper db = BlockListDBHelper.getInstance(mContext);
		BlackListDBHelper db2 = BlackListDBHelper.getInstance(mContext);
		String name = db2.getName(number);
		
	//	db.insert(name, number, time,type,sms,content);
		
	}

	private boolean isOutOfContacter(String number) {
		ContacterDBHelper db = ContacterDBHelper.getInstance(mContext);
		return !db.isMobileExist(number);
	}

	private boolean isInBlackList(String number) {
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

	private int blockType() {
		return SettingPreferenceHandler.getInstance().getFireMode();
	}

	private void notifyUserIfNeeded(String number,boolean issms,boolean iscall) {
		if (isNeedToNotifyUser()) {
			 BlackListDBHelper db = BlackListDBHelper.getInstance(mContext);
			 int k=db.querySms(number);
			 switch (k) {
			case 800:
				if(issms){
					postToNotification("(" + number + ")短信已被拦截");
				}else{
					postToNotification("(" + number + ")来电已被拦截");
				}
				
				break;

			case 801:
				if(iscall){
				postToNotification("(" + number + ")来电已被拦截");
				}
				break;
			case 810:
				if(issms){
				postToNotification("(" + number + ")短信已被拦截");
				}
				break;
			
			}
			
			
		}
	}

	private boolean isNeedToNotifyUser() {
		return SettingPreferenceHandler.getInstance().getToastWhenBlock();
	}

	private void postToNotification(String message) {
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService("notification");
		Notification notification = new Notification();
		notification.icon = R.drawable.logo;
		notification.tickerText = message;
		notification.defaults = Notification.DEFAULT_SOUND;
		Intent intent = new Intent();
		intent.putExtra(NOTIFICATION_TAG, true);
		intent.setClass(mContext, MainTabActivity.class);
		PendingIntent m_PendingIntent = PendingIntent.getActivity(mContext, 0,
				intent, 0);
		notification.setLatestEventInfo(mContext, "来电卫士", message,
				m_PendingIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}
	
}
