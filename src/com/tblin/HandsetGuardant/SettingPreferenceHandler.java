package com.tblin.HandsetGuardant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class SettingPreferenceHandler {

	private SharedPreferences pre;
	private Resources resource;
	private SharedPreferences.Editor spe;
	private static Context mContext;
	private static SettingPreferenceHandler INSTANCE;
	private static final String TAG = SettingPreferenceHandler.class.toString();
	
	private OnBlockSetChange lsnr;
	public interface OnBlockSetChange {
		void onSetChange();
	}

	public void registLsnr(OnBlockSetChange lsnr) {
		this.lsnr = lsnr;
	}

	private SettingPreferenceHandler(){}
	
	public static SettingPreferenceHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SettingPreferenceHandler();
		} else if (mContext == null) {
			throw new IllegalAccessError("you must init before use");
		}
		return INSTANCE;
	}
	
	public void init(Context context) {
		mContext = context;
		pre = PreferenceManager.getDefaultSharedPreferences(context);
		spe = pre.edit();
		resource = context.getResources();
	}

	public void initAll() {
		spe.putBoolean(resource.getString(R.string.toast_when_block_key), true);
		spe.putBoolean(resource.getString(R.string.record_when_block_key), true);
//		spe.putString(resource.getString(R.string.fire_mode_key),
//				Integer.toString(CallHandler.BLOCK_TYPE_BLOCK_NOT_IN_CONTACTER));
//		spe.putString(resource.getString(R.string.sms_fire_mode_key),
//				Integer.toString(CallHandler.SMS_BLOCK_TYPE_BLOCK_BLACK));
		spe.putString(resource.getString(R.string.block_type_key),
				Integer.toString(BusySetter.TYPE_NORMAL));
		String phonePrefix = PhonePrefixHelper.getCountryPhonePrefix(mContext);
		if (phonePrefix != null) {
			spe.putString(resource.getString(R.string.native_prefix_key),
					phonePrefix);
		}
		spe.commit();
	}
	
	public long getLastIntentTime() {
		return pre.getLong(resource.getString(R.string.last_intent_time), 0);
	}
	
	public boolean setLastIntentTime(long value) {
		spe.putLong(resource.getString(R.string.last_intent_time), value);
		return spe.commit();
	}

	public long getUpdateTime() {
		return pre.getLong(resource.getString(R.string.update_time_key), 0);
	}

	public boolean setUpdateTime(long value) {
		spe.putLong(resource.getString(R.string.update_time_key), value);
		return spe.commit();
	}

	public String getNativePrefix() {
		return pre.getString(resource.getString(R.string.native_prefix_key),
				PhonePrefixHelper.UNKNOW_PRE);
	}

	public void setNativePrefix(String prefix) {
		if (prefix == null) {
			return;
		}
		String initial = getNativePrefix();
		if (initial.equals(prefix))
			return;
		spe.putString(resource.getString(R.string.native_prefix_key), prefix);
		spe.commit();
		if (lsnr != null)
			lsnr.onSetChange();
	}

	public boolean getBlockNative() {
		return pre.getBoolean(resource.getString(R.string.native_block_key),
				false);
	}

	public void setBlockNatvie(boolean value) {
		boolean initial = getBlockNative();
		if (initial == value)
			return;
		spe.putBoolean(resource.getString(R.string.native_block_key), value);
		spe.commit();
		if (lsnr != null)
			lsnr.onSetChange();
	}

	public boolean getToastWhenBlock() {
		return pre.getBoolean(
				resource.getString(R.string.toast_when_block_key), false);
	}

	public void setToastWhenBlock(boolean value) {
		spe.putBoolean(resource.getString(R.string.toast_when_block_key), value);
		spe.commit();
	}

	public int getFireMode() {
		String mo = pre.getString(resource.getString(R.string.fire_mode_key),
				Integer.toString(BlockType.CALL_BLOCK_BLACK_LIST));
		int mode = Integer.parseInt(mo);
		return mode;
	}

	public void setFireMode(int value) {
		if (value < 1 || value > 5) {
			return;
		}
		int initial = getFireMode();
		if (initial == value)
			return;
		spe.putString(resource.getString(R.string.fire_mode_key),
				Integer.toString(value));
		spe.commit();
		if (lsnr != null)
			lsnr.onSetChange();
	}
	
	public int getSmsFireMode() {
		String mo = pre.getString(resource.getString(R.string.sms_fire_mode_key),
				Integer.toString(BlockType.SMS_BLOCK_BLACK));
		int mode = Integer.parseInt(mo);
		return mode;
	}

	public void setSmsFireMode(int value) {
		if (value < 11 || value > 15) {
			return;
		}
		int initial = getSmsFireMode();
		if (initial == value)
			return;
		spe.putString(resource.getString(R.string.sms_fire_mode_key),
				Integer.toString(value));
		spe.commit();
		if (lsnr != null)
			lsnr.onSetChange();
	}

	public int getBlockType() {
		String ty = pre.getString(resource.getString(R.string.block_type_key),
				Integer.toString(BusySetter.TYPE_UNKNOW));
		int type = Integer.parseInt(ty);
		return type;
	}

	public void setBlockType(int value) {
		if (value < 0 || value > 3) {
			return;
		}
		spe.putString(resource.getString(R.string.block_type_key),
				Integer.toString(value));
		spe.commit();
	}

	public boolean getRecordCondition() {
		return pre.getBoolean(
				resource.getString(R.string.record_when_block_key), false);
	}

	public void setRecord(boolean value) {
		spe.putBoolean(resource.getString(R.string.record_when_block_key),
				value);
		spe.commit();
	}
	public boolean getViewAddCondition() {
		return pre.getBoolean(
				"开启来去电归属地显示", true);
	}

	public void setViewAdd(boolean value) {
		spe.putBoolean("开启来去电归属地显示",
				value);
		spe.commit();
	}
}
