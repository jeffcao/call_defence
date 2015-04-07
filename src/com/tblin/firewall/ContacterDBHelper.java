package com.tblin.firewall;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContacterDBHelper extends SQLiteOpenHelper {

	private static final String TABLE_NAME = "contacter_list";
	// private static final String TAG = ContacterDBHelper.class.toString();
	private static ContacterDBHelper db;

	/**
	 * table key is _id--name-mobile
	 * 
	 * @param context
	 */
	private ContacterDBHelper(Context context) {
		super(context, TABLE_NAME, null, 1);
	}

	public static ContacterDBHelper getInstance(Context context) {
		if (db == null) {
			db = new ContacterDBHelper(context);
		}
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE "
				+ TABLE_NAME
				+ "(_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL, mobile TEXT NOT NULL)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "drop table " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public void clearAll() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from " + TABLE_NAME);
	}

	public long insert(String name, String mobile) {
		if (name == null || mobile == null) {
			return -1;
		} else if (isMobileExist(mobile)) {
			return -2;
		}
		SQLiteDatabase db = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("mobile", mobile);
		long re = -1;
		synchronized (db) {
			re = db.insert(TABLE_NAME, null, contentValues);
		}
		return re;
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

	public List<String[]> getAllContacts() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = null;
		List<String[]> contacts = new ArrayList<String[]>();
		try {
			c = db.rawQuery("select * from " + TABLE_NAME, new String[] {});
			while (c.moveToNext()) {
				if (c.getColumnCount() == 3) {
					String name = c.getString(1);
					String mobile = c.getString(2);
					if (null != name && mobile != null) {
						contacts.add(new String[] { name, mobile });
					}
				}
			}
		} finally {
			if (null != c) {
				c.close();
			}
		}
		return contacts;
	}

	public List<String> getAllMobiles() {
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
		db.close();
		Logger.i("1111", "re is " + re);
		return re;
	}
	
	private List<OnSyncListener> lsnrs;
	
	public void registSyncListener(OnSyncListener lsnr) {
		if (lsnrs == null)
			lsnrs = new ArrayList<ContacterDBHelper.OnSyncListener>();
		if (lsnr != null)
			lsnrs.add(lsnr);
	}
	
	public interface OnSyncListener {
		void onSync();
	}
	
	public void onSync() {
		if (lsnrs != null)
			for (OnSyncListener lsnr : lsnrs)
				lsnr.onSync();
	}

}
