package com.tblin.HandsetGuardant.core;

public class CallBlockBlackWhiteDecider extends SmsBlockBlackWhiteDecider {

	public CallBlockBlackWhiteDecider(BlackDecider blackDecider,
			WhiteDecider whiteDecider) {
		super(blackDecider, whiteDecider);
	}

}
