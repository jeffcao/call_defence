package com.tblin.HandsetGuardant.core;

public interface Decider {
	public static final int ACTION_NOT_BLOCK_AS_IS_WHITE = -2;
	public static final int ACTION_NOT_BLOCK = -1;
	public static final int ACTION_CALL_BLOCK_END = 1;
	public static final int ACTION_CALL_BLOCK_SILENCE = 2;
	public static final int ACTION_SMS_BLOCK_END = 3;
	public static final int ACTION_WHITE_DEFULAT_BLOCK_TYPE = 4;
	public static final int ACTION_NOT_WHITE=5;
	public static final int ACTION_BLOCK_ALL = 6;

	int decideBlockType(String number);
}
