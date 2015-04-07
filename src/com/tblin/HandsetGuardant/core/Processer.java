package com.tblin.HandsetGuardant.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.tblin.HandsetGuardant.BlackListDBHelper;
import com.tblin.HandsetGuardant.CallHandler;
import com.tblin.HandsetGuardant.ContacterDBHelper;
import com.tblin.HandsetGuardant.DisplayCallUitl;
import com.tblin.HandsetGuardant.KeyWorder;
import com.tblin.HandsetGuardant.Logger;
import com.tblin.HandsetGuardant.WhiteListDBHelper;
import com.tblin.HandsetGuardant.WhiteUser;

public class Processer {
	private Decider decider;
	private Blocker blocker;
	private Recorder recorder;
	private Notifier notifier;
	private int myType; // 这个参数没有使用
	private static final String TAG = Processer.class.toString();

	/**
	 * 注意， myType参数目前没有使用
	 * 
	 * @param decider
	 * @param blocker
	 * @param recorder
	 * @param notifier
	 * @param myType
	 */
	public Processer(Decider decider, Blocker blocker, Recorder recorder,
			Notifier notifier, int myType) {
		this.decider = decider;
		this.blocker = blocker;
		this.recorder = recorder;
		this.notifier = notifier;
		this.myType = myType;

	}

	/**
	 * 
	 * @param number
	 *            要处理的号码
	 * @param dataToBlocker
	 *            传递给Blocker的参数，拦截电话传递：null
	 * @param dataToRecorder
	 * @param context
	 *            Context
	 * @param msgToNotifier
	 *            传递给PostNotifier的参数，显示在消息栏的文字
	 */
	/*
	 * public void processIncomingNumber(String number, Map<String, Object>
	 * dataToBlocker, Map<String, Object> dataToRecorder, Context context,
	 * String msgToNotifier) { if (null == number || "".equals(number)) return;
	 * int blockType = decider.decideBlockType(number); if (blockType ==
	 * Decider.NOT_BLOCK) return; if (dataToBlocker == null) dataToBlocker = new
	 * HashMap<String, Object>(); dataToBlocker.put("context", context);
	 * dataToBlocker.put("number", number); dataToBlocker.put("type",
	 * blockType); blocker.block(dataToBlocker);
	 * recorder.recordIfNeed(dataToRecorder); notifier.notifyUserIfNeed(context,
	 * msgToNotifier); }
	 */

	/**
	 * 处理来电
	 * 
	 * @param number
	 *            要处理的来电号码
	 * @param context
	 *            context
	 * @param msgToNotify
	 *            提示到通知栏时显示的文字
	 */
	public void processIncomingCall(String number, Context context,
			String msgToNotify, int type) {
		Logger.i(TAG, "start process incoming call " + number);
		if (null == number || "".equals(number))
			return;
		int blockType = decider.decideBlockType(number);

		Logger.i(TAG, "number==" + number + "type==" + blockType);
		if (blockType == Decider.ACTION_NOT_BLOCK || blockType == Decider.ACTION_NOT_BLOCK_AS_IS_WHITE) {
			Intent it = new Intent();
			it.putExtra("call", number);
			it.setAction("com.tblin.firewall.out.call");
			context.sendBroadcast(it);
			Logger.i(TAG, "incall...sendbroadcast.. number=" + number);
			Logger.i(TAG, "not_endcall");
			return;
		}

		Intent it = new Intent();
		it.putExtra("call", number);
		it.setAction("com.tblin.firewall.out.call");
		context.sendBroadcast(it);
		Logger.i(TAG, "incall...sendbroadcast.. number=" + number);

		// 注释掉||blockType==Decider.CALL_BLOCK_END，因为这个会导致编辑详情中设置是否拦截电话失效
		if (needEndCall(number, context)
				|| blockType == Decider.ACTION_NOT_WHITE
				|| blockType == Decider.ACTION_BLOCK_ALL) {

			if (blockType == Decider.ACTION_NOT_WHITE) {
				blockType = Decider.ACTION_CALL_BLOCK_END;
			}
			Map<String, Object> dataToBlocker = new HashMap<String, Object>();
			dataToBlocker.put("context", context);
			dataToBlocker.put("number", number);
			dataToBlocker.put("type", blockType);
			Map<String, Object> dataToRecorder = new HashMap<String, Object>();
			dataToRecorder.put("number", number);
			dataToRecorder.put("context", context);
			blocker.block(dataToBlocker);
			recorder.recordIfNeed(dataToRecorder);
			notifier.notifyUserIfNeed(context, msgToNotify,
					NotificationNotifier.BLOCK__ITEM_TYPE_CALL);
			Logger.i(TAG, "endcall");
		}
	}

