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

import com.app.util.ConstantsUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * @author Jonathan McCann
 */
public class ConstantsUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpProperties();

		ConstantsUtil.init();
	}

	@Test
	public void testGetCurrencySymbol() {
		Assert.assertEquals("$", ConstantsUtil.getCurrencySymbol("USD"));
		Assert.assertEquals("C $", ConstantsUtil.getCurrencySymbol("CAD"));
		Assert.assertEquals("£", ConstantsUtil.getCurrencySymbol("GBP"));
		Assert.assertEquals("€", ConstantsUtil.getCurrencySymbol("EUR"));
		Assert.assertEquals("AU $", ConstantsUtil.getCurrencySymbol("AUD"));
		Assert.assertEquals("CHF ", ConstantsUtil.getCurrencySymbol("CHF"));
	}

	@Test
	public void testGetPreferredCurrency() {
		Assert.assertEquals(
			"USD", ConstantsUtil.getPreferredCurrency("EBAY_US"));
		Assert.assertEquals(
			"CAD", ConstantsUtil.getPreferredCurrency("EBAY_CA"));
		Assert.assertEquals(
			"GBP", ConstantsUtil.getPreferredCurrency("EBAY_GB"));
		Assert.assertEquals(
			"EUR", ConstantsUtil.getPreferredCurrency("EBAY_AT"));
		Assert.assertEquals(
			"AUD", ConstantsUtil.getPreferredCurrency("EBAY_AU"));
		Assert.assertEquals(
			"EUR", ConstantsUtil.getPreferredCurrency("EBAY_BE"));
		Assert.assertEquals(
			"CHF", ConstantsUtil.getPreferredCurrency("EBAY_CH"));
		Assert.assertEquals(
			"EUR", ConstantsUtil.getPreferredCurrency("EBAY_DE"));
		Assert.assertEquals(
			"EUR", ConstantsUtil.getPreferredCurrency("EBAY_ES"));
		Assert.assertEquals(
			"EUR", ConstantsUtil.getPreferredCurrency("EBAY_FR"));
		Assert.assertEquals(
			"EUR", ConstantsUtil.getPreferredCurrency("EBAY_IE"));
		Assert.assertEquals(
			"EUR", ConstantsUtil.getPreferredCurrency("EBAY_IT"));
		Assert.assertEquals(
			"EUR", ConstantsUtil.getPreferredCurrency("EBAY_NL"));
	}

	@Test
	public void testGetMarketplaceIds() {
		Map<String, String> marketplaceIds =
			ConstantsUtil.getMarketplaceIds();

		Assert.assertEquals(
			"United States (.com)", marketplaceIds.get("EBAY_US"));

		Assert.assertEquals(
			"Canada (.ca)", marketplaceIds.get("EBAY_CA"));

		Assert.assertEquals(
			"United Kingdom (.co.uk)", marketplaceIds.get("EBAY_GB"));

		Assert.assertEquals(
			"Austria (.at)", marketplaceIds.get("EBAY_AT"));

		Assert.assertEquals(
			"Australia (.com.au)", marketplaceIds.get("EBAY_AU"));

		Assert.assertEquals(
			"Belgium (.be)", marketplaceIds.get("EBAY_BE"));

		Assert.assertEquals(
			"Switzerland (.ch)", marketplaceIds.get("EBAY_CH"));

		Assert.assertEquals(
			"Germany (.de)", marketplaceIds.get("EBAY_DE"));

		Assert.assertEquals(
			"Spain (.es)", marketplaceIds.get("EBAY_ES"));

		Assert.assertEquals(
			"France (.fr)", marketplaceIds.get("EBAY_FR"));

		Assert.assertEquals(
			"Ireland (.ie)", marketplaceIds.get("EBAY_IE"));

		Assert.assertEquals(
			"Italy (.it)", marketplaceIds.get("EBAY_IT"));

		Assert.assertEquals(
			"Netherlands (.nl)", marketplaceIds.get("EBAY_NL"));
	}

}