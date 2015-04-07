package com.tblin.HandsetGuardant.sel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Contacter {
	public long time;
	public String name;
	public String mobile;
	public String pinyin;
	
	@Override
	public String toString() {
		String str = "name:" + name + ", mobile: " + mobile + ", pinyin: " + pinyin;
		return str.toString();
	}
	
	public static List<Contacter> filter(List<Contacter> source, String str) {
		List<Contacter> contacters = new ArrayList<Contacter>();
		if (null == source || source.isEmpty()) {
			return contacters;
		}
		for(Contacter contacter : source) {
			if (isChecked(contacter, str)) {
				contacters.add(contacter);
			}
		}
		return contacters;
	}
	
	private static boolean isChecked(Contacter contacter, String str) {
		String name = contacter.name.toLowerCase(Locale.CHINESE);
		str = str.toLowerCase(Locale.CHINESE);
		String pinyin = contacter.pinyin != null ? contacter.pinyin.toLowerCase(Locale.CHINESE) : null;
		
		//check name
		if (name.contains(str)) {
			return true;
		}
		
		//check mobile
		if (contacter.mobile.contains(str)) {
			return true;
		}
		
		//check pinyin
		if (null != pinyin && pinyin.contains(str)) {
			return true;
		}
		
		return false;
	}
	
	public static void sort(List<Contacter> contacters) {
		Collections.sort(contacters, cont);
	}
	
	public static void sortReverse(List<Contacter> contacters) {
		Collections.sort(contacters, cont_reverse);
	}
	
	public static Comparator<Contacter> cont = new Comparator<Contacter>() {
		
		@Override
		public int compare(Contacter lhs, Contacter rhs) {
			if (lhs.time > 0 && rhs.time > 0) {
				return lhs.time - rhs.time > 0 ? 1 : -1;
			}
			String name1 = null != lhs.pinyin ? lhs.pinyin : lhs.name;
			String name2 = null != rhs.pinyin ? rhs.pinyin : rhs.name;
			return name1.compareToIgnoreCase(name2);
		}
	};
	
	public static Comparator<Contacter> cont_reverse = new Comparator<Contacter>() {
		
		@Override
		public int compare(Contacter lhs, Contacter rhs) {
			if (lhs.time > 0 && rhs.time > 0) {
				return lhs.time - rhs.time < 0 ? 1 : -1;
			}
			String name1 = null != lhs.pinyin ? lhs.pinyin : lhs.name;
			String name2 = null != rhs.pinyin ? rhs.pinyin : rhs.name;
			return name1.compareToIgnoreCase(name2);
		}
	};
}
