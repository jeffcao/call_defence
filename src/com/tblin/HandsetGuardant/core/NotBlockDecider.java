package com.tblin.HandsetGuardant.core;

public class NotBlockDecider implements Decider {

	@Override
	public int decideBlockType(String number) {
		return ACTION_NOT_BLOCK;
	}

}
