package com.tblin.HandsetGuardant.core;

public class SmsBlockBlackWhiteDecider implements Decider {

	private BlackDecider blackDecider;
	private WhiteDecider whiteDecider;

	public SmsBlockBlackWhiteDecider(BlackDecider blackDecider,
			WhiteDecider whiteDecider) {
		super();
		this.blackDecider = blackDecider;
		this.whiteDecider = whiteDecider;
	}

	/**
	 * 先查看是否在黑名单里面，再查看是否在白名单里面
	 * 这里的白名单不是说在白名单里面就不拦截，而是说不在白名单里面就一定拦截
	 */
	@Override
	public int decideBlockType(String number) {
		int blackType = blackDecider.decideBlockType(number);
		if (blackType == ACTION_NOT_BLOCK) {
			return whiteDecider.decideBlockType(number);
		}
		return blackType;
	}

}