	public void processIncomingSms(String number, Context context,
			String msgToNotify, String smsContent, BroadcastReceiver receiver) {
		Logger.i(TAG, "start process incoming sms " + number);
		if (null == number || "".equals(number))
			return;
		int blockType = decider.decideBlockType(number);
		Logger.i(TAG, "decider is " + decider);
		Logger.i(TAG, "block type is " + blockType);
		boolean is_keyword = isKeyWord(context, smsContent);
		if (blockType == Decider.ACTION_NOT_BLOCK_AS_IS_WHITE
				|| (blockType == Decider.ACTION_NOT_BLOCK && !is_keyword)) {
			Logger.i(TAG, "返回=" + blockType);
			return;
		}
		if (blockType == Decider.ACTION_NOT_BLOCK && is_keyword) {
			WhiteListDBHelper db = WhiteListDBHelper.getInstance(context);
			List<WhiteUser> users = db.getAllUsers();
			List<String> mobiles = new ArrayList<String>();
			for (WhiteUser usr : users) {
				mobiles.add(usr.mobile);
			}
			WhiteDecider dec = new WhiteDecider(mobiles,
					Decider.ACTION_WHITE_DEFULAT_BLOCK_TYPE);
			if (dec.decideBlockType(number) == Decider.ACTION_NOT_BLOCK_AS_IS_WHITE) {
				return;
			}
		}
		Logger.i(TAG, "processIncomingSms1");
		if (needEndSms(number, context) || is_keyword
				|| blockType == Decider.ACTION_NOT_WHITE
				|| blockType == Decider.ACTION_BLOCK_ALL) {
			Map<String, Object> dataToBlocker = new HashMap<String, Object>();
			dataToBlocker.put("receiver", receiver);
			Map<String, Object> dataToRecorder = new HashMap<String, Object>();
			dataToRecorder.put("number", number);
			dataToRecorder.put("context", context);
			dataToRecorder.put("content", smsContent);
			blocker.block(dataToBlocker);
			recorder.recordIfNeed(dataToRecorder);
			notifier.notifyUserIfNeed(context, msgToNotify,
					NotificationNotifier.BLOCK__ITEM_TYPE_SMS);
		}
	}

	private boolean needEndCall(String number, Context mContext) {
		BlackListDBHelper db = BlackListDBHelper.getInstance(mContext);
		CallHandler hd = new CallHandler(mContext);
		String shortnum = hd.getShortNum(number);
		if (hd.isInBlackList(number)) {

			int k = db.queryCall(shortnum);

			if (k == 801 || k == 800) {

				return true;

			}
		}

		return false;
	}

	private boolean needEndSms(String numbers, Context mContext) {
		String number = numbers;
		if (numbers.contains("+86")) {
			number = numbers.substring(3);
		}
		BlackListDBHelper db = BlackListDBHelper.getInstance(mContext);
		CallHandler hd = new CallHandler(mContext);
		String shortnum = hd.getShortNum(number);
		if (hd.isInBlackList(number)) {
			int k = db.queryCall(shortnum);
			if (k == 810 || k == 800) {

				return true;

			}
		}
		return false;
	}

	private boolean isKeyWord(Context context, String str) {
		Logger.i(TAG, "收到短信=" + str);
		SharedPreferences date = context.getSharedPreferences("addrees", 0);
		boolean sk = date.getBoolean("key", true);
		ArrayList<KeyWorder> keys = DisplayCallUitl.getKeyWord();
		for (KeyWorder k : keys) {
			if (str.contains(k.getName()) && sk) {
				Logger.i(TAG, "找到相同=" + str);
				return true;

			}
		}
		return false;
	}
}
