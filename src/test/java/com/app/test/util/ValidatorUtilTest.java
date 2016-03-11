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

package com.app.test.util;

import com.app.util.ValidatorUtil;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan McCann
 */
public class ValidatorUtilTest {

	@Test
	public void testNullString() {
		Assert.assertTrue(ValidatorUtil.isNull((String)null));
		Assert.assertTrue(ValidatorUtil.isNull(""));
		Assert.assertTrue(ValidatorUtil.isNull("null"));
		Assert.assertTrue(ValidatorUtil.isNull("NULL"));

		Assert.assertFalse(ValidatorUtil.isNotNull((String) null));
		Assert.assertFalse(ValidatorUtil.isNotNull(""));
		Assert.assertFalse(ValidatorUtil.isNotNull("null"));
		Assert.assertFalse(ValidatorUtil.isNotNull("NULL"));
	}

	@Test
	public void testNonNullString() {
		Assert.assertFalse(ValidatorUtil.isNull("test"));

		Assert.assertTrue(ValidatorUtil.isNotNull("test"));
	}

	@Test
	public void testNullArray() {
		Assert.assertTrue(ValidatorUtil.isNull((Object[])null));
		Assert.assertTrue(ValidatorUtil.isNull(new String[0]));
		Assert.assertTrue(ValidatorUtil.isNull(new String[1]));

		Assert.assertFalse(ValidatorUtil.isNotNull((Object[]) null));
		Assert.assertFalse(ValidatorUtil.isNotNull(new String[0]));
		Assert.assertFalse(ValidatorUtil.isNotNull(new String[1]));
	}

	@Test
	public void testNonNullArray() {
		String[] stringArray = new String[1];
		stringArray[0] = "test";

		Assert.assertFalse(ValidatorUtil.isNull(stringArray));
		Assert.assertTrue(ValidatorUtil.isNotNull(stringArray));

		Integer[] intArray = new Integer[1];
		intArray[0] = 1;

		Assert.assertFalse(ValidatorUtil.isNull(intArray));
		Assert.assertTrue(ValidatorUtil.isNotNull(intArray));
	}

	@Test
	public void testInvalidEmailAddress() {
		Assert.assertFalse(
			ValidatorUtil.isValidEmailAddress("invalidEmailAddress"));
		Assert.assertFalse(
			ValidatorUtil.isValidEmailAddress("invalidEmailAddress#test.com"));
	}

	@Test
	public void testNullEmailAddress() {
		Assert.assertFalse(ValidatorUtil.isValidEmailAddress(null));
		Assert.assertFalse(ValidatorUtil.isValidEmailAddress(""));
		Assert.assertFalse(ValidatorUtil.isValidEmailAddress(" "));
	}

	@Test
	public void testValidEmailAddress() throws Exception {
		Assert.assertTrue(
			ValidatorUtil.isValidEmailAddress("test@test"));
		Assert.assertTrue(
			ValidatorUtil.isValidEmailAddress("test@test.com"));
		Assert.assertTrue(
			ValidatorUtil.isValidEmailAddress("test2@test2.com"));
	}

	@Test
	public void testInvalidPhoneNumber() {
		Assert.assertFalse(ValidatorUtil.isValidPhoneNumber("1234"));
		Assert.assertFalse(ValidatorUtil.isValidPhoneNumber("123-456-7890"));
		Assert.assertFalse(ValidatorUtil.isValidPhoneNumber("12345678901"));
		Assert.assertFalse(ValidatorUtil.isValidPhoneNumber("test"));
	}

	@Test
	public void testNullPhoneNumber() {
		Assert.assertFalse(ValidatorUtil.isValidPhoneNumber(null));
		Assert.assertFalse(ValidatorUtil.isValidPhoneNumber(""));
		Assert.assertFalse(ValidatorUtil.isValidPhoneNumber(" "));
	}

	@Test
	public void testValidPhoneNumbers() throws Exception {
		Assert.assertTrue(ValidatorUtil.isValidPhoneNumber("1234567890"));
		Assert.assertTrue(ValidatorUtil.isValidPhoneNumber("2345678901"));
	}

}