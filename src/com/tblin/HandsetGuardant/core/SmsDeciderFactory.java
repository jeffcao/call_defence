package com.tblin.HandsetGuardant.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.tblin.HandsetGuardant.BlackListDBHelper;
import com.tblin.HandsetGuardant.BlackUser;
import com.tblin.HandsetGuardant.BlockType;
import com.tblin.HandsetGuardant.CallHandler;
import com.tblin.HandsetGuardant.ContacterDBHelper;
import com.tblin.HandsetGuardant.Logger;
import com.tblin.HandsetGuardant.SettingPreferenceHandler;
import com.tblin.HandsetGuardant.WhiteListDBHelper;
import com.tblin.HandsetGuardant.WhiteUser;

public class SmsDeciderFactory {
	
	private static final String TAG = "SmsDeciderFactory";
	
	public static Decider getDecider(int fireMode, Context context) {
		Decider decider = null;
		SettingPreferenceHandler sph = SettingPreferenceHandler.getInstance();
		switch (fireMode) {
		case BlockType.SMS_RECEIVE_ALL:
			Logger.i(TAG, "fire mode SMS_BLOCK_TYPE_RECEIVE_ALL");
			decider = new NotBlockDecider();
			break;
		case BlockType.SMS_BLOCK_BLACK:
			Logger.i(TAG, "fire mode SMS_BLOCK_TYPE_BLOCK_BLACK");
			BlackDecider black_decider = getBlackDecider(context, sph);
			WhiteDecider white_decider = getWhiteDBDecider(context, null);
			decider = new SmsBlockWhiteBlackDecider(black_decider, white_decider);
			break;
		case BlockType.SMS_BLOCK_BLACK_STRANGER:
			Logger.i(TAG, "fire mode SMS_BLOCK_TYPE_BLOCK_BLACK_STRANGER");
			BlackDecider blackDecider = getBlackDecider(context, sph);
			ContacterDBHelper db2 = ContacterDBHelper.getInstance(context);
			WhiteDecider whiteDecider = getWhiteDBDecider(context, db2.getAllMobiles());
			decider = new SmsBlockBlackWhiteDecider(blackDecider,
					whiteDecider);
			break;
		case BlockType.SMS_BLOCK_ALL:
			Logger.i(TAG, "fire mode SMS_BLOCK_TYPE_BLOCK_ALL");
			decider = new BloclAllSmsDecider();
			break;
		case BlockType.SMS_RECEIVE_ONLY_WHITE:
			Logger.i(TAG, "fire mode SMS_RECEIVE_ONLY_WHITE");
			decider = getWhiteDBDecider(context, null);
			break;
		}
		Logger.i(TAG, "decider is " + decider);
		return decider;
	}
	
	private static WhiteDecider getWhiteDecider(List<String> mobiles) {
		return new WhiteDecider(mobiles,
				Decider.ACTION_WHITE_DEFULAT_BLOCK_TYPE);
	}
	
	private static WhiteDecider getWhiteDBDecider(Context mContext, List<String> mobilesExtra) {
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

	private static BlackDecider getBlackDecider(Context context,
			SettingPreferenceHandler sph) {
		BlackListDBHelper db1 = BlackListDBHelper.getInstance(context);

		// TODO
		List<BlackUser> blackUsers = db1.getAllUsers();
		List<BlackItemDecider> deciders = new ArrayList<BlackItemDecider>();
		String nativePrefix = sph.getNativePrefix();
		if (sph.getBlockNative()) {
			BlackItemDecider itemDecider = new BlackItemDecider(nativePrefix,
					db1.queryBlock(nativePrefix), nativePrefix);
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
}
