package com.tblin.HandsetGuardant.phoneattribute;

import android.content.Context;

public class PhoneStateUtil {

	/**
	 * 需要权限 ACCESS_NETWORK_STATE
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		return NetWorkUtil.isNetConnected(context);
	}

	public static boolean isSdCardAvailable() {
		return SDCardUtil.isSdCardAvailable();
	}

	public static long getAvailableSpace() {
		return SDCardUtil.getAvailableSpace();
	}

	public static boolean isSpaceAvailableByte(long bytes) {
		return SDCardUtil.isSpaceAvailableByte(bytes);
	}

	public static boolean isSpaceAvailableKB(long kbs) {
		return SDCardUtil.isSpaceAvailableKB(kbs);
	}

	public static boolean isSpaceAvailableMB(long mbs) {
		return SDCardUtil.isSpaceAvailableMB(mbs);
	}

}
