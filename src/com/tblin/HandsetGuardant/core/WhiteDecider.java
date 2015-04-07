package com.tblin.HandsetGuardant.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

import com.tblin.HandsetGuardant.Logger;

public class WhiteDecider implements Decider{
	
	private List<Long> whiteNoAdd;
	private List<Long> whiteWithAdd;
	private int blockType;
	private static final String TAG = WhiteDecider.class.toString();
	
	public WhiteDecider(List<String> whites, int blockType) {
		this.blockType = blockType;
		generateWhite(whites);
	}

	@Override
	public int decideBlockType(String number) {
		Logger.i(TAG, "white decider start");
		Logger.i(TAG, "iswhiteMatch===" + isWhiteMatch(number));
		
		if (isWhiteMatch(number)) {
			//return ACTION_NOT_BLOCK;
			return ACTION_NOT_BLOCK_AS_IS_WHITE;
		} else {
			return ACTION_NOT_WHITE;
		}
	}
	
	private void generateWhite(List<String> whites) {
		List<String> tempList=new ArrayList<String>();
		for(String s:whites){
			String ss=s.replace(" ", "");
			     tempList.add(ss);
			       
			 }

		
		
		
		whiteNoAdd = new ArrayList<Long>();
		whiteWithAdd = new ArrayList<Long>();
		for (String white : tempList)
			if (white.startsWith("+"))
					whiteWithAdd.add(removeMobileAdd(white));
			else {
				try {
					Long value = Long.parseLong(white);
					whiteNoAdd.add(value);
				} catch (Exception e) {
					continue;
				}
			}
		sort(whiteNoAdd);
		sort(whiteWithAdd);
		
	}

	private boolean isWhiteMatch(String number) {
		Logger.i(TAG,"decide number " + number);
		removeMobileAdd(number);
		if (number.startsWith("+")) {
			for (Long mbl : whiteNoAdd)
				if (number.contains(Long.toString(mbl)))
					return true;
			return Collections.binarySearch(whiteWithAdd,
					removeMobileAdd(number)) >= 0;
					
		} else {
			for (Long mbl : whiteWithAdd)
				if (Long.toString(mbl).contains(number))
					return true;
			return Collections
					.binarySearch(whiteNoAdd, removeMobileAdd(number)) >= 0;
		}
	}

	private void sort(List<Long> data) {
		Collections.sort(data, new Comparator<Long>() {

			@Override
			public int compare(Long object1, Long object2) {
				return object1 > object2 ? 1 : -1;
			}
		});
	}

	private long removeMobileAdd(String mobile) {
		if (null == mobile || mobile.length() == 0) {
			return -1;
		}
		while (mobile.charAt(0) == '+' || mobile.charAt(0) == '0') {
			mobile = mobile.substring(1);
		}
		mobile.replaceAll(" ", "");
		try {
			return Long.parseLong(mobile.replace(" ", ""));
		} catch (Exception e) {
			return 0;
		}
	}

}
