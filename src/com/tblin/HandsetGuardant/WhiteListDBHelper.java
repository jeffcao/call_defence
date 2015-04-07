package com.tblin.HandsetGuardant;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WhiteListDBHelper extends SQLiteOpenHelper {

	private static final String TABLE_NAME = "white_list";
	private List<OnDataChanged> lsnrs;
	private static final String TAG = WhiteListDBHelper.class.toString();
	private static WhiteListDBHelper db;
	private static int DBVERSION = 3;
	private Context mContext;

	public interface OnDataChanged {
		void onDataChange();
	}

	/**
	 * table key is _id--name-mobile--block
	 * 
	 * @param context
	 */
	private WhiteListDBHelper(Context context) {
		super(context, TABLE_NAME, null, DBVERSION);
		lsnrs = new ArrayList<WhiteListDBHelper.OnDataChanged>();
		mContext = context;

	}

	public static WhiteListDBHelper getInstance(Context context) {
		if (db == null) {
			db = new WhiteListDBHelper(context);
		}
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "CREATE TABLE "
				+ TABLE_NAME
				+ "(_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL, mobile TEXT NOT NULL, address TEXT)";

		Logger.i(TAG, "create white_list");
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.i(TAG, "on db upgrade");
		db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD address TEXT");
		db.execSQL("UPDATE " + TABLE_NAME + " SET address=''");
		Logger.i(TAG, "更新数据库完成");
	}

	public void registDataLsnr(OnDataChanged on) {
		if (on != null) {
			Logger.i(TAG, "listener regist on");
			lsnrs.add(on);
		}
	}

	public long insert(String name, String mobile, String address) {
		if (mobile == null) {
			return -1;
		} else if (isMobileExist(mobile)) {
			return -2;
		}
		name = (name == null || "".equals(name)) ? BlackUser.UNKNOWNAME : name;
		SQLiteDatabase db = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		if (!MainTabActivity.nativePrefix.equals(mobile)) {
			mobile = PhoneNumberHelper.trimNumber(mobile, mContext);
		} else {
			SettingPreferenceHandler.getInstance().setBlockNatvie(false);
		}
		contentValues.put("name", name);
		contentValues.put("mobile", mobile);
		contentValues.put("address", address);
		long re = db.insert(TABLE_NAME, null, contentValues);
		if (re != -1) {
			notifyLsnrs();
		}
		return re;
	}

	private void notifyLsnrs() {
		Logger.i(TAG, "listeners count: " + lsnrs.size());
		for (OnDataChanged o : lsnrs) {
			Logger.i(TAG, "retrive listeners");
			o.onDataChange();
		}
	}

	public void removeLsnr(OnDataChanged o) {
		lsnrs.remove(o);
	}

	public boolean isMobileExist(String mobile) {
		List<String> mobiles = getAllMobiles();
		for (String t : mobiles) {
			if (t.equals(mobile)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getAllMobiles() {
		SQLiteDatabase db = getReadableDatabase();
		List<String> mobiles = new ArrayList<String>();
		Cursor c = db.rawQuery("select mobile from " + TABLE_NAME,
				new String[] {});
		try {
			while (c.moveToNext()) {
				mobiles.add(c.getString(0));
			}
		} finally {
			c.close();
		}
		return mobiles;
	}

	public int delete(int _id) {
		SQLiteDatabase db = getWritableDatabase();
		int re = db.delete(TABLE_NAME, "_id=?",
				new String[] { Integer.toString(_id) });
		Logger.i("1111", "re is " + re);
		if (re != 0) {
			notifyLsnrs();
		}
		return re;
	}

	public boolean delete(String mobile) {
		if (isMobileExist(mobile)) {
			SQLiteDatabase db = getReadableDatabase();
			Cursor c = db.rawQuery("select _id from " + TABLE_NAME
					+ " where mobile = ?", new String[] { mobile });
			try {
				c.moveToFirst();
				int _id = c.getInt(0);
				if (delete(_id) != 0) {
					SettingPreferenceHandler.getInstance()
							.setBlockNatvie(false);
				}
			} finally {
				c.close();
			}
			return true;
		}
		return false;
	}

	public List<WhiteUser> getAllUsers() {
		List<WhiteUser> users = new ArrayList<WhiteUser>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from " + TABLE_NAME, new String[] {});
		try {
			while (c.moveToNext()) {
				WhiteUser user = new WhiteUser();
				user.setName(c.getString(1));
				user.setMobile(c.getString(2));
				String strs = null;
				String str = c.getString(3);
				String get2 = c.getString(2);
				if (str.equals(null) || str.equals("")) {

					String num = DisplayCallUitl.proNumber(get2);

					strs = DisplayCallService.getInCommingNUm(num);

					str = strs;
					db.execSQL("update white_list set address=" + "'" + str
							+ "'" + " where mobile=" + "'" + get2 + "'");

				}
				user.setAddress(str);
				users.add(0, user);
			}
		} finally {
			c.close();
		}
		return users;
	}

	public long updateName(String newName, String mobile) {
		if (null == newName || null == mobile || "".equals(newName)
				|| "".equals(mobile)) {
			return -1;
		}
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", newName);
		long result = db.update(TABLE_NAME, values, "mobile=?",
				new String[] { mobile });
		notifyLsnrs();
		return result;
	}

	public String getName(String mobile) {
		if (mobile == null) {
			return null;
		}
		String name = null;
		if (isMobileExist(mobile)) {
			SQLiteDatabase db = getReadableDatabase();
			Cursor c = db.rawQuery("select name from " + TABLE_NAME
					+ " where mobile = ?", new String[] { mobile });
			try {
				c.moveToFirst();
				name = c.getString(0);
			} finally {
				c.close();
			}
		} else {
			name = BlackUser.UNKNOWNAME;
		}
		return name;
	}
}
