package com.tblin.firewall;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tblin.firewall.core.Decider;

public class BlackListDBHelper extends SQLiteOpenHelper {
	
	private static final int BLOCKCALL=801;
	private static final int BLOCKSMS=810;
	private static final int CALLANDSMS=800;
	private static final int NOBLOCK=811;
	private static final String TABLE_NAME = "black_list";
	private List<OnDataChanged> lsnrs;
	private static final String TAG = BlackListDBHelper.class.toString();
	private static BlackListDBHelper db;
	private Context mContext;

	public interface OnDataChanged {
		void onDataChange();
	}

	/**
	 * table key is _id--name-mobile--block
	 * 
	 * @param context
	 */
	private BlackListDBHelper(Context context) {
		super(context, TABLE_NAME, null, 2);
		lsnrs = new ArrayList<BlackListDBHelper.OnDataChanged>();
		mContext = context;
	}

	public static BlackListDBHelper getInstance(Context context) {
		if (db == null) {
			db = new BlackListDBHelper(context);
		}
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE "
				+ TABLE_NAME
				+ "(_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL, mobile TEXT NOT NULL, type INTEGER NOT NULL, block INTEGER NOT NULL)";
		
		
		db.execSQL(sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.i(TAG, "on db upgrade");
		db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD type INTEGER");
		db.execSQL("UPDATE " + TABLE_NAME + " SET type=" + CALLANDSMS);
	}

	public void registDataLsnr(OnDataChanged on) {
		if (on != null) {
			Logger.i(TAG, "listener regist on");
			lsnrs.add(on);
		}
	}

	public long insert(String name, String mobile,int type) {
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
			SettingPreferenceHandler.getInstance().setBlockNatvie(true);
		}
		contentValues.put("name", name);
		contentValues.put("mobile", mobile);
		contentValues.put("type", type);
		Logger.i(TAG, "insert type is:" + type);
		contentValues.put("block", Decider.CALL_BLOCK_END);
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

	public  boolean isMobileExist(String mobile) {
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

	public List<BlackUser> getAllUsers() {
		List<BlackUser> users = new ArrayList<BlackUser>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select _id, name, mobile, type from " + TABLE_NAME, new String[] {});
		try {
			while (c.moveToNext()) {
				BlackUser user = new BlackUser();
				user.setName(c.getString(1));
				user.setMobile(c.getString(2));
				user.setType(c.getInt(3));
				Logger.i(TAG, "user type:" + c.getInt(3));
				switch(c.getInt(3)){
				case CALLANDSMS: user.setIskillcall(true);user.setIskillsms(true)  ;break;
				case BLOCKCALL:user.setIskillcall(true);user.setIskillsms(false);break;
				case BLOCKSMS:user.setIskillcall(false);user.setIskillsms(true);break;
				case NOBLOCK:user.setIskillcall(false);user.setIskillsms(false);break;
				}
				users.add(0, user);
			}
		} finally {
			c.close();
		}
		return users;
	}

	public long updateBlock(String mobile, int type) {
		if (null == mobile || "".equals(mobile) || (type > 2 || type < 1)) {
			return -1;
		}
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("block", type);
		long result = db.update(TABLE_NAME, values, "mobile=?",
				new String[] { mobile });
		notifyLsnrs();
		return result;
	}
	
	public int queryBlock(String mobile) {
		if (null == mobile || "".equals(mobile)) {
			return -1;
		}
		int result = -1;
		
		SQLiteDatabase db = null;
		Cursor c = null;
		
		try {
			db = getReadableDatabase();
			c = db.rawQuery("select  block  from " + TABLE_NAME
					+ " where mobile = ?", new String[] { mobile });
			if (c.moveToFirst()) {
				Logger.i(TAG, "有记录: " + mobile);
				result = c.getInt(0);
			} else {
				Logger.i(TAG, "无记录: " + mobile);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	
	}

	public int queryCall(String mobile) {
		if (null == mobile || "".equals(mobile)) {
			return -1;
		}
		int result = -1;
		
		SQLiteDatabase db = null;
		Cursor c = null;
		
		try {
			db = getReadableDatabase();
			c = db.rawQuery("select  type  from " + TABLE_NAME
					+ " where mobile = ?", new String[] { mobile });
			if (c.moveToFirst()) {
				Logger.i(TAG, "有记录: " + mobile);
				result = c.getInt(0);
			} else {
				Logger.i(TAG, "无记录: " + mobile);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}
	public int querySms(String mobile){
		if (null == mobile || "".equals(mobile)) {
			return -1;
		}
		int result=-1;
		SQLiteDatabase db = null;
		Cursor c = null;
		
		try {
			db = getReadableDatabase();
			c = db.rawQuery("select  type  from " + TABLE_NAME
					+ " where mobile = ?", new String[] { mobile });
			if (c.moveToFirst()) {
				
				Logger.i("TAG", "查记录type="+c.getInt(0));
				result = c.getInt(0);
			} else {
				Logger.i(TAG, "无记录: " + mobile);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
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
