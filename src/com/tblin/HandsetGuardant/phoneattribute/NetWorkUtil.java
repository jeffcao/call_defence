package com.tblin.HandsetGuardant.phoneattribute;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {

	/**
	 * 需要权限 ACCESS_NETWORK_STATE
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		boolean result = false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			NetworkInfo.State state = info.getState();
			if (state == NetworkInfo.State.CONNECTED) {
				result = true;
			}
		}
		return result;
	}
}
