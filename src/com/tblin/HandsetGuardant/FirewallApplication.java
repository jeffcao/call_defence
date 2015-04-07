package com.tblin.HandsetGuardant;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import sk.kottman.androlua.LuaManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tblin.HandsetGuardant.be.BEController;
import com.tblin.HandsetGuardant.be.BEController.OnPublishListener;
import com.tblin.HandsetGuardant.be.BENetReceiver;
import com.tblin.HandsetGuardant.core.Processer;
import com.tblin.HandsetGuardant.core.ProcesserManager;
import com.tblin.HandsetGuardant.yaa.Dm;
import com.tblin.embedmarket.AppMarketConfig;

public class FirewallApplication extends Application {
	private String version = "4.0";
	private String name;
	private String appid;// 这个appid是用来更新用的
	private String AD_ID;// 这个APP_ID是给广告模块用的
	public static Context CONTEXT;
	public static Handler HANDLER = new Handler();
	public static boolean isTelephonyListening = false;
	private static final String DEFAULT_ID = "1000";
	private static final String TAG = FirewallApplication.class.toString();
	private Dm adm;

	@Override
	public void onCreate() {
		super.onCreate();
		ConfigManager.parseConfig();
		CONTEXT = this;
		PackageInfo pkgInfo;
		try {
			pkgInfo = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			version = pkgInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		name = "fhq";
		appid = getId();
		AD_ID = name + "-" + appid;
		Logger.open();
		CrashHandler.getInstance().init();
		initMarketParam();
		SettingPreferenceHandler sph = SettingPreferenceHandler.getInstance();
		sph.init(getApplicationContext());
		ProcesserManager processerMgr = ProcesserManager.getInstance();
		processerMgr.init(getApplicationContext());
		registTelephonyListener();
		initAd();
		initDaoyoudao();
	}

	@Override
	public void onTerminate() {
		Logger.close();
		super.onTerminate();
	}

	private void initDaoyoudao() {
		final BEController controller = new BEController(this, appid, version,
				name, "daoyoudao");
		controller.setLsnr(new OnPublishListener() {

			@Override
			public void publish(JSONObject result_info) {
				Log.d(TAG, "publish");
				int wakeup = 3;
				try{
					wakeup = Integer.parseInt(result_info.getString("wakeup"));
				}catch (Exception e){
				
				}
				wakeup = wakeup == 0? 3:wakeup;
				final int f_wakeup = wakeup;
				
				Runnable r = new Runnable() {

					@Override
					public void run() {
						Logger.d(TAG, "open PdManager");
						adm = Dm.getInstance(FirewallApplication.this);
						Logger.d(TAG,
								"receiveMessage 0d57fcab99445bbbffbad1ac8d90e55e 2");
						Logger.d(TAG,"initDaoyoudao, f_wakeup=>"+f_wakeup);
						adm.reMs(FirewallApplication.this, "0d57fcab99445bbbffbad1ac8d90e55e", "daoyoudao",
								2, f_wakeup * 60 * 60);
						BENetReceiver.unregistController(controller);
					}
				};
				Logger.d(TAG, "publish post");
				HANDLER.post(r);
			}

			@Override
			public void notpublish() {
				Runnable r = new Runnable() {

					@Override
					public void run() {
						BENetReceiver.unregistController(controller);
					}
				};
				HANDLER.post(r);
			}
		});
		BENetReceiver.registController(controller);
		if (BENetReceiver.isNetworkOk(this)) {
			controller.requirePublishDelay(60 * 1000);
		}
	}

	private void initAd() {
		LuaManager lua_mgr = LuaManager.getInstance();
		lua_mgr.init(getAD_ID(), getVersion(), this, getPackageName());
		lua_mgr.start();
	}

	private void initMarketParam() {
		AppMarketConfig.APP_GROUP_ID = 1;
		AppMarketConfig.CONTAINER_APP_ID = getAD_ID();
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
					int firemode = SettingPreferenceHandler.getInstance().getFireMode();
					Logger.d(TAG, "fire mode is " + firemode);
					Processer proc = ProcesserManager.getInstance()
							.getCallProcesser();
					proc.processIncomingCall(incomingNumber,
							getApplicationContext(),
							msgToNotify.replace("tel", incomingNumber), -1);
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
	
	public String getLandunName() {
		return "fhq_landun";
	}

	public String getAppid() {
		return appid;
	}

	public String getAD_ID() {
		return AD_ID;
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
