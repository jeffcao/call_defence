package com.tblin.HandsetGuardant.be;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.webkit.URLUtil;

import com.tblin.HandsetGuardant.Logger;

/**
 * BEController跑在一个单独的非主线程 android.permission.READ_PHONE_STATE
 * android.permission.ACCESS_WIFI_STATE android.permission.INTERNET
 * 
 * @author qy
 * 
 */
public class BEController {

	private Context mContext;
	private String appid;
	private String version;
	private String softname;
	private String adtype;
	private String imeimac = "-";
	private OnPublishListener lsnr;
	private PublishController pController;
	private Thread requireThread;
	private boolean had_required = false;
	private static final String TAG = BEController.class.toString();
	public static final String BE_URL = "http://wap.cn6000.com/cm/andr/ad_c.php";

	public BEController(Context mContext, String appid, String version,
			String softname, String adtype) {
		super();
		this.mContext = mContext;
		this.appid = appid;
		this.version = version;
		this.softname = softname;
		this.adtype = adtype;
		MobileInfoInspector mii = new MobileInfoInspector();
		this.imeimac = mii.getImei() + "-" + mii.getMac();
		Logger.i(TAG,"imeimac: " + this.imeimac);
		pController = new PublishController();
	}

	public void setLsnr(OnPublishListener lsnr) {
		this.lsnr = lsnr;
	}

	private void onPublish(JSONObject result_info) {
		if (null == this.lsnr) {
			return;
		}
		this.lsnr.publish(result_info);
	}

	private void onNotPublish() {
		if (null == this.lsnr) {
			return;
		}
		this.lsnr.notpublish();
	}

	public void requirePublish() {
		requirePublishDelay(-1);
	}

