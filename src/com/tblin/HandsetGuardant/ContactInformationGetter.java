package com.tblin.HandsetGuardant;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class ContactInformationGetter {

	/**
	 * <uses-permission android:name="android.permission.READ_CONTACTS"/>
	 * 
	 * @param context
	 * @return
	 */
	public static List<String[]> getContactRecord(Context context) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[] {
				CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME }, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);
		String[] temp;
		List<String[]> result = new ArrayList<String[]>();
		try {
			String number;
			String name;
			while (cursor.moveToNext()) {
				number = cursor.getString(0);
				// 假如联系人在联系表中不存在，这里name为Null
				name = cursor.getString(1);
				if (name == null || "".equals(name)) {
					name = BlackUser.UNKNOWNAME;
				}
				temp = new String[] { name, number };
				result.add(temp);
				Logger.i(ContactInformationGetter.class.toString(), "name: "
						+ name);
				Logger.i(ContactInformationGetter.class.toString(), "number: "
						+ number);
			}
		} finally {
			cursor.close();
		}
		return result;
	}

	/**
	 * <uses-permission android:name="android.permission.READ_CONTACTS"/>
	 * 
	 * @param context
	 * @return
	 */
	public static String[] getFirstContacter(Context context) {
		List<String[]> records = getContactRecord(context);
		return records.size() > 0 ? records.get(0) : null;
	}

	/**
	 * 删除number的最近一次来电
	 * @param context
	 * @param number
	 */
	public static void deleteLastCallRecord(Context context, String number) {
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = null;
		try {
			cursor = resolver.query(CallLog.Calls.CONTENT_URI,
					new String[] { "_id" }, "number=? and (type=1 or type=3)",
					new String[] { number }, "_id desc limit 1");
			if (cursor.moveToFirst()) {
				int id = cursor.getInt(0);
				resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?",
						new String[] { id + "" });
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * <uses-permission android:name="android.permission.READ_CONTACTS"/>
	 * 对于联系人较多的情况，这个操作比较耗时间
	 * 
	 * @param context
	 * @return
	 */
	public static List<String[]> getContacters(Context context) {
		ContentResolver cr = context.getContentResolver();
		List<String[]> result = new ArrayList<String[]>();
		Cursor cs = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null,
				null, null);
		try {
			while (cs.moveToNext()) {
				int nameFieldColumnIndex = cs
						.getColumnIndex(PhoneLookup.DISPLAY_NAME);
				String contact = cs.getString(nameFieldColumnIndex);
				String contactID = cs.getString(cs
						.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor csPhone = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactID, null, null);
				try {
					while (csPhone.moveToNext()) {
						String strPhoneNum = csPhone
								.getString(csPhone
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						int size = result.size();
						if (size > 0) {
							String[] lastContacter = result
									.get(result.size() - 1);
							if (!lastContacter[0].equals(contact)
									|| !lastContacter[1].equals(strPhoneNum)) {
								result.add(new String[] { contact, strPhoneNum });
							}
						} else {
							result.add(new String[] { contact, strPhoneNum });
						}
					}
				} finally {
					csPhone.close();
				}
			}
		} finally {
			cs.close();
		}
		for (String[] temp : result) {
			Logger.i(ContactInformationGetter.class.toString(), "name: "
					+ temp[0]);
			Logger.i(ContactInformationGetter.class.toString(), "number: "
					+ temp[1]);
		}
		return result;
	}

}
