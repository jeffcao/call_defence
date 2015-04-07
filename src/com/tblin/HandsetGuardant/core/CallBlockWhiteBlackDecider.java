package com.tblin.HandsetGuardant.core;

public class CallBlockWhiteBlackDecider extends SmsBlockWhiteBlackDecider {

	public CallBlockWhiteBlackDecider(BlackDecider blackDecider,
			WhiteDecider whiteDecider) {
		super(blackDecider, whiteDecider);
	}

}
