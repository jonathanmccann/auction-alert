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

	private static final Map<String, String> _currencySymbols = new HashMap<>();
	private static final Map<String, String> _globalIds = new LinkedHashMap<>();
	private static final Map<String, String> _preferredCurrencies =
		new HashMap<>();
	private static final Map<String, String> _preferredDomains =
		new LinkedHashMap<>();
	private static final Map<String, String> _rssGlobalIds =
		new LinkedHashMap<>();

	static {
		_currencySymbols.put("USD", "$");
		_currencySymbols.put("CAD", "C $");
		_currencySymbols.put("GBP", "£");
		_currencySymbols.put("EUR", "€");
		_currencySymbols.put("AUD", "AU $");
		_currencySymbols.put("EUR", "€");
		_currencySymbols.put("CHF", "CHF ");
		_currencySymbols.put("EUR", "€");
		_currencySymbols.put("EUR", "€");
		_currencySymbols.put("EUR", "€");
		_currencySymbols.put("EUR", "€");
		_currencySymbols.put("EUR", "€");
		_currencySymbols.put("EUR", "€");

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

		_preferredCurrencies.put("http://www.ebay.com/itm/", "USD");
		_preferredCurrencies.put("http://www.ebay.ca/itm/", "CAD");
		_preferredCurrencies.put("http://www.ebay.co.uk/itm/", "GBP");
		_preferredCurrencies.put("http://www.ebay.at/itm/", "EUR");
		_preferredCurrencies.put("http://www.ebay.com.au/itm/", "AUD");
		_preferredCurrencies.put("http://www.befr.ebay.be/itm/", "EUR");
		_preferredCurrencies.put("http://www.ebay.ch/itm/", "CHF");
		_preferredCurrencies.put("http://www.ebay.de/itm/", "EUR");
		_preferredCurrencies.put("http://www.ebay.es/itm/", "EUR");
		_preferredCurrencies.put("http://www.ebay.fr/itm/", "EUR");
		_preferredCurrencies.put("http://www.ebay.ie/itm/", "EUR");
		_preferredCurrencies.put("http://www.ebay.it/itm/", "EUR");
		_preferredCurrencies.put("http://www.ebay.nl/itm/", "EUR");

		_preferredDomains.put("http://www.ebay.com/itm/", ".com");
		_preferredDomains.put("http://www.ebay.ca/itm/", ".ca");
		_preferredDomains.put("http://www.ebay.co.uk/itm/", ".co.uk");
		_preferredDomains.put("http://www.ebay.at/itm/", ".at");
		_preferredDomains.put("http://www.ebay.com.au/itm/", ".com.au");
		_preferredDomains.put("http://www.befr.ebay.be/itm/", ".be");
		_preferredDomains.put("http://www.ebay.ch/itm/", ".ch");
		_preferredDomains.put("http://www.ebay.de/itm/", ".de");
		_preferredDomains.put("http://www.ebay.es/itm/", ".es");
		_preferredDomains.put("http://www.ebay.fr/itm/", ".fr");
		_preferredDomains.put("http://www.ebay.ie/itm/", ".ie");
		_preferredDomains.put("http://www.ebay.it/itm/", ".it");
		_preferredDomains.put("http://www.ebay.nl/itm/", ".nl");

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

}