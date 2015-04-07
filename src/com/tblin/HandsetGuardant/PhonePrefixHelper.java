package com.tblin.HandsetGuardant;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

public class PhonePrefixHelper {

	public static final String UNKNOW_PRE = "";
	private static final String[] PRES = { "+86", "+852", "+853", "+886" };
	private static final String[] NAMES = { "中国", "香港", "澳门", "台湾" };
	private static final String[] IMSI_PRES = { "460", "454", "455", "466" };

	public static String getCountryPhonePrefix(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if (imsi != null && !"".equals(imsi)) {
			for (int i = 0; i < IMSI_PRES.length; i++) {
				if (imsi.startsWith(IMSI_PRES[i])) {
					return PRES[i];
				}
			}
		} else {
			return UNKNOW_PRE;
		}
		return UNKNOW_PRE;
	}

	public static String getAreaName(String prefix) {
		if (prefix == null || UNKNOW_PRE.equals(prefix)) {
			return "未知";
		}
		for (int i = 0; i < PRES.length; i++) {
			if (PRES[i].equals(prefix)) {
				return NAMES[i];
			}
		}
		return "未知";
	}
}
