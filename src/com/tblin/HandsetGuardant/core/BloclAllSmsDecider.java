package com.tblin.HandsetGuardant.core;

public class BloclAllSmsDecider implements Decider {

	@Override
	public int decideBlockType(String number) {
		return ACTION_BLOCK_ALL;
	}

}
