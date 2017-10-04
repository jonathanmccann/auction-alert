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
	public void testGetGlobalIds() {
		Map<String, String> globalIds = ConstantsUtil.getGlobalIds();

		Assert.assertEquals("United States", globalIds.get("EBAY-US"));
		Assert.assertEquals("Canada", globalIds.get("EBAY-ENCA"));
		Assert.assertEquals("United Kingdom", globalIds.get("EBAY-GB"));
		Assert.assertEquals("Austria", globalIds.get("EBAY-AT"));
		Assert.assertEquals("Australia", globalIds.get("EBAY-AU"));
		Assert.assertEquals("Belgium", globalIds.get("EBAY-FRBE"));
		Assert.assertEquals("Switzerland", globalIds.get("EBAY-CH"));
		Assert.assertEquals("Germany", globalIds.get("EBAY-DE"));
		Assert.assertEquals("Spain", globalIds.get("EBAY-ES"));
		Assert.assertEquals("France", globalIds.get("EBAY-FR"));
		Assert.assertEquals("Ireland", globalIds.get("EBAY-IE"));
		Assert.assertEquals("Italy", globalIds.get("EBAY-IT"));
		Assert.assertEquals("Netherlands", globalIds.get("EBAY-NL"));
	}

	@Test
	public void testGetPreferredCurrency() {
		Assert.assertEquals(
			"USD",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/711-53200-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229466&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"CAD",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/706-53473-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229529&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"GBP",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/710-53481-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229508&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/5221-53469-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229473&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"AUD",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/705-53470-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229515&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/1553-53471-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229522&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"CHF",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/5222-53480-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229536&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/707-53477-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229487&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/1185-53479-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229501&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/709-53476-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229480&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/5282-53468-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229543&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/724-53478-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229494&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			"EUR",
			ConstantsUtil.getPreferredCurrency("http://rover.ebay.com/rover/1/1346-53482-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229557&kwid=902099&mtid=824&kw=lg&icep_item="));
	}

	@Test
	public void testGetPreferredDomains() {
		Map<String, String> preferredDomains =
			ConstantsUtil.getPreferredDomains();

		Assert.assertEquals(
			".com",
			preferredDomains.get("http://rover.ebay.com/rover/1/711-53200-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229466&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".ca",
			preferredDomains.get("http://rover.ebay.com/rover/1/706-53473-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229529&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".co.uk",
			preferredDomains.get("http://rover.ebay.com/rover/1/710-53481-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229508&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".at",
			preferredDomains.get("http://rover.ebay.com/rover/1/5221-53469-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229473&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".com.au",
			preferredDomains.get("http://rover.ebay.com/rover/1/705-53470-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229515&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".be",
			preferredDomains.get("http://rover.ebay.com/rover/1/1553-53471-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229522&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".ch",
			preferredDomains.get("http://rover.ebay.com/rover/1/5222-53480-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229536&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".de",
			preferredDomains.get("http://rover.ebay.com/rover/1/707-53477-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229487&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".es",
			preferredDomains.get("http://rover.ebay.com/rover/1/1185-53479-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229501&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".fr",
			preferredDomains.get("http://rover.ebay.com/rover/1/709-53476-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229480&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".ie",
			preferredDomains.get("http://rover.ebay.com/rover/1/5282-53468-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229543&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".it",
			preferredDomains.get("http://rover.ebay.com/rover/1/724-53478-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229494&kwid=902099&mtid=824&kw=lg&icep_item="));
		Assert.assertEquals(
			".nl",
			preferredDomains.get("http://rover.ebay.com/rover/1/1346-53482-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229557&kwid=902099&mtid=824&kw=lg&icep_item="));
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