package com.tblin.firewall;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SmsCallHelper {

	/**
	 * <uses-permission Android:name="android.permission.CALL_PHONE" />
	 * 
	 * @param mobile
	 * @param context
	 */
	public static void call(String mobile, Context context) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		String data = "tel:" + mobile;
		intent.setData(Uri.parse(data));
		context.startActivity(intent);
	}

	public static void sendSms(String mobile, Context context) {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		String data = "smsto:" + mobile;
		intent.setData(Uri.parse(data));
		context.startActivity(intent);
	}
	public static void sendSms2You(String content,Context context){
		
		Uri uri = Uri.parse("smsto:");
		
		Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
		intent.putExtra("sms_body" ,content);
		context.startActivity(intent);
	}
	public static void writeInbox(String moblie,String content,Context context){
		ContentResolver cr = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put("address", moblie);
        cv.put("body", content);
      
        cr.insert(Uri.parse("content://sms/inbox"), cv);
	}
}
