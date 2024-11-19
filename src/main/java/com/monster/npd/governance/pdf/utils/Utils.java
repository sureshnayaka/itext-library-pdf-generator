package com.monster.npd.governance.pdf.utils;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * 
 * @author SureshNayak
 *
 */
public class Utils {


	/**
	 * @apiNote Utility method to check for empty values
	 * @param value
	 * @return
	 */
	public static boolean isNullOrEmptyString(String value) {
		boolean stringValue = false;
		if (value == null || value.isEmpty() || value.isBlank() || "".equals(value)) {
			stringValue = true;
		}
		return stringValue;
	}

	public static boolean isNullOrEmptyInt(int value) {
		boolean intValue = false;
		if (value == 0 || (Integer.valueOf(value) == null)) {
			intValue = true;
		}
		return intValue;
	}

	public static boolean isNullOrEmptyInteger(Integer value) {
		boolean integerValue = false;
		if (value == 0 || value == null) {
			integerValue = true;
		}
		return integerValue;
	}

	public static boolean isNullOrEmptyLong(long value) {
		boolean longValue = false;
		if (value == 0 || (Long.valueOf(value) == null)) {
			longValue = true;
		}
		return longValue;
	}

	public static boolean isNullOrEmptyObject(Object obj) {
		boolean objValue = false;
		if (obj == null || obj.equals("")) {
			objValue = true;
		}
		return objValue;
	}

	public static boolean isNullOrEmptyNode(JsonNode obj) {
		boolean objValue = false;
		if (obj == null || obj.isNull()) {
			objValue = true;
		}
		return objValue;
	}

	public static boolean isNullOrEmptyDate(Date value) {
		boolean dateValue = false;
		if (value == null) {
			dateValue = true;
		}
		return dateValue;
	}

	public static boolean isListNull(List<Object> value) {
		boolean isNull = false;
		if (value == null || value.isEmpty()) {
			isNull = true;
		}

		for (Object obj : value) {
			if (obj == null || "".equals(obj)) {
				isNull = true;
				break;
			}
		}
		return isNull;
	}

	private Utils() {

	}
}