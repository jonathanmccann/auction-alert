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
			"?mkrid=711-53200-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID;

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
			"?mkrid=711-53200-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"USD");
		_preferredCurrencies.put(
			"?mkrid=706-53473-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"CAD");
		_preferredCurrencies.put(
			"?mkrid=710-53481-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"GBP");
		_preferredCurrencies.put(
			"?mkrid=5221-53469-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"EUR");
		_preferredCurrencies.put(
			"?mkrid=705-53470-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"AUD");
		_preferredCurrencies.put(
			"?mkrid=1553-53471-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"EUR");
		_preferredCurrencies.put(
			"?mkrid=5222-53480-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"CHF");
		_preferredCurrencies.put(
			"?mkrid=707-53477-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"EUR");
		_preferredCurrencies.put(
			"?mkrid=1185-53479-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"EUR");
		_preferredCurrencies.put(
			"?mkrid=709-53476-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"EUR");
		_preferredCurrencies.put(
			"?mkrid=5282-53468-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"EUR");
		_preferredCurrencies.put(
			"?mkrid=724-53478-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"EUR");
		_preferredCurrencies.put(
			"?mkrid=1346-53482-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			"EUR");

		_preferredDomains.put(
			"?mkrid=711-53200-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".com");
		_preferredDomains.put(
			"?mkrid=706-53473-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".ca");
		_preferredDomains.put(
			"?mkrid=710-53481-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".co.uk");
		_preferredDomains.put(
			"?mkrid=5221-53469-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".at");
		_preferredDomains.put(
			"?mkrid=705-53470-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".com.au");
		_preferredDomains.put(
			"?mkrid=1553-53471-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".be");
		_preferredDomains.put(
			"?mkrid=5222-53480-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".ch");
		_preferredDomains.put(
			"?mkrid=707-53477-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".de");
		_preferredDomains.put(
			"?mkrid=1185-53479-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".es");
		_preferredDomains.put(
			"?mkrid=709-53476-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".fr");
		_preferredDomains.put(
			"?mkrid=5282-53468-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".ie");
		_preferredDomains.put(
			"?mkrid=724-53478-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
			".it");
		_preferredDomains.put(
			"?mkrid=1346-53482-19255-0&mkcid=1&toolid=10001&mkevt=1&campid=" +
				PropertiesValues.EBAY_CAMPAIGN_ID,
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