package com.tblin.HandsetGuardant;

import java.util.List;

import android.content.Context;

public class PhoneNumberHelper {

	public static final int MOBILE_PHONE = 1;
	public static final int TELEPHONE = 2;
	public static final int UNKNOW = 3;
	
	public static String trimNumber(String number, Context context){
		String prefix = SettingPreferenceHandler.getInstance().getNativePrefix();
		if (number == null) {
			return null;
		}
		String s = number;
		if(prefix.length() > 0 && number.startsWith(prefix)){
			s = number.substring(prefix.length());
		}
		return s;
	}
	
	public static int numberType(String number, Context context) {
		if (number == null) {
			return UNKNOW;
		}
		String s = trimNumber(number, context);
		if (s.length() < 1) {
			return UNKNOW;
		}
		String c = s.substring(0, 1);
		int firstNumber = Integer.parseInt(c);
		if (firstNumber == 0) {
			return TELEPHONE;
		} else if (firstNumber == 1) {
			return MOBILE_PHONE;
		} else {
			return UNKNOW;
		}
	}
	
	
	public static boolean isPrefixMatch(String number, List<String> prefixs) {
		if (number == null || prefixs.size() == 0) {
			return false;
		}
		String[] numbers = new String[number.length()];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = number.substring(0, i + 1);
		}
		for (String str : prefixs) {
			if (str.length() <= numbers.length && str.equals(numbers[str.length() - 1])) {
				return true;
			}
		}
		return false;
	}
	public static String  gettype(String number, List<String> prefixs) {
		
		if (number == null || prefixs.size() == 0) {
			return null;
		}
		String[] numbers = new String[number.length()];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = number.substring(0, i + 1);
		}
		for (String str : prefixs) {
			if (str.length() <= numbers.length && str.equals(numbers[str.length() - 1])) {
				
				return str;
			}
		}
		return null;
	}
}
