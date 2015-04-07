package com.tblin.HandsetGuardant.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.tblin.HandsetGuardant.BlackListDBHelper;
import com.tblin.HandsetGuardant.BlackUser;
import com.tblin.HandsetGuardant.BlockType;
import com.tblin.HandsetGuardant.ContacterDBHelper;
import com.tblin.HandsetGuardant.DisplayCallService;
import com.tblin.HandsetGuardant.DisplayCallUitl;
import com.tblin.HandsetGuardant.Logger;
import com.tblin.HandsetGuardant.SettingPreferenceHandler;
import com.tblin.HandsetGuardant.WhiteListDBHelper;
import com.tblin.HandsetGuardant.WhiteUser;
import com.tblin.HandsetGuardant.BlackListDBHelper.OnDataChanged;
import com.tblin.HandsetGuardant.ContacterDBHelper.OnSyncListener;
import com.tblin.HandsetGuardant.SettingPreferenceHandler.OnBlockSetChange;

public class ProcesserManager {
	private static ProcesserManager INSTANCE;
	private static final String TAG = ProcesserManager.class.toString();
	private Context mContext;
	private Processer smsProcesser;
	private Processer callProcesser;
	private boolean inited;
	private SettingPreferenceHandler sph;/*
	public static final int BLOCK_TYPE_UNKNOW = -1;
	public static final int BLOCK_TYPE_BLOCK_BLACK_LIST = 2;
	public static final int BLOCK_TYPE_BLOCK_NOT_IN_CONTACTER = 3;
	public static final int BLOCK_TYPE_BLOCK_NOT_BLOCK = 1;
	public static final int BLOCK_TYPE_BLOCK_ALL = 4;*/
	public int myType;
	private ProcesserManager() {
	}