	public void requirePublishDelay(final long delay) {
		if (null != requireThread) {
			Logger.d(TAG, "require is running");
			return;
		}
		if (had_required) {
			Logger.d(TAG, "had required, do not require again");
			return;
		}
		requireThread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (delay > 0) {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				pController.requirePublish();
				requireThread = null;
			}
		});
		requireThread.start();
		//pController.requirePublish();
	}

	/**
	 * 内嵌类
	 * 
	 * @author qy
	 * 
	 */
	public interface OnPublishListener {
		public void publish(JSONObject result_info);

		public void notpublish();
	}

	public enum REQUIRE_RESULT {
		PUBLISH, SLEEP, UNREACH
	};

	private class RequireResult {
		REQUIRE_RESULT result_code;
		JSONObject result_info;

		public RequireResult(REQUIRE_RESULT result_code, JSONObject result_info) {
			this.result_code = result_code;
			this.result_info = result_info;
		}

	};

	private interface PublishRequire {
		public RequireResult requirePublish();
	}

	private class NativePublishRequire implements PublishRequire {

		private SharedPreferences sp;

		public NativePublishRequire() {
			sp = mContext.getSharedPreferences("be_controller",
					Context.MODE_PRIVATE);
		}

		public void saveNext(long sleep_time, int last_result) {
			long next = System.currentTimeMillis() + sleep_time;
			sp.edit().putLong("next_time", next).commit();
			sp.edit().putInt("last_result", last_result).commit();
		}

		/**
		 * 如果之前有缓存过结果，且缓存结果还未过期，返回缓存的结果，PUBLISH 或 SLEEP
		 * 如果没有缓存结果或缓存结果已过去，返回UNREACH
		 */
		@Override
		public RequireResult requirePublish() {
			long curTime = System.currentTimeMillis();
			long expect = sp.getLong("next_time", 0);
			int last_result = sp.getInt("last_result", -100);
			Logger.i(TAG,"cur time is: " + getDateByMillis(curTime));
			Logger.i(TAG,"expect time is: " + getDateByMillis(expect));
			Logger.i(TAG,"last_result is: " + last_result);
			if (curTime <= expect && last_result != -100) {
				REQUIRE_RESULT re;
				if (last_result == 0) {
					re = REQUIRE_RESULT.PUBLISH;
				} else {
					re = REQUIRE_RESULT.SLEEP;
				}
				return new RequireResult(re, null);
			}
			return new RequireResult(REQUIRE_RESULT.UNREACH, null);
		}

		private String getDateByMillis(long millis) {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy年MM月dd日 HH时mm分ss秒 E ");
			Date date = new Date(millis);
			return sdf.format(date);
		}

	}

	private class ServerPublishRequire implements PublishRequire {

		/**
		 * 如果服务器有返回，返回服务器缓存的结果，PUBLISH 或 SLEEP 如果服务器没有返回，或出现错误，返回UNREACH
		 */
		@Override
		public RequireResult requirePublish() {
			Map<String, String> params = new HashMap<String, String>();
			params.put("appid", appid);
			params.put("version", version);
			params.put("softname", softname);
			params.put("adtype", adtype);
			params.put("imeimac", imeimac);
			for (String key :params.keySet()) {
				Logger.i(TAG,key+":"+params.get(key));
			}
			try {
				String result = PostExcuter.excutePost(BE_URL, params);
				Logger.i(TAG,"excutePost result=> "+ result);
				if (null != result) {
					JSONObject json = new JSONObject(result);
					int status = Integer.parseInt(json.getString("status"));
					
					REQUIRE_RESULT re;
					if (status == 0) {
						re = REQUIRE_RESULT.PUBLISH;
					} else {
						re = REQUIRE_RESULT.SLEEP;
					}
					return new RequireResult(re, json);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new RequireResult(REQUIRE_RESULT.UNREACH, null);
		}

	}

	private class PublishController implements PublishRequire {

		private ServerPublishRequire serverRequire;
		private NativePublishRequire nativeRequire;

		public PublishController() {
			serverRequire = new ServerPublishRequire();
			nativeRequire = new NativePublishRequire();
		}

		/**
		 * 关键的处理逻辑在这里
		 */
		@Override
		public RequireResult requirePublish() {
			RequireResult result = nativeRequire.requirePublish();
			if (result.result_code != REQUIRE_RESULT.UNREACH) {
				processResult(result, false);
				return null;
			}
			result = serverRequire.requirePublish();
			processResult(result, true);
			return null;
		}

		private void processResult(RequireResult result, boolean needSave) {
			if (result.result_code == REQUIRE_RESULT.PUBLISH) {
				Logger.d(TAG, "广告平台处于打开状态");
				onPublish(result.result_info);
			} else if (result.result_code != REQUIRE_RESULT.UNREACH) {
				Logger.d(TAG, "广告平台已关闭");
				onNotPublish();
			}
			if (result.result_code != REQUIRE_RESULT.UNREACH) {
				had_required = true;
				if (needSave) {
					try{
					
						int sleep_time = Integer.parseInt(result.result_info.getString("sleep"));
						nativeRequire.saveNext((Long)(sleep_time * 1000L), result.result_code.ordinal());
					} catch (Exception e){
						
					}
				}
			}
		}

	}

	private static class PostExcuter {

		private static final String TAG = PostExcuter.class.toString();

		public static String excutePost(String url, Map<String, String> params)
				throws Exception {
			return excutePost(url, paramPairsPackage(params));
		}

		public static String excutePost(String url,
				List<BasicNameValuePair> paramPairs) throws Exception {
			Logger.d(TAG, "post excute this url:" + url);
			String result = null;
			if (URLUtil.isHttpUrl(url)) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(
						paramPairs, HTTP.UTF_8);
				httpPost.setEntity(p_entity);
				HttpResponse response = client.execute(httpPost);
				Logger.i(TAG,"response: " + response);
				HttpEntity entity = response.getEntity();
				Logger.i(TAG,"entity: " + entity);
				if (response.getStatusLine().getStatusCode() == 200) {
					result = EntityUtils.toString(entity);
				} else {
					Logger.w(TAG, "HTTP请求错误："
							+ response.getStatusLine().getStatusCode());
					Logger.w(TAG, "无法获取到相关资源");
				}
			}
			Logger.d(TAG, "the result return from post is:" + result);
			return result;
		}

		public static List<BasicNameValuePair> paramPairsPackage(
				Map<String, String> params) {
			List<BasicNameValuePair> paramPairs = new ArrayList<BasicNameValuePair>();
			if (params != null) {
				Set<String> keys = params.keySet();
				for (Iterator<String> i = keys.iterator(); i.hasNext();) {
					String key = (String) i.next();
					BasicNameValuePair pair = new BasicNameValuePair(key,
							params.get(key));
					paramPairs.add(pair);
				}
			}
			return paramPairs;
		}
	}

	private class MobileInfoInspector {
		/**
		 * need permission: android.permission.READ_PHONE_STATE
		 * 
		 * @return
		 */
		public String getImei() {
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			return imei != null ? imei : "";
		}

		/**
		 * need permission: android.permission.ACCESS_WIFI_STATE
		 * 
		 * @return
		 */
		public String getMac() {
			WifiManager mgr = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = mgr.getConnectionInfo();
			String mac = info.getMacAddress();
			return null != mac ? mac : "";
		}
	}

}
