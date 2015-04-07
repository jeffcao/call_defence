package com.tblin.HandsetGuardant.core;

import java.util.Map;

public interface Recorder {
	/**
	 * all Recorder need data: "context"-Context
	 * 
	 * CallRecorder data: "number"-String(the number blocked)
	 * 
	 * @param data
	 */
	void recordIfNeed(Map<String, Object> data);
}
