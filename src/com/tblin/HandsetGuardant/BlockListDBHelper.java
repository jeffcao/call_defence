package com.tblin.HandsetGuardant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlockListDBHelper extends SQLiteOpenHelper {

	private static final String TABLE_NAME = "block_list";
	private static final String TABLE_SMS="block_sms";
	private static BlockListDBHelper INSTANCE;
	private Context mContext;
	public static final String BLOCK_DATA_CHANGED = "com.tblin.firewall.BlockDataChanged";
	public static final String BLOCK_SMS_CHANGED="com.tblin.firewall.sms";
	private static int BDBVERSION=3;
	public interface OnBlockChanged {
		void onBlockChanged();
	}

	/**
	 * table key is _id--name-mobile-time
	 * 
	 * @param context
	 */
	private BlockListDBHelper(Context context) {
		super(context, TABLE_NAME, null, BDBVERSION);
		mContext = context;
	}

	public static BlockListDBHelper getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new BlockListDBHelper(context);
		}
		return INSTANCE;
	}

	private void notifyLsnrs() {
		Intent intent = new Intent();
		intent.setAction(BLOCK_DATA_CHANGED);
		mContext.sendBroadcast(intent);
	}
	private void notifySMS() {
		Intent intent = new Intent();
		intent.setAction(BLOCK_SMS_CHANGED);
		mContext.sendBroadcast(intent);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE "
				+ TABLE_NAME
				+ "(_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL, mobile TEXT NOT NULL, time LONG NOT NULL,address TEXT  )";
		String sms= "CREATE TABLE "
				+ TABLE_SMS
				+ "(_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL, mobile TEXT NOT NULL, time LONG NOT NULL,content TEXT ,address TEXT )";
		db.execSQL(sql);
		db.execSQL(sms);
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD address TEXT");
		db.execSQL("UPDATE " + TABLE_NAME + " SET address=''");
		db.execSQL("ALTER TABLE " + TABLE_SMS + " ADD address TEXT");
		db.execSQL("UPDATE " + TABLE_SMS + " SET address=''");
	}
	
	public long insert(String name, String mobile, long time,String address) {
		if (name == null || mobile == null || time == -1) {
			return -1;
		}
		SQLiteDatabase db = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("mobile", mobile);
		contentValues.put("time", time);
		contentValues.put("address", address);
		long re = db.insert(TABLE_NAME, null, contentValues);
		if (re != -1) {
			notifyLsnrs();
		}
		return re;
	}

	public long insertCall(String name, String mobile, long time,String address) {
		if (name == null || mobile == null || time == -1) {
			return -1;
		}
		SQLiteDatabase db = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("mobile", mobile);
		contentValues.put("time", time);
		contentValues.put("address", address);
		
		
		long re = db.insert(TABLE_NAME, null, contentValues);
		if (re != -1) {
			notifyLsnrs();
		}
		return re;
	}
	public long insertSMS(String name, String mobile, long time,String content,String address) {
		if (name == null || mobile == null || time == -1 ) {
			return -1;
		}
		SQLiteDatabase db = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("mobile", mobile);
		contentValues.put("time", time);
		contentValues.put("address",address);
		if(content!=null){
			contentValues.put("content", content);
		}
		long re = db.insert(TABLE_SMS, null, contentValues);
		if (re != -1) {
			notifySMS();
		}
		return re;
	}
	private boolean isMobileExist(String mobile) {
		List<String> mobiles = getAllMobiles();
		for (String t : mobiles) {
			if (t.equals(mobile)) {
				return true;
			}
		}
		return false;
	}
	public int delete(int _id,String table_name) {
		SQLiteDatabase db = getWritableDatabase();
		int re = db.delete(table_name, "_id=?",
				new String[] { Integer.toString(_id) });
		if (re != 0) {
			notifyLsnrs();
			notifySMS();
		}
		return re;
	}
	public boolean delete(String mobile,int k) {
			String table=null;
		if (isMobileExist(mobile)) {
			SQLiteDatabase db = getReadableDatabase();
			switch (k){
			case 0: table=TABLE_NAME; break;
			case 1:table=TABLE_SMS; break;
			}
			Cursor c = db.rawQuery("select _id from " + table
					+ " where mobile = ?", new String[] { mobile });
			try {
				c.moveToFirst();
				int _id = c.getInt(0);
				if (delete(_id,table) != 0) {
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
	public boolean delete(String name, String mobile, long time,int k) {
		if (name == null || mobile == null || time == -1) {
			return false;
		}
		boolean call=true;
		SQLiteDatabase db = getReadableDatabase();
		String table=null;
		switch (k){
		case 0: table=TABLE_NAME; break;
		case 1:table=TABLE_SMS; call=false; break;
		}
		Cursor c = db.rawQuery("select _id from " + table
				+ " where mobile = ? and name = ? and time = ?", new String[] {
				mobile, name, Long.toString(time) });
		try {
			c.moveToFirst();
			int _id = c.getInt(0);
			delete(_id,table);
		} finally {
			c.close();
		}
		if(call){
		notifyLsnrs();
		}else{
		notifySMS();
		}
		return true;
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

	public void clearAll() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from " + TABLE_NAME);
		db.execSQL("delete from "+ TABLE_SMS);
		notifyLsnrs();
		notifySMS();
	}
	public List<BlockEvent> getCallRecords(){
		List<BlockEvent> e = new ArrayList<BlockEvent>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from " + TABLE_NAME, new String[] {});
		
		try {
			while (c.moveToNext()) {
				String strs=null;
				String str=c.getString(4);
				String get2=c.getString(2);
				if(str.equals(null)||str.equals("")){
					String num=DisplayCallUitl.proNumber(get2);
				 strs = DisplayCallService.getInCommingNUm(num);
					 
					 str=strs;
					 db.execSQL("update block_list set address="+"'"+str+"'"+" where mobile="+"'"+get2+"'");
					
				}
				BlockEvent event = new BlockEvent(c.getString(1),
						get2, c.getLong(3),str,0);
				e.add(0, event);
			}
		} finally {
			c.close();
		}
		
		return e;

	}
	public List<BlockEvent> getSMSRecords(){
		List<BlockEvent> e = new ArrayList<BlockEvent>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from " + TABLE_SMS, new String[] {});
		
		try {
			while (c.moveToNext()) {
				String strs=null;
				String str=c.getString(5);
				String get2=c.getString(2);
				if(str.equals(null)||str.equals("")){
					String num=DisplayCallUitl.proNumber(get2);
				 strs = DisplayCallService.getInCommingNUm(num);
					 
					 str=strs;
					 db.execSQL("update block_sms set address="+"'"+str+"'"+" where mobile="+"'"+get2+"'");
					
				}
				BlockEvent event = new BlockEvent(c.getString(1),
						c.getString(2), c.getLong(3),c.getString(4),str,true);
				e.add(0, event);
				
				
			}
		} finally {
			c.close();
		}
		
		return e;

	}

}
