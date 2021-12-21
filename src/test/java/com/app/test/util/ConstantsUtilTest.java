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
import com.app.util.PropertiesValues;
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
			"USD",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=711-53200-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"CAD",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=706-53473-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"GBP",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=710-53481-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=5221-53469-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"AUD",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=705-53470-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=1553-53471-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"CHF",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=5222-53480-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=707-53477-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=1185-53479-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=709-53476-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=5282-53468-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=724-53478-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency(
				"?mkrid=1346-53482-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
					PropertiesValues.EBAY_CAMPAIGN_ID));
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

	@Test
	public void testGetRssGlobalIds()  {
		Map<String, String> rssGlobalIds = ConstantsUtil.getRssGlobalIds();

		Assert.assertEquals("United States", rssGlobalIds.get("1"));
		Assert.assertEquals("Canada", rssGlobalIds.get("7"));
		Assert.assertEquals("United Kingdom", rssGlobalIds.get("15"));
		Assert.assertEquals("Austria", rssGlobalIds.get("3"));
		Assert.assertEquals("Australia", rssGlobalIds.get("4"));
		Assert.assertEquals("Belgium", rssGlobalIds.get("5"));
		Assert.assertEquals("Switzerland", rssGlobalIds.get("14"));
		Assert.assertEquals("Germany", rssGlobalIds.get("11"));
		Assert.assertEquals("Spain", rssGlobalIds.get("13"));
		Assert.assertEquals("France", rssGlobalIds.get("10"));
		Assert.assertEquals("Ireland", rssGlobalIds.get("2"));
		Assert.assertEquals("Italy", rssGlobalIds.get("12"));
		Assert.assertEquals("Netherlands", rssGlobalIds.get("16"));
	}

}