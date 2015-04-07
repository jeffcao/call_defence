package com.tblin.HandsetGuardant;

import java.lang.Thread.UncaughtExceptionHandler;

import android.util.Log;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类 来接管程序,并记录 发送错误报告.
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = CrashHandler.class.toString();
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static CrashHandler INSTANCE;

	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init() {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			Logger.e(TAG, ex.getMessage());
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	public boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		Logger.e(TAG, Log.getStackTraceString(ex));
		return true;
	}
}