package com.tblin.firewall.core;

public interface Decider {
	public static final int NOT_BLOCK = -1;
	public static final int CALL_BLOCK_END = 1;
	public static final int CALL_BLOCK_SILENCE = 2;
	public static final int SMS_BLOCK_END = 3;
	public static final int WHITE_DEFULAT_BLOCK_TYPE = 4;

	int decideBlockType(String number);
}
