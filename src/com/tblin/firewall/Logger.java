package com.tblin.firewall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

public class Logger {

	private static final boolean DEBUG = false;
	private static final String DIR = "/mnt/sdcard/tblin/firewall/";
	private static PrintStream fos;
	private static final String TAG = Logger.class.toString();

	public static boolean open() {
		if (fos != null) {
			return false;
		}
		if (DEBUG) {
			Log.d(TAG, "open");
		}
		LogManager.work();
		try {
			File f = new File(DIR + LogManager.getFileName());
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			fos = new PrintStream(new FileOutputStream(f, true));
			return true;
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, e.toString());
			}
			return false;
		}
	}

	public static void close() {
		if (fos != null) {
			d("Logger", "close");
			fos.close();
		}
	}

	public static void save(String tag, String msg) {
		if (fos == null) {
			if (DEBUG)
				Log.e(TAG, "Logger has not been initialized");
			if (!open())
				return;
		}
		fos.println(tag + ":" + msg);
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
		save(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {
			Log.d(tag, msg);
		}
		save(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			Log.e(tag, msg);
		}
		save(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			Log.w(tag, msg);
		}
		save(tag, msg);
	}

	public static void w(String tag, Throwable t) {
		if (DEBUG) {
			Log.w(tag, t);
		}
		save(tag, t.toString());
	}

	private static class LogManager {

		public static final int MAX_LOGS = 5;

		public static void work() {
			try {
				File file = new File(DIR);
				File[] childs = file.listFiles();
				List<Integer> names = null;
				if (childs != null && childs.length > MAX_LOGS) {
					names = new ArrayList<Integer>();
					for (File f : childs) {
						String nm = f.getName();
						if (!nm.endsWith(".txt") || nm.length() < 5)
							continue;
						nm = nm.substring(0, nm.length() - 4);
						try {
							names.add(Integer.parseInt(nm));
						} catch (Exception e) {
							continue;
						}
					}
					Collections.sort(names, new Comparator<Integer>() {

						@Override
						public int compare(Integer object1, Integer object2) {
							return -object1.compareTo(object2);
						}
					});
					deleteTimePassed(names);
					if (DEBUG)
						Log.i(TAG, "size:" + names.size());
					if (MAX_LOGS >= names.size())
						return;
					for (int i = MAX_LOGS; i < names.size(); i++) {
						if (DEBUG)
							Log.i(TAG, "delete:" + names.get(i));
						new File(DIR + names.get(i) + ".txt").delete();
					}
					if (DEBUG)
						Log.i(TAG, names.toString());
				}
			} catch (Exception e) {
				if (DEBUG)
					Log.e(TAG, (e != null ? e.getMessage() : "null point"));
			}
		}

		private static void deleteTimePassed(List<Integer> names) {
			String now = getFileName();
			int nowName = Integer.parseInt(now.substring(0, now.length() - 4));
			List<Integer> toDelete = new ArrayList<Integer>();
			for (Integer name : names)
				if (name > nowName) {
					new File(DIR + name + ".txt").delete();
					toDelete.add(name);
					if (DEBUG)
						Log.i(TAG, "delete time passed:" + name);
				}
			for (Integer delte : toDelete)
				names.remove(delte);
		}

		public static String getFileName() {
			Calendar c = Calendar.getInstance();
			String year = c.get(Calendar.YEAR) + "";
			int mon = c.get(Calendar.MONTH) + 1;
			String month = mon >= 10 ? Integer.toString(mon) : ("0" + mon);
			int dy = c.get(Calendar.DAY_OF_MONTH);
			String day = dy >= 10 ? Integer.toString(dy) : ("0" + dy);
			return year + month + day + ".txt";
		}
	}
}
