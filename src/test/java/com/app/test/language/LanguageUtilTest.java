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

package com.app.test.language;

import com.app.language.LanguageUtil;
import com.app.test.BaseTestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class LanguageUtilTest extends BaseTestCase {

	@Test
	public void testGetMessage() throws Exception {
		Assert.assertEquals(
			"Your account has been updated.",
			LanguageUtil.getMessage("account-update-success"));
	}

	@Test
	public void testFormatMessage() throws Exception {
		Assert.assertEquals(
			"5 emails sent today",
			LanguageUtil.formatMessage("x-emails-sent", 5));
	}

}