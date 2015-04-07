package com.tblin.HandsetGuardant.core;

import com.tblin.HandsetGuardant.core.BlackDecider;
import com.tblin.HandsetGuardant.core.Decider;
import com.tblin.HandsetGuardant.core.WhiteDecider;

public class SmsBlockWhiteBlackDecider implements Decider {

	private BlackDecider blackDecider;
	private WhiteDecider whiteDecider;

	public SmsBlockWhiteBlackDecider(BlackDecider blackDecider,
			WhiteDecider whiteDecider) {
		super();
		this.blackDecider = blackDecider;
		this.whiteDecider = whiteDecider;
	}

	/**
	 * 先查看是否在白名单里面，再查看是否在黑名单里面
	 * 如果在白名单里面，就直接不拦截了
	 */
	@Override
	public int decideBlockType(String number) {
		int blackType = whiteDecider.decideBlockType(number);
		if (blackType != ACTION_NOT_BLOCK && blackType != ACTION_NOT_BLOCK_AS_IS_WHITE) {
			return blackDecider.decideBlockType(number);
		}
		return blackType;
	}

}
