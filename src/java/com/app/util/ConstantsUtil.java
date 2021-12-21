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

	public static final String DEFAULT_MARKETPLACE_ID = "EBAY_US";

	public static String getCurrencySymbol(String currency) {
		return _currencySymbols.get(currency);
	}

	public static Map<String, String> getMarketplaceIds() {
		return _marketplaceIds;
	}

	public static String getPreferredCurrency(String marketplaceId) {
		return _preferredCurrencies.get(marketplaceId);
	}

	public static void init() {
		_currencySymbols.put("USD", "$");
		_currencySymbols.put("CAD", "C $");
		_currencySymbols.put("GBP", "£");
		_currencySymbols.put("EUR", "€");
		_currencySymbols.put("AUD", "AU $");
		_currencySymbols.put("CHF", "CHF ");

		_marketplaceIds.put("EBAY_US", "United States (.com)");
		_marketplaceIds.put("EBAY_CA", "Canada (.ca)");
		_marketplaceIds.put("EBAY_GB", "United Kingdom (.co.uk)");
		_marketplaceIds.put("EBAY_AT", "Austria (.at)");
		_marketplaceIds.put("EBAY_AU", "Australia (.com.au)");
		_marketplaceIds.put("EBAY_BE", "Belgium (.be)");
		_marketplaceIds.put("EBAY_CH", "Switzerland (.ch)");
		_marketplaceIds.put("EBAY_DE", "Germany (.de)");
		_marketplaceIds.put("EBAY_ES", "Spain (.es)");
		_marketplaceIds.put("EBAY_FR", "France (.fr)");
		_marketplaceIds.put("EBAY_IE", "Ireland (.ie)");
		_marketplaceIds.put("EBAY_IT", "Italy (.it)");
		_marketplaceIds.put("EBAY_NL", "Netherlands (.nl)");

		_preferredCurrencies.put("EBAY_US", "USD");
		_preferredCurrencies.put("EBAY_CA", "CAD");
		_preferredCurrencies.put("EBAY_GB", "GBP");
		_preferredCurrencies.put("EBAY_AT", "EUR");
		_preferredCurrencies.put("EBAY_AU", "AUD");
		_preferredCurrencies.put("EBAY_BE", "EUR");
		_preferredCurrencies.put("EBAY_CH", "CHF");
		_preferredCurrencies.put("EBAY_DE", "EUR");
		_preferredCurrencies.put("EBAY_ES", "EUR");
		_preferredCurrencies.put("EBAY_FR", "EUR");
		_preferredCurrencies.put("EBAY_IE", "EUR");
		_preferredCurrencies.put("EBAY_IT", "EUR");
		_preferredCurrencies.put("EBAY_NL", "EUR");
	}

	private static final Map<String, String> _currencySymbols = new HashMap<>();
	private static final Map<String, String> _preferredCurrencies =
		new HashMap<>();
	private static final Map<String, String> _marketplaceIds =
		new LinkedHashMap<>();

}