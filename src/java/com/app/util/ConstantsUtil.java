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
		return _CURRENCY_SYMBOLS.get(currency);
	}

	public static Map<String, String> getGlobalIds() {
		return _GLOBAL_IDS;
	}

	public static String getPreferredCurrency(String preferredDomain) {
		return _PREFERRED_CURRENCIES.get(preferredDomain);
	}

	public static Map<String, String> getPreferredDomains() {
		return _PREFERRED_DOMAINS;
	}

	public static Map<String, String> getRssGlobalIds() {
		return _RSS_GLOBAL_IDS;
	}

	private static final Map<String, String> _CURRENCY_SYMBOLS = new HashMap<>();
	private static final Map<String, String> _GLOBAL_IDS = new LinkedHashMap<>();
	private static final Map<String, String> _PREFERRED_CURRENCIES =
		new HashMap<>();
	private static final Map<String, String> _PREFERRED_DOMAINS =
		new LinkedHashMap<>();
	private static final Map<String, String> _RSS_GLOBAL_IDS =
		new LinkedHashMap<>();

	static {
		_CURRENCY_SYMBOLS.put("USD", "$");
		_CURRENCY_SYMBOLS.put("CAD", "C $");
		_CURRENCY_SYMBOLS.put("GBP", "£");
		_CURRENCY_SYMBOLS.put("EUR", "€");
		_CURRENCY_SYMBOLS.put("AUD", "AU $");
		_CURRENCY_SYMBOLS.put("EUR", "€");
		_CURRENCY_SYMBOLS.put("CHF", "CHF ");
		_CURRENCY_SYMBOLS.put("EUR", "€");
		_CURRENCY_SYMBOLS.put("EUR", "€");
		_CURRENCY_SYMBOLS.put("EUR", "€");
		_CURRENCY_SYMBOLS.put("EUR", "€");
		_CURRENCY_SYMBOLS.put("EUR", "€");
		_CURRENCY_SYMBOLS.put("EUR", "€");

		_GLOBAL_IDS.put("EBAY-US", "United States");
		_GLOBAL_IDS.put("EBAY-ENCA", "Canada");
		_GLOBAL_IDS.put("EBAY-GB", "United Kingdom");
		_GLOBAL_IDS.put("EBAY-AT", "Austria");
		_GLOBAL_IDS.put("EBAY-AU", "Australia");
		_GLOBAL_IDS.put("EBAY-FRBE", "Belgium");
		_GLOBAL_IDS.put("EBAY-CH", "Switzerland");
		_GLOBAL_IDS.put("EBAY-DE", "Germany");
		_GLOBAL_IDS.put("EBAY-ES", "Spain");
		_GLOBAL_IDS.put("EBAY-FR", "France");
		_GLOBAL_IDS.put("EBAY-IE", "Ireland");
		_GLOBAL_IDS.put("EBAY-IT", "Italy");
		_GLOBAL_IDS.put("EBAY-NL", "Netherlands");

		_PREFERRED_CURRENCIES.put("http://www.ebay.com/itm/", "USD");
		_PREFERRED_CURRENCIES.put("http://www.ebay.ca/itm/", "CAD");
		_PREFERRED_CURRENCIES.put("http://www.ebay.co.uk/itm/", "GBP");
		_PREFERRED_CURRENCIES.put("http://www.ebay.at/itm/", "EUR");
		_PREFERRED_CURRENCIES.put("http://www.ebay.com.au/itm/", "AUD");
		_PREFERRED_CURRENCIES.put("http://www.befr.ebay.be/itm/", "EUR");
		_PREFERRED_CURRENCIES.put("http://www.ebay.ch/itm/", "CHF");
		_PREFERRED_CURRENCIES.put("http://www.ebay.de/itm/", "EUR");
		_PREFERRED_CURRENCIES.put("http://www.ebay.es/itm/", "EUR");
		_PREFERRED_CURRENCIES.put("http://www.ebay.fr/itm/", "EUR");
		_PREFERRED_CURRENCIES.put("http://www.ebay.ie/itm/", "EUR");
		_PREFERRED_CURRENCIES.put("http://www.ebay.it/itm/", "EUR");
		_PREFERRED_CURRENCIES.put("http://www.ebay.nl/itm/", "EUR");

		_PREFERRED_DOMAINS.put("http://www.ebay.com/itm/", ".com");
		_PREFERRED_DOMAINS.put("http://www.ebay.ca/itm/", ".ca");
		_PREFERRED_DOMAINS.put("http://www.ebay.co.uk/itm/", ".co.uk");
		_PREFERRED_DOMAINS.put("http://www.ebay.at/itm/", ".at");
		_PREFERRED_DOMAINS.put("http://www.ebay.com.au/itm/", ".com.au");
		_PREFERRED_DOMAINS.put("http://www.befr.ebay.be/itm/", ".be");
		_PREFERRED_DOMAINS.put("http://www.ebay.ch/itm/", ".ch");
		_PREFERRED_DOMAINS.put("http://www.ebay.de/itm/", ".de");
		_PREFERRED_DOMAINS.put("http://www.ebay.es/itm/", ".es");
		_PREFERRED_DOMAINS.put("http://www.ebay.fr/itm/", ".fr");
		_PREFERRED_DOMAINS.put("http://www.ebay.ie/itm/", ".ie");
		_PREFERRED_DOMAINS.put("http://www.ebay.it/itm/", ".it");
		_PREFERRED_DOMAINS.put("http://www.ebay.nl/itm/", ".nl");

		_RSS_GLOBAL_IDS.put("1", "United States");
		_RSS_GLOBAL_IDS.put("7", "Canada");
		_RSS_GLOBAL_IDS.put("15", "United Kingdom");
		_RSS_GLOBAL_IDS.put("3", "Austria");
		_RSS_GLOBAL_IDS.put("4", "Australia");
		_RSS_GLOBAL_IDS.put("5", "Belgium");
		_RSS_GLOBAL_IDS.put("14", "Switzerland");
		_RSS_GLOBAL_IDS.put("11", "Germany");
		_RSS_GLOBAL_IDS.put("13", "Spain");
		_RSS_GLOBAL_IDS.put("10", "France");
		_RSS_GLOBAL_IDS.put("2", "Ireland");
		_RSS_GLOBAL_IDS.put("12", "Italy");
		_RSS_GLOBAL_IDS.put("16", "Netherlands");
	}

}