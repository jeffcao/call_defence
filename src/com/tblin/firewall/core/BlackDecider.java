package com.tblin.firewall.core;

import java.util.List;

import com.tblin.firewall.Logger;

public class BlackDecider implements Decider {

	private List<BlackItemDecider> deciders;
	private static final String TAG = BlackDecider.class.toString();

	public BlackDecider(List<BlackItemDecider> deciders) {
		this.deciders = deciders;
	}

	@Override
	public int decideBlockType(String number) {
		Logger.i(TAG, "BlackDecider start");
		for (BlackItemDecider decider : deciders) {
			int type = decider.decideBlockType(number);
			if (type != NOT_BLOCK)
				return type;
		}
		return NOT_BLOCK;
	}

}
