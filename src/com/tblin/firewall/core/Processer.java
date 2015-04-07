package com.tblin.firewall.core;

import java.util.HashMap;
import java.util.Map;

import com.tblin.firewall.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;

public class Processer {
	private Decider decider;
	private Blocker blocker;
	private Recorder recorder;
	private Notifier notifier;
	private static final String TAG = Processer.class.toString();

	public Processer(Decider decider, Blocker blocker, Recorder recorder,
			Notifier notifier) {
		this.decider = decider;
		this.blocker = blocker;
		this.recorder = recorder;
		this.notifier = notifier;
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
	 *//*
	public void processIncomingNumber(String number,
			Map<String, Object> dataToBlocker,
			Map<String, Object> dataToRecorder, Context context,
			String msgToNotifier) {
		if (null == number || "".equals(number))
			return;
		int blockType = decider.decideBlockType(number);
		if (blockType == Decider.NOT_BLOCK)
			return;
		if (dataToBlocker == null)
			dataToBlocker = new HashMap<String, Object>();
		dataToBlocker.put("context", context);
		dataToBlocker.put("number", number);
		dataToBlocker.put("type", blockType);
		blocker.block(dataToBlocker);
		recorder.recordIfNeed(dataToRecorder);
		notifier.notifyUserIfNeed(context, msgToNotifier);
	}*/

	/**
	 * 处理来电
	 * @param number 要处理的来电号码
	 * @param context context
	 * @param msgToNotify 提示到通知栏时显示的文字
	 */
	public void processIncomingCall(String number, Context context,
			String msgToNotify) {
		Logger.i(TAG, "start process incoming call " + number);
		if (null == number || "".equals(number))
			return;
		int blockType = decider.decideBlockType(number);
		if (blockType == Decider.NOT_BLOCK)
			return;
		Map<String, Object> dataToBlocker = new HashMap<String, Object>();
		dataToBlocker.put("context", context);
		dataToBlocker.put("number", number);
		dataToBlocker.put("type", blockType);
		Map<String, Object> dataToRecorder = new HashMap<String, Object>();
		dataToRecorder.put("number", number);
		dataToRecorder.put("context", context);
		blocker.block(dataToBlocker);
		recorder.recordIfNeed(dataToRecorder);
		notifier.notifyUserIfNeed(context, msgToNotify, NotificationNotifier.BLOCK__ITEM_TYPE_CALL);
	}
	
	public void processIncomingSms(String number, Context context,
			String msgToNotify, String smsContent, BroadcastReceiver receiver) {
		Logger.i(TAG, "start process incoming sms " + number);
		if (null == number || "".equals(number))
			return;
		int blockType = decider.decideBlockType(number);
		if (blockType == Decider.NOT_BLOCK)
			return;
		Map<String, Object> dataToBlocker = new HashMap<String, Object>();
		dataToBlocker.put("receiver", receiver);
		Map<String, Object> dataToRecorder = new HashMap<String, Object>();
		dataToRecorder.put("number", number);
		dataToRecorder.put("context", context);
		dataToRecorder.put("content", smsContent);
		blocker.block(dataToBlocker);
		recorder.recordIfNeed(dataToRecorder);
		notifier.notifyUserIfNeed(context, msgToNotify, NotificationNotifier.BLOCK__ITEM_TYPE_SMS);
	}
	
	

}
