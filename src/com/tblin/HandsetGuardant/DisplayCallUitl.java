package com.tblin.HandsetGuardant;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.tblin.HandsetGuardant.phoneattribute.PhoneStateUtil;



public class DisplayCallUitl {
	public static  String PATH = Environment.getExternalStorageDirectory()+"/tblin/firewall/phone.db";
	private static SQLiteDatabase db;
	  private static HashMap<String, String> pros=new HashMap<String, String>();
      private static HashMap<String, String> city=new HashMap<String, String>();
      private static HashMap<String, String> opes =new HashMap<String, String>();
      private static String TAG="DisplayCallUitl";
      private static String KEYPATH=FirewallApplication.CONTEXT.getFilesDir().getPath()+"/keyword.db";
     private static String KEYWORD_TABLE="word";
     public static String PHONE_DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tblin/firewall/phone.db";
	public static void saveDB(Context context) { 
		Logger.i(TAG, "phone db save path is " + PHONE_DB_PATH);
		if (!checkIsPhoneDbExist()) {
			Logger.w(TAG, "phone db not exist, only initial key word");
			saveKeyWord(context);
			return;
		} else {
			Logger.i(TAG, "phone db exist, init phone db an key word db");
			db = SQLiteDatabase.openOrCreateDatabase(PHONE_DB_PATH, null);
			seachData(context);
			return;
		}
		
		/*close();
		Logger.i(TAG, "转移数据...");
			InputStream in = context.getResources().openRawResource(R.raw.phone); //欲导入的数据库
		
			if(!existSDcard()){
				Logger.i(TAG, "没有SD卡...");
						saveInData(context, in);
						seachData(context);
						
			}else if(existSDcard()&&isAvaiableSpace(9)){
				File file = new File(PATH);
				
				
				Logger.i(TAG, "有SD卡,空间充足...");
					try {
						if (!file.exists()) {
							File files=new File(Environment.getExternalStorageDirectory()+"/tblin/firewall");
							files.mkdirs();
							Logger.i(TAG, "未有文件,复制...");
							
							FileOutputStream fos = new FileOutputStream(PATH);
							
							byte[] bytes = new byte[1024];
							int count = 0;
						
								
							while((count = in.read(bytes)) >0) {
								fos.write(bytes, 0, count);
								
							}
							fos.flush();
							
							fos.close();
							
							in.close();
							
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
						
					}finally{
						if(in!=null){
							try {
								
								in.close();
							} catch (IOException e) {
								
								e.printStackTrace();
							}
						}
					}
					db = SQLiteDatabase.openOrCreateDatabase(PATH, null);
					seachData(context);
			}else{
				Toast.makeText(context, "SD卡空间不足.", 1000).show();
				Logger.e(TAG, "SD卡空间不足");
				saveInData(context,in);
				seachData(context);
			}	*/
	
		
		
	}
	
	public static boolean checkIsPhoneDbExist() {
		boolean isSdExist = PhoneStateUtil.isSdCardAvailable();
		if (!isSdExist) {
			Logger.w(TAG, "sd card is not exist, can not init phone db");
			return false;
		}
		File file = new File(PHONE_DB_PATH + "ok");
		boolean isPhoneDbOk = file.exists();
		if (!isPhoneDbOk) {
			Logger.w(TAG, "phone is not exist or not unpackage success, can not init phone db");
			return false;
		}
		return true;
	}
	
	public static boolean isPhoneDbVersionOne() {
		File phoneDb = new File(PHONE_DB_PATH);
		long dbLength = phoneDb.length();
		Logger.i(TAG, "find phone db file, length is " + dbLength);
		boolean isPhoneDbVersionOne = dbLength == 8182784;
		if (isPhoneDbVersionOne) {
			Logger.i(TAG, "user has phone db version one");
			return true;
		}
		return isPhoneDbVersionOne;
	}
	
