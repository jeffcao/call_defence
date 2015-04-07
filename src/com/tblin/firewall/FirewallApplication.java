package com.tblin.firewall;

import java.io.IOException;
import java.io.InputStream;

import android.app.Application;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.tblin.ad.AdLogger;
import com.tblin.ad.AdLogger.AdLog;
import com.tblin.embedmarket.AppMarketConfig;
import com.tblin.firewall.core.Processer;
import com.tblin.firewall.core.ProcesserManager;

public class FirewallApplication extends Application {
	private String version;
	private String name;
	private String appid;// 这个appid是用来更新用的
	private String APP_ID;// 这个APP_ID是给广告模块用的
	public static boolean isTelephonyListening = false;
	private static final String DEFAULT_ID = "1000";
	private static final String TAG = FirewallApplication.class.toString();

	@Override
	public void onCreate() {
		super.onCreate();
		ConfigManager.parseConfig();
		version = "3.0";
		name = "fhq";
		appid = getId();
		APP_ID = name + "-" + appid;
		Logger.open();
		CrashHandler.getInstance().init();
		initMarketParam();
		SettingPreferenceHandler sph = SettingPreferenceHandler.getInstance();
		sph.init(getApplicationContext());
		ProcesserManager processerMgr = ProcesserManager.getInstance();
		processerMgr.init(getApplicationContext());
		registTelephonyListener();
		initAd();
	}

	@Override
	public void onTerminate() {
		Logger.close();
		super.onTerminate();
	}
	
	private void initAd() {
		AdLogger.setLogger(new AdLog() {
			
			@Override
			public void w(String arg0, String arg1) {
				Logger.w(arg0, arg1);
			}
			
			@Override
			public void i(String arg0, String arg1) {
				Logger.i(arg0, arg1);
			}
			
			@Override
			public void e(String arg0, String arg1) {
				Logger.e(arg0, arg1);
			}
			
			@Override
			public void d(String arg0, String arg1) {
				Logger.d(arg0, arg1);
			}
		});
	}

	private void initMarketParam() {
		AppMarketConfig.APP_GROUP_ID = 1;
		AppMarketConfig.CONTAINER_APP_ID = getAPP_ID();
		AppMarketConfig.CONTAINER_APP_VERSION = getVersion();
	}

	private void registTelephonyListener() {
		TelephonyManager mTelephonyMgr = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyMgr.listen(new PhoneStateListener() {

			String msgToNotify = "(tel)来电已拦截";

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					Logger.d(TAG,
							"time receiver call:" + System.currentTimeMillis());
					Processer proc = ProcesserManager.getInstance()
							.getCallProcesser();
					proc.processIncomingCall(incomingNumber,
							getApplicationContext(),
							msgToNotify.replace("tel", incomingNumber));
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					Logger.i(TAG, "CALL_STATE_OFFHOOK");
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
		isTelephonyListening = true;
	}

	public String getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}

	public String getAppid() {
		return appid;
	}

	public String getAPP_ID() {
		return APP_ID;
	}

	private String getId() {
		InputStream is = null;
		String result = DEFAULT_ID;
		try {
			is = getResources().openRawResource(R.raw.appid);
			byte[] buffer = new byte[100];
			int length = is.read(buffer);
			if (length > 0) {
				String id = new String(buffer, 0, length);
				result = id != null ? id : result;
				Logger.i(TAG, "app id is: " + result);
			} else {
				Logger.w(TAG, "app.txt is null");
			}
		} catch (IOException e) {
			Logger.e(TAG, e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Logger.e(TAG, e.getMessage());
				}
			}
		}
		return result;
	}
}
