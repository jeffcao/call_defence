package com.tblin.HandsetGuardant;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class UpdateNotifier {
	public static final String EVENT_PULL_UPDATE = "pull_update";
	public static final String EVENT_DOWNLOAD_UPDATE = "download_update";
	public static final String EVENT_REJECT_UPDATE = "reject_update";
	public static final String EVENT_CANCEL_UPDATE = "cancel_update";
	public static final String NOTIFY_URL = "";
	public static void notify(FirewallApplication application, String event) {
		String name = application.getName();
		String version = application.getVersion(); 
		String appid = application.getAppid();
		String update_mode = FireWallConfig.UPDATE_MODE;
		try {
			requestUrl(name, version, appid, update_mode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void requestUrl(String appName, String version, String appid,
			String mode) throws IOException {
		String name = URLEncoder.encode(appName);
		String vers = URLEncoder.encode(version);
		HttpGet httpGet = new HttpGet(NOTIFY_URL + "?softname=" + name
				+ "&version=" + vers + "&appid=" + appid + "&mode=" + mode);
		HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
		int httpCode = httpResponse.getStatusLine().getStatusCode();
		if (httpCode != 200) {
			
		} else {
			String httpResult = EntityUtils.toString(httpResponse.getEntity());
			
		}
	}
}
