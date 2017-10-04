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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class ConstantsUtil {

	public static String DEFAULT_PREFERRED_DOMAIN;

	public static String getCurrencySymbol(String currency) {
		return _currencySymbols.get(currency);
	}

	public static Map<String, String> getGlobalIds() {
		return _globalIds;
	}

	public static String getPreferredCurrency(String preferredDomain) {
		return _preferredCurrencies.get(preferredDomain);
	}

	public static Map<String, String> getPreferredDomains() {
		return _preferredDomains;
	}

	public static Map<String, String> getRssGlobalIds() {
		return _rssGlobalIds;
	}

	public static void init() {
		DEFAULT_PREFERRED_DOMAIN =
			"http://rover.ebay.com/rover/1/711-53200-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229466&kwid=902099&mtid=824&kw=lg&icep_item=";

		_currencySymbols.put("USD", "$");
		_currencySymbols.put("CAD", "C $");
		_currencySymbols.put("GBP", "£");
		_currencySymbols.put("EUR", "€");
		_currencySymbols.put("AUD", "AU $");
		_currencySymbols.put("CHF", "CHF ");

		_globalIds.put("EBAY-US", "United States");
		_globalIds.put("EBAY-ENCA", "Canada");
		_globalIds.put("EBAY-GB", "United Kingdom");
		_globalIds.put("EBAY-AT", "Austria");
		_globalIds.put("EBAY-AU", "Australia");
		_globalIds.put("EBAY-FRBE", "Belgium");
		_globalIds.put("EBAY-CH", "Switzerland");
		_globalIds.put("EBAY-DE", "Germany");
		_globalIds.put("EBAY-ES", "Spain");
		_globalIds.put("EBAY-FR", "France");
		_globalIds.put("EBAY-IE", "Ireland");
		_globalIds.put("EBAY-IT", "Italy");
		_globalIds.put("EBAY-NL", "Netherlands");

		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/711-53200-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229466&kwid=902099&mtid=824&kw=lg&icep_item=",
			"USD");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/706-53473-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229529&kwid=902099&mtid=824&kw=lg&icep_item=",
			"CAD");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/710-53481-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229508&kwid=902099&mtid=824&kw=lg&icep_item=",
			"GBP");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/5221-53469-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229473&kwid=902099&mtid=824&kw=lg&icep_item=",
			"EUR");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/705-53470-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229515&kwid=902099&mtid=824&kw=lg&icep_item=",
			"AUD");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/1553-53471-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229522&kwid=902099&mtid=824&kw=lg&icep_item=",
			"EUR");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/5222-53480-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229536&kwid=902099&mtid=824&kw=lg&icep_item=",
			"CHF");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/707-53477-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229487&kwid=902099&mtid=824&kw=lg&icep_item=",
			"EUR");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/1185-53479-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229501&kwid=902099&mtid=824&kw=lg&icep_item=",
			"EUR");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/709-53476-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229480&kwid=902099&mtid=824&kw=lg&icep_item=",
			"EUR");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/5282-53468-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229543&kwid=902099&mtid=824&kw=lg&icep_item=",
			"EUR");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/724-53478-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229494&kwid=902099&mtid=824&kw=lg&icep_item=",
			"EUR");
		_preferredCurrencies.put(
			"http://rover.ebay.com/rover/1/1346-53482-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229557&kwid=902099&mtid=824&kw=lg&icep_item=",
			"EUR");

		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/711-53200-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229466&kwid=902099&mtid=824&kw=lg&icep_item=",
			".com");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/706-53473-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229529&kwid=902099&mtid=824&kw=lg&icep_item=",
			".ca");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/710-53481-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229508&kwid=902099&mtid=824&kw=lg&icep_item=",
			".co.uk");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/5221-53469-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229473&kwid=902099&mtid=824&kw=lg&icep_item=",
			".at");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/705-53470-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229515&kwid=902099&mtid=824&kw=lg&icep_item=",
			".com.au");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/1553-53471-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229522&kwid=902099&mtid=824&kw=lg&icep_item=",
			".be");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/5222-53480-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229536&kwid=902099&mtid=824&kw=lg&icep_item=",
			".ch");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/707-53477-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229487&kwid=902099&mtid=824&kw=lg&icep_item=",
			".de");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/1185-53479-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229501&kwid=902099&mtid=824&kw=lg&icep_item=",
			".es");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/709-53476-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229480&kwid=902099&mtid=824&kw=lg&icep_item=",
			".fr");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/5282-53468-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229543&kwid=902099&mtid=824&kw=lg&icep_item=",
			".ie");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/724-53478-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229494&kwid=902099&mtid=824&kw=lg&icep_item=",
			".it");
		_preferredDomains.put(
			"http://rover.ebay.com/rover/1/1346-53482-19255-0/1?icep_ff3=2&pub=" +
				PropertiesValues.EBAY_PUBLISHER_ID + "&toolid=10001&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID +
				"&customid=&ipn=psmain&icep_vectorid=229557&kwid=902099&mtid=824&kw=lg&icep_item=",
			".nl");

		_rssGlobalIds.put("1", "United States");
		_rssGlobalIds.put("7", "Canada");
		_rssGlobalIds.put("15", "United Kingdom");
		_rssGlobalIds.put("3", "Austria");
		_rssGlobalIds.put("4", "Australia");
		_rssGlobalIds.put("5", "Belgium");
		_rssGlobalIds.put("14", "Switzerland");
		_rssGlobalIds.put("11", "Germany");
		_rssGlobalIds.put("13", "Spain");
		_rssGlobalIds.put("10", "France");
		_rssGlobalIds.put("2", "Ireland");
		_rssGlobalIds.put("12", "Italy");
		_rssGlobalIds.put("16", "Netherlands");
	}

	private static final Map<String, String> _currencySymbols = new HashMap<>();
	private static final Map<String, String> _globalIds = new LinkedHashMap<>();
	private static final Map<String, String> _preferredCurrencies =
		new HashMap<>();
	private static final Map<String, String> _preferredDomains =
		new LinkedHashMap<>();
	private static final Map<String, String> _rssGlobalIds =
		new LinkedHashMap<>();

}