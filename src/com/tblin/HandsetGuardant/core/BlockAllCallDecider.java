package com.tblin.HandsetGuardant.core;

public class BlockAllCallDecider implements Decider {

	@Override
	public int decideBlockType(String number) {
		return ACTION_BLOCK_ALL;
	}

}
