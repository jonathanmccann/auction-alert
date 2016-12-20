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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jonathan McCann
 */
public class ExchangeRateUtil {

	public static double convertCurrency(
		String fromCurrency, String toCurrency, double price) {

		if (fromCurrency.equals(toCurrency)) {
			return price;
		}

		double exchangeRate = _exchangeRates.get(fromCurrency).get(toCurrency);

		return price * exchangeRate;
	}

	public static void updateExchangeRates() throws Exception {
		for (String currencyId : _CURRENCY_IDS) {
			URL exchangeRateUrl = new URL(
				MessageFormat.format(_EXCHANGE_RATE_URL, currencyId));

			URLConnection urlConnection = exchangeRateUrl.openConnection();

			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(urlConnection.getInputStream()));

			Gson gson = new Gson();

			Map<Object, Object> response = gson.fromJson(
				bufferedReader, Map.class);

			_exchangeRates.put(
				currencyId, (Map<String, Double>)response.get("rates"));
		}
	}

	private static final String[] _CURRENCY_IDS = {
		"AUD", "CAD", "CHF", "EUR", "GBP", "USD"
	};

	private static final String _EXCHANGE_RATE_URL =
		"http://api.fixer.io/latest?base={0}&symbols=AUD,CAD,CHF,EUR,GBP,USD";

	private static final Map<String, Map<String, Double>> _exchangeRates =
		new HashMap<>();

}