package com.tblin.firewall.core;

import java.util.regex.Pattern;

public class BlackItemDecider implements Decider {

	private String str;
	private int type;
	private String nativePrefix;
	private Pattern pattern;
	private static final String PARTTERN_TEXT = "(^[\\+,0]\\d{1,3}bla)|(^bla)";

	// private static final String TAG = BlackItemDecider.class.toString();

	public BlackItemDecider(String str, int type, String nativePrefix) {
		this.str = str;
		this.type = type;
		this.nativePrefix = nativePrefix;
		this.pattern = Pattern.compile(PARTTERN_TEXT.replace("bla", str));
	}

	private boolean match(String number) {
		if (number == null || "".equals(number))
			return false;
		if (str.equals(nativePrefix) && !number.startsWith("+"))
			return true;
		return pattern.matcher(number).find();
	}

	@Override
	public int decideBlockType(String number) {
		if (match(number))
			return type;
		return NOT_BLOCK;
	}

}
