package com.tblin.HandsetGuardant.be;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tblin.HandsetGuardant.Logger;

public class BENetReceiver extends BroadcastReceiver {

	private static List<BEController> bes = new ArrayList<BEController>();
	private static final String TAG = "BENetReceiver";

	public static void registController(BEController controller) {
		if (null != controller && !bes.contains(controller)) {
			Logger.i(TAG, "add controller: " + controller);
			bes.add(controller);
		}
	}

	public static void unregistController(BEController controller) {
		bes.remove(controller);
		Logger.i(TAG, "remove controller: " + controller);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i(TAG,"receive network BroadcastReceiver at ");
		if (!isNetworkOk(context)) {
			Logger.i(TAG, "网络连接未打开，不启动be");
			return;
		}
		Logger.i(TAG, "网络连接已打开，启动be:" + bes.size());
		for (BEController controller : bes) {
			controller.requirePublishDelay(60 * 1000);
		}
	}
	
	public static boolean isNetworkOk(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean isOK = info != null && info.isConnected() && info.isAvailable();
        return isOK;
    }

}