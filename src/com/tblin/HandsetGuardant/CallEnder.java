package com.tblin.HandsetGuardant;

import java.lang.reflect.Method;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

public class CallEnder {

	private static ITelephony iTelephony;
	private static final String TAG = CallEnder.class.toString();

	/**
	 * TODO 什么是RemoteException, 什么是AIDL?
	 * 
	 * @param context
	 * @throws RemoteException
	 */
	public static void endCall(Context context) throws RemoteException {
		if (iTelephony == null) {
			initITelephony(context);
		}
		iTelephony.endCall();
	}

	/**
	 * 这里的异常为反射异常，不会出现
	 * 
	 * @param context
	 */
	private static void initITelephony(Context context) {
		TelephonyManager telephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		Method getITelephonyMethod;
		try {
			getITelephonyMethod = TelephonyManager.class.getDeclaredMethod(
					"getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyMgr,
					(Object[]) null);
		} catch (Exception e) {
			Logger.e(CallEnder.class.toString(), e.getMessage());
		}
	}

}