	private static void saveInData(Context context, InputStream in) {
		try {
			final File filed=new File("/data/data/com.tblin.firewall/files/phone.db");
			
			if (!filed.exists()) {
				Logger.i(TAG, "未有文件,复制...");
				
				FileOutputStream fos = context.openFileOutput("phone.db",Context.MODE_PRIVATE);
				byte[] bytes = new byte[2046];
				int count = 0;
				
				while((count = in.read(bytes)) >0) {
					fos.write(bytes, 0, count);
				}
				fos.flush();
				fos.close();
				in.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.tblin.firewall/files/phone.db", null);
	}
	private static void seachData(Context context) {
		pros= DisplayCallUitl.queryProvinces();
        city=DisplayCallUitl.queryCity();
        opes=DisplayCallUitl.queryOperators();
        Logger.i(TAG, "进入service... 省="+pros.size()+"市="+city.size()+"公司="+opes.size());
        
        
			Logger.i(TAG, "数据准备好...");
			//复制关键字数据库
			saveKeyWord(context);
	}
	public static void saveKeyWord(Context context){
		try {
			final File keyfile=new File(KEYPATH);
			InputStream key = context.getResources().openRawResource(R.raw.keyword); //欲导入的数据库
			if (!keyfile.exists()) {
				Logger.i(TAG, "未有keyword文件,复制...");
				
				FileOutputStream fos = context.openFileOutput("keyword.db",Context.MODE_PRIVATE);
				byte[] bytes = new byte[2046];
				int count = 0;
				
				while((count = key.read(bytes)) >0) {
					fos.write(bytes, 0, count);
				}
				fos.flush();
				fos.close();
				key.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.i(TAG, "已保存关键字数据");
	}
	
	public static ArrayList<KeyWorder> getKeyWord(){
		ArrayList<KeyWorder> keyworders=new ArrayList<KeyWorder>();
		SQLiteDatabase dbkey = SQLiteDatabase.openOrCreateDatabase(KEYPATH, null);
		Cursor c=dbkey.query(KEYWORD_TABLE, new String[]{"name"},null , null, null, null, null);
		if(c!=null){
			
			while(c.moveToNext()){
				KeyWorder key=new KeyWorder();
				key.setName(c.getString(c.getColumnIndex("name")));
				keyworders.add(key);
			}
			
		}
		c.close();
		dbkey.close();
		return keyworders;
	}

	public static void insertKeyWord(Context context,String str){
		SQLiteDatabase dbkey = SQLiteDatabase.openOrCreateDatabase(KEYPATH, null);
		ContentValues values = new ContentValues();
		values.put("name", str);
		dbkey.insert(KEYWORD_TABLE, null, values);
	
		dbkey.close();
		notifyKey(context);
	}
	public static void updateKey(Context context,String oldstr,String newstr){
		SQLiteDatabase dbkey = SQLiteDatabase.openOrCreateDatabase(KEYPATH, null);
		ContentValues values = new ContentValues();
		values.put("name", newstr);
		dbkey.update(KEYWORD_TABLE, values, "name=?", new String[]{oldstr});
		dbkey.close();
		notifyKey(context);
	}
	public static void delKeyword(Context context,String str){
		SQLiteDatabase dbkey = SQLiteDatabase.openOrCreateDatabase(KEYPATH, null);
		dbkey.delete(KEYWORD_TABLE, "name=?", new String[]{str});
		dbkey.close();
		notifyKey(context);
	}
	 public static boolean existSDcard()
	    {
	        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState()))
	        {
	            return true;
	        }
	        else
	            return false;
	    }
	
	 public static String proNumber(String str){
			String ss=null;
			ss=cutNumber(str);
			
			if(ss.length()<7||ss.length()==7){
				if (ss.length()==3) {
					
					ss = "8600"+ss;
				}else if(ss.length()==4){
					ss="860"+ss;
				}
				
				return ss;
			}
			
			if (ss.substring(0, 1).equals("0")) {
				Logger.i("displaycalluitl",
						"返回=" + ss.substring(0, ss.length() - 8));
				if (ss.startsWith("01") || ss.startsWith("02")) {
					String k=ss.substring(0, 3);
					ss = "8600"+k;
				} else {
					String k=ss.substring(0, 4);
					ss = "860"+k;
				}
			} else {
				ss=ss.substring(0, 7);
			}
		
			return ss;

		}  
	public static String cutNumber(String str){
		
		String cut[]=new String[]{"+86","+852","00852"};
		for(int n=0;n<cut.length-1;n++){
			if(str.contains(cut[n])){
			String ss =	str.substring(cut[n].length());
			 return ss;
			}
			
		}
		return str;
	}
	
	public static void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}
	public static String queryInfos(String phone ){
		if (null == db) {
			Logger.w(TAG, "queryInfos db is null");
			return "未知地区";
		} if (phone.startsWith("+852") || phone.startsWith("00852")) {
			return "中国-香港";
		} if (phone.startsWith("+853") || phone.startsWith("00853")) {
			return "中国-澳门";
		}
		phone = proNumber(phone);
		String address = null;
		String pro=null;
		String citys=null;
		String ope=null;
		
		
		Cursor c = db.query("phone_reg", new String[]{"phone_id","pid","cid","oid"}, "phone_id=?", new String[]{phone}, null, null, null);
		try{
		if(c!=null&&c.getCount()>0){
			c.moveToNext();
	
			pro=c.getString(c.getColumnIndex("pid"));
			citys=c.getString(c.getColumnIndex("cid"));
			ope=c.getString(c.getColumnIndex("oid"));
			
			address=pros.get(pro)+city.get(citys)+"-"+opes.get(ope);
			return address;
			
		}
		}finally{
			if(c!=null){
				c.close();
			}
		}
		
		
		return address="未知地区";
	}
	

	public static HashMap<String,String> queryProvinces(){
		
		HashMap<String,String> map=new HashMap<String,String>();
		if (null == db) {
			Logger.w(TAG, "queryProvinces db is null");
			return map;
		}
		Cursor c=db.query("provinces", new String[]{"name","pid"}, null, null, null, null, null);
		
		try {
		if(c!=null && c.getCount()>0){
			while(c.moveToNext()){
				
				map.put(""+c.getString(c.getColumnIndex("pid")), c.getString(c.getColumnIndex("name")));
			}
		
			c.close();
		} } finally {
			if (c != null)
				c.close();
		}
		
		
		return map;
	}
	
	public static HashMap<String,String> queryCity(){
		HashMap<String,String> map=new HashMap<String,String>();
		if (null == db) {
			Logger.w(TAG, "queryCity db is null");
			return map;
		}
		Cursor c=db.query("cities", new String[]{"name","cid"}, null, null, null, null, null);
		try{
		if(c!=null && c.getCount()>0){
			while(c.moveToNext()){
				map.put(""+c.getString(c.getColumnIndex("cid")), c.getString(c.getColumnIndex("name")));
			}
		
			c.close();
		}
		}finally{
			if(c!=null){
				c.close();
			}
		}
		
		
		return map;
	}
	public static HashMap<String,String> queryOperators(){
		HashMap<String,String> map=new HashMap<String,String>();
		if (null == db) {
			Logger.w(TAG, "queryOperators db is null");
			return map;
		}
		Cursor c=db.query("operators", new String[]{"name","oid"}, null, null, null, null, null);
		try{
		if(c!=null && c.getCount()>0){
			while(c.moveToNext()){
				map.put(""+c.getString(c.getColumnIndex("oid")), c.getString(c.getColumnIndex("name")));
			}
		
			c.close();
		}
		}finally{
			if(c!=null){
				c.close();
			}
		}
		
		
		return map;
	}
	public static void notifyKey(Context context){
		Intent it=new Intent();
		it.setAction("com.tblin.firewall.key");
		
		context.sendBroadcast(it);
	}
	public static boolean isAvaiableSpace(int sizeMb) {
		boolean ishasSpace = false;
		if (android.os.Environment.getExternalStorageState().equals(
		android.os.Environment.MEDIA_MOUNTED)) {
		String sdcard = Environment.getExternalStorageDirectory().getPath();
		StatFs statFs = new StatFs(sdcard);
		long blockSize = statFs.getBlockSize();
		long blocks = statFs.getAvailableBlocks();
		long availableSpare = (blocks * blockSize) / (1024 * 1024);
		Log.d("剩余空间", "availableSpare = " + availableSpare);
		if (availableSpare > sizeMb) {
		ishasSpace = true;
		}
		}
		return ishasSpace;
		}

//	public static void updatePhoneReg(String phone_id,String pid,String cid,String oid){
//		String sql="insert into phone_reg (phone_id,pid,cid,oid)values("+phone_id +","+ pid +","+cid +","+ oid+")";
//		Logger.i(TAG, "insert ---------->>>");
//		db.execSQL(sql);
//		
//	}
//	public static void updateProvinces(String pid,String name){
//		String sql="insert into provinces (pid,name) values("+pid +","+name+")";
//		Logger.i(TAG, "insert provinces ---------->>>");
//		db.execSQL(sql);
//		db.close();
//	}
//	public static void updateCities(String cid,String name){
//		String sql="insert into cities (cid,name) values("+cid +","+name+")";
//		Logger.i(TAG, "insert cities ---------->>>");
//		db.execSQL(sql);
//		db.close();
//	}
//	public static void updateOperators(String oid,String name){
//		String sql="insert into operators (oid,name) values("+oid +","+name+")";
//		Logger.i(TAG, "insert operators ---------->>>");
//		db.execSQL(sql);
//		db.close();
//	}
}
