	public static ProcesserManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ProcesserManager();
		} else if (!INSTANCE.inited) {
			throw new IllegalAccessError("you must init before use");
		}
		return INSTANCE;
	}

	public void init(Context context) {
		mContext = context;
		sph = SettingPreferenceHandler.getInstance();
		initCallProcesser();
		initSmsProcesser();
		registDataListeners();
		inited = true;
		
	}

	private void registDataListeners() {
		SettingPreferenceHandler.getInstance().registLsnr(
				new OnBlockSetChange() {

					@Override
					public void onSetChange() {
						Logger.i(TAG, "SettingPreferenceHandler onDataChanged");
						onDataChanged();
					}
				});
		WhiteListDBHelper.getInstance(mContext).registDataLsnr(new WhiteListDBHelper.OnDataChanged() {
			
			@Override
			public void onDataChange() {
				Logger.i(TAG, "WhiteListDBHelper onDataChanged");
				onDataChanged();
			}
		});
		BlackListDBHelper.getInstance(mContext).registDataLsnr(
				new OnDataChanged() {

					@Override
					public void onDataChange() {
						Logger.i(TAG, "BlackListDBHelper onDataChanged");
						Logger.i(TAG, "setactivity000==="+sph.getFireMode());
						onDataChanged();
					}
				});
		ContacterDBHelper.getInstance(mContext).registSyncListener(
				new OnSyncListener() {

					@Override
					public void onSync() {
						Logger.i(TAG, "ContacterDBHelper onDataChanged");
						Logger.i(TAG, "setactivity111==="+sph.getFireMode());
						onDataChanged();
					}
				});
	}

	private void onDataChanged() {
		smsProcesser = null;
		callProcesser = null;
		System.gc();
		initCallProcesser();
		initSmsProcesser();
	}

	private void initCallProcesser() {
		int firemode = sph.getFireMode();
		Logger.i(TAG, "setactivity222==="+sph.getFireMode());
		Decider decider = getDecider(firemode);
		Logger.i(TAG, "get call decider is " + decider);
		Blocker blocker = new CallBlocker();
		Recorder recorder = new CallRecorder();
		Notifier notifier = new NotificationNotifier();
		callProcesser = new Processer(decider, blocker, recorder, notifier ,myType);
		
	}

	private void initSmsProcesser() {
		//int firemode = sph.getFireMode();
		
		//Decider decider = getDecider(firemode);
		int firemode = sph.getSmsFireMode();
		Logger.i(TAG, "sms block type is " + firemode);
		Decider decider = SmsDeciderFactory.getDecider(firemode, mContext);
		Logger.i(TAG, "get decider is " + decider);
		Blocker blocker = new SmsBlocker();
		Recorder recorder = new SmsRecorder();
		Notifier notifier = new NotificationNotifier();
		smsProcesser = new Processer(decider, blocker, recorder, notifier,myType);
		
	}

	private Decider getDecider(int firemode) {
		Decider decider = null;
		DisplayCallUitl.saveDB(mContext);
		switch (firemode) {
		case BlockType.CALL_BLOCK_BLACK_LIST:
			BlackDecider black_decider = getBlackDecider();
			decider = new CallBlockWhiteBlackDecider(black_decider,
					getWhiteDBDecider(null));
			myType=BlockType.CALL_BLOCK_BLACK_LIST;
			break;
		case BlockType.CALL_BLOCK_NOT_IN_CONTACTER:
			BlackDecider blackDecider = getBlackDecider();
			ContacterDBHelper db2 = ContacterDBHelper.getInstance(mContext);
			//WhiteDecider whiteDecider = getContactWhiteDecider();
			WhiteDecider whiteDecider = getWhiteDBDecider(db2.getAllMobiles());
			decider = new CallBlockBlackWhiteDecider(blackDecider,
					whiteDecider);
			myType=BlockType.CALL_BLOCK_NOT_IN_CONTACTER;
			break;
		case BlockType.CALL_BLOCK_ALL:
			decider = new BlockAllCallDecider();
			break;
		case BlockType.CALL_RECEIVE_ONLY_WHITE:
			decider = getWhiteDBDecider(null);
			break;
		default:
			decider = new NotBlockDecider();
			break;
		}
		return decider;
	}
	
	private WhiteDecider getWhiteDecider(List<String> mobiles) {
		return new WhiteDecider(mobiles,
				Decider.ACTION_WHITE_DEFULAT_BLOCK_TYPE);
	}
	
	private WhiteDecider getWhiteDBDecider(List<String> mobilesExtra) {
		WhiteListDBHelper db = WhiteListDBHelper.getInstance(mContext);
		List<WhiteUser> users = db.getAllUsers();
		List<String> mobiles = new ArrayList<String>();
		for (WhiteUser usr : users) {
			mobiles.add(usr.mobile);
		}
		if (null != mobilesExtra && !mobilesExtra.isEmpty()) {
			mobiles.addAll(mobilesExtra);
		}
		return getWhiteDecider(mobiles);
	}

	private BlackDecider getBlackDecider() {
		BlackListDBHelper db1 = BlackListDBHelper.getInstance(mContext);
		
		//TODO
		List<BlackUser> blackUsers = db1.getAllUsers();
		List<BlackItemDecider> deciders = new ArrayList<BlackItemDecider>();
		String nativePrefix = sph.getNativePrefix();
		if (sph.getBlockNative()) {
			BlackItemDecider itemDecider = new BlackItemDecider(
					nativePrefix, db1.queryBlock(nativePrefix),
					nativePrefix);
			deciders.add(itemDecider);
		}
		for (BlackUser user : blackUsers) {
			int blockType = db1.queryBlock(user.getMobile());
		
		
			BlackItemDecider itemDecider = new BlackItemDecider(
					user.getMobile(), blockType, nativePrefix);
			deciders.add(itemDecider);
		}
		return new BlackDecider(deciders);
	}

	public Processer getSmsProcesser() {
		return smsProcesser;
	}

	public Processer getCallProcesser() {
		return callProcesser;
	}
}
