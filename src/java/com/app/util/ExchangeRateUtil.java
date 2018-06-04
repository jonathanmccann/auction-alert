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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jonathan McCann
 */
public class ExchangeRateUtil {

	public static double convertCurrency(
		String fromCurrencyId, String toCurrencyId, double price) {

		if (fromCurrencyId.equals(toCurrencyId)) {
			return price;
		}

		String currencyKey = fromCurrencyId + "_" + toCurrencyId;

		return price * _exchangeRates.get(currencyKey);
	}

	public static void updateExchangeRates() throws Exception {
		Gson gson = new Gson();

		for (String fromCurrencyId : _CURRENCY_IDS) {
			for (String toCurrencyId : _CURRENCY_IDS) {
				if (fromCurrencyId.equals(toCurrencyId)) {
					continue;
				}

				URL exchangeRateUrl = new URL(
					_EXCHANGE_RATE_URL + fromCurrencyId + "_" + toCurrencyId);

				URLConnection urlConnection = exchangeRateUrl.openConnection();

				try (InputStreamReader inputStreamReader = new InputStreamReader(
						urlConnection.getInputStream());
					BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader)) {

					_exchangeRates.putAll(
						gson.fromJson(bufferedReader, Map.class));
				}
			}
		}
	}

	private static final String[] _CURRENCY_IDS = {
		"AUD", "CAD", "CHF", "EUR", "GBP", "USD"
	};

	private static final String _EXCHANGE_RATE_URL =
		"https://free.currencyconverterapi.com/api/v5/convert?compact=ultra&q=";

	private static final Map<String, Double> _exchangeRates =
		new HashMap<>();

}