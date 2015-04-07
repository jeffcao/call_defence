package com.tblin.firewall.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tblin.firewall.MainTabActivity;
import com.tblin.firewall.R;
import com.tblin.firewall.SettingPreferenceHandler;

public class NotificationNotifier implements Notifier {

	@Override
	public void notifyUserIfNeed(Context context, String msg,
			String blockItemType) {
		if (SettingPreferenceHandler.getInstance().getToastWhenBlock())
			postToNotification(msg, context, blockItemType);
	}

	public static final String NOTIFICATION_TAG = "notification_tag";
	public static final String BLOCK__ITEM_TYPE = "block_item_type";
	public static final String BLOCK__ITEM_TYPE_SMS = "block_item_type_sms";
	public static final String BLOCK__ITEM_TYPE_CALL = "block_item_type_call";
	public static final String INTENT_TIME = "intent_time";
	private static final int NOTIFICATION_ID = 123;

	private void postToNotification(String message, Context context,
			String blockItemType) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService("notification");
		Notification notification = new Notification();
		notification.icon = R.drawable.logo;
		notification.tickerText = message;
		Intent intent = new Intent();
		intent.putExtra(NOTIFICATION_TAG, true);
		intent.putExtra(BLOCK__ITEM_TYPE, blockItemType);
		intent.putExtra(INTENT_TIME, System.currentTimeMillis());
		intent.setClass(context, MainTabActivity.class);
		PendingIntent m_PendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, "来电卫士", message,
				m_PendingIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

}
