package com.tblin.firewall.core;

public class NotBlockDecider implements Decider {

	@Override
	public int decideBlockType(String number) {
		return NOT_BLOCK;
	}

}
