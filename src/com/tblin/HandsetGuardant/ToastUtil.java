package com.tblin.HandsetGuardant;

import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

	private static Toast mToast;

	public static void toast(final String msg) {
		if (msg == null)
			return;
		Runnable r = new Runnable() {

			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(FirewallApplication.CONTEXT, msg,
							Toast.LENGTH_LONG);
					mToast.setGravity(Gravity.CENTER, 0, 0);
				} else {
					mToast.setText(msg);
				}
				mToast.show();
			}
		};
		FirewallApplication.HANDLER.post(r);
	}

}
