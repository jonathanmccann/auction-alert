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

package com.app.test.mail;

import com.app.mail.MailUtil;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jonathan McCann
 */
public class MailUtilTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_clazz = Class.forName(MailUtil.class.getName());

		_classInstance = _clazz.newInstance();
	}

	@Test
	public void testGetCurrentDate() throws Exception {
		Method getCurrentDate = _clazz.getDeclaredMethod("getCurrentDate");

		getCurrentDate.setAccessible(true);

		String currentDate = (String)getCurrentDate.invoke(_classInstance);

		Matcher matcher = _DATE_PATTERN.matcher(currentDate);

		Assert.assertTrue(matcher.find());
	}

	private static Object _classInstance;
	private static Class _clazz;

	private static final Pattern _DATE_PATTERN =
		Pattern.compile("\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d");

}