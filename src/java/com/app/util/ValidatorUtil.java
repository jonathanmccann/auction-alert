/**
 * Copyright (c) 2014-present Jonathan McCann
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package com.app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jonathan McCann
 */
public class ValidatorUtil {

	public static boolean isNotNull(String s) {
		return !isNull(s);
	}

	public static boolean isNotNull(Object[] array) {
		return !isNull(array);
	}

	public static boolean isNull(Object[] array) {
		if ((array == null) || (array.length == 0)) {
			return true;
		}

		for (Object o : array) {
			if (o == null) {
				return true;
			}
		}

		return false;
	}

	public static boolean isNull(String s) {
		if ((s == null) || s.isEmpty() || s.equalsIgnoreCase("null")) {
			return true;
		}

		return false;
	}

	public static boolean isValidEmailAddress(String emailAddress) {
		Matcher matcher = _emailAddressPattern.matcher(emailAddress);

		return matcher.matches();
	}

	public static boolean isValidPhoneNumber(String emailAddress) {
		Matcher matcher = _phoneNumberPattern.matcher(emailAddress);

		return matcher.matches();
	}

	private static final Pattern _emailAddressPattern = Pattern.compile(
		"[a-zA-Z0-9]*@[a-zA-Z0-9]*\\.[a-zA-Z]{1,6}");
	private static final Pattern _phoneNumberPattern = Pattern.compile(
		"[0-9]{10,10}");

}