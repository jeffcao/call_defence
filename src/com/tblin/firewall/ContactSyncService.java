package com.tblin.firewall;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;

public class ContactSyncService extends Service {

	private static final String TAG = ContactSyncService.class.toString();
	private static Context mContext;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// 监听联系人数据的监听对象
	private static ContentObserver mObserver = new ContentObserver(
			new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					syncContacer(mContext);
				}
			}).start();
		}
	};

	public static void syncContacer(Context context) {
		if (context == null) {
			return;
		}
		Logger.i(TAG, "联系人列表发生了改变，同步联系人列表...");
		ContacterDBHelper db = ContacterDBHelper.getInstance(context);
		db.clearAll();
		List<String[]> contacters = ContactInformationGetter
				.getContacters(context);
		for (String[] t : contacters) {
			if (t.length == 2) {
				db.insert(t[0], t[1]);
			}
		}
		db.onSync();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		getContentResolver().registerContentObserver(
				ContactsContract.Contacts.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mObserver);
	}
}
