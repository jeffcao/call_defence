package com.tblin.firewall.core;

import java.util.Map;

public interface Blocker {
	/**
	 * CallBlocker param: "context":Context(context) "type"-int(type) "number"-String(block number) 
	 * SmsBlokcer param: "receiver"-BroadcastReceiver(the receiver to do abort) "type"-int(type)
	 * 
	 * @param param
	 */
	void block(Map<String, Object> data);
}
