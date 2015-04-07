package com.tblin.HandsetGuardant;

public class BlockType {
	
	/** 电话：未知 **/
	public static int CALL_BLOCK_UNKNOW = -1;
	/** 电话：不拦截 **/
	public static final int CALL_RECEIVE_ALL = 1;
	/** 电话：拦截黑名单 **/
	public static final int CALL_BLOCK_BLACK_LIST = 2;
	/** 电话：拦截黑名单及陌生号码 **/
	public static final int CALL_BLOCK_NOT_IN_CONTACTER = 3;
	/** 电话：仅允许白名单来电 **/
	public static final int CALL_RECEIVE_ONLY_WHITE = 4;
	/** 电话：拦截所有 **/
	public static final int CALL_BLOCK_ALL = 5;
	
	/** 短信：接收所有短信 **/
	public static final int SMS_RECEIVE_ALL = 11;
	/** 短信：拦截黑名单号码短信 **/
	public static final int SMS_BLOCK_BLACK = 12;
	/** 短信：拦截黑名单及陌生号码号码短信 **/
	public static final int SMS_BLOCK_BLACK_STRANGER = 13;
	/** 短信：仅接收白名单短信 **/
	public static final int SMS_RECEIVE_ONLY_WHITE = 14;
	/** 短信：拦截所有短信 **/
	public static final int SMS_BLOCK_ALL = 15;
}
