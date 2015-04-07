package com.tblin.firewall.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.tblin.firewall.BlackListDBHelper;
import com.tblin.firewall.BlackUser;
import com.tblin.firewall.ContacterDBHelper;
import com.tblin.firewall.SettingPreferenceHandler;
import com.tblin.firewall.BlackListDBHelper.OnDataChanged;
import com.tblin.firewall.ContacterDBHelper.OnSyncListener;
import com.tblin.firewall.SettingPreferenceHandler.OnBlockSetChange;

public class ProcesserManager {
	private static ProcesserManager INSTANCE;
	private Context mContext;
	private Processer smsProcesser;
	private Processer callProcesser;
	private boolean inited;
	private SettingPreferenceHandler sph;
	public static final int BLOCK_TYPE_UNKNOW = -1;
	public static final int BLOCK_TYPE_BLOCK_BLACK_LIST = 1;
	public static final int BLOCK_TYPE_BLOCK_NOT_IN_CONTACTER = 2;
	public static final int BLOCK_TYPE_BLOCK_NOT_BLOCK = 3;

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
						onDataChanged();
					}
				});
		BlackListDBHelper.getInstance(mContext).registDataLsnr(
				new OnDataChanged() {

					@Override
					public void onDataChange() {
						if (sph.getFireMode() == BLOCK_TYPE_BLOCK_BLACK_LIST)
							onDataChanged();
					}
				});
		ContacterDBHelper.getInstance(mContext).registSyncListener(
				new OnSyncListener() {

					@Override
					public void onSync() {
						if (sph.getFireMode() == BLOCK_TYPE_BLOCK_NOT_IN_CONTACTER)
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
		Decider decider = getDecider(firemode);
		Blocker blocker = new CallBlocker();
		Recorder recorder = new CallRecorder();
		Notifier notifier = new NotificationNotifier();
		callProcesser = new Processer(decider, blocker, recorder, notifier);
	}

	private void initSmsProcesser() {
		int firemode = sph.getFireMode();
		Decider decider = getDecider(firemode);
		Blocker blocker = new SmsBlocker();
		Recorder recorder = new SmsRecorder();
		Notifier notifier = new NotificationNotifier();
		smsProcesser = new Processer(decider, blocker, recorder, notifier);
	}

	private Decider getDecider(int firemode) {
		Decider decider = null;
		switch (firemode) {
		case BLOCK_TYPE_BLOCK_BLACK_LIST:
			BlackListDBHelper db1 = BlackListDBHelper.getInstance(mContext);
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
			decider = new BlackDecider(deciders);
			break;
		case BLOCK_TYPE_BLOCK_NOT_IN_CONTACTER:
			ContacterDBHelper db2 = ContacterDBHelper.getInstance(mContext);
			decider = new WhiteDecider(db2.getAllMobiles(),
					Decider.WHITE_DEFULAT_BLOCK_TYPE);
			break;
		default:
			decider = new NotBlockDecider();
			break;
		}
		return decider;
	}

	public Processer getSmsProcesser() {
		return smsProcesser;
	}

	public Processer getCallProcesser() {
		return callProcesser;
	}
}
