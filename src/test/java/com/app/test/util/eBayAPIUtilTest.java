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

import com.app.test.BaseTestCase;
import com.app.util.PropertiesKeys;
import com.app.util.eBayAPIUtil;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class eBayAPIUtilTest extends BaseTestCase {

	@Test
	public void testGetApiContext() {
		ApiContext apiContext = eBayAPIUtil.getApiContext();

		ApiCredential apiCredential = apiContext.getApiCredential();

		Assert.assertEquals(
			System.getProperty(PropertiesKeys.EBAY_TOKEN),
			apiCredential.geteBayToken());
		Assert.assertEquals(_API_SERVER_URL, apiContext.getApiServerUrl());
		Assert.assertEquals(SiteCodeType.US, apiContext.getSite());
	}

	private static final String _API_SERVER_URL = "https://api.ebay.com/wsapi";

}