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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		/*try {
			Gson gson = new Gson();

			for (String fromCurrencyId : _CURRENCY_IDS) {
				for (String toCurrencyId : _CURRENCY_IDS) {
					if (fromCurrencyId.equals(toCurrencyId)) {
						continue;
					}

					HttpClient httpClient = HttpClients.createDefault();

					HttpGet httpGet = new HttpGet(
						_EXCHANGE_RATE_URL + fromCurrencyId + "_" + toCurrencyId);

					HttpResponse response = httpClient.execute(httpGet);

					_exchangeRates.putAll(
						gson.fromJson(
							EntityUtils.toString(response.getEntity()),
							Map.class));
				}
			}
		}
		catch (Exception e) {
			_log.error(
				"Unable to fetch exchange rates from API", e.getMessage(), e);*/

			_exchangeRates.put("AUD_CAD", 0.95);
			_exchangeRates.put("AUD_CHF", 0.71);
			_exchangeRates.put("AUD_EUR", 0.63);
			_exchangeRates.put("AUD_GBP", 0.55);
			_exchangeRates.put("AUD_USD", 0.71);

			_exchangeRates.put("CAD_AUD", 1.05);
			_exchangeRates.put("CAD_CHF", 0.75);
			_exchangeRates.put("CAD_EUR", 0.67);
			_exchangeRates.put("CAD_GBP", 0.58);
			_exchangeRates.put("CAD_USD", 0.75);

			_exchangeRates.put("CHF_AUD", 1.40);
			_exchangeRates.put("CHF_CAD", 1.33);
			_exchangeRates.put("CHF_EUR", 0.89);
			_exchangeRates.put("CHF_GBP", 0.77);
			_exchangeRates.put("CHF_USD", 1.00);

			_exchangeRates.put("EUR_AUD", 1.58);
			_exchangeRates.put("EUR_CAD", 1.50);
			_exchangeRates.put("EUR_CHF", 1.13);
			_exchangeRates.put("EUR_GBP", 0.86);
			_exchangeRates.put("EUR_USD", 1.13);

			_exchangeRates.put("GBP_AUD", 1.83);
			_exchangeRates.put("GBP_CAD", 1.74);
			_exchangeRates.put("GBP_CHF", 1.30);
			_exchangeRates.put("GBP_EUR", 1.16);
			_exchangeRates.put("GBP_USD", 1.31);

			_exchangeRates.put("USD_AUD", 1.40);
			_exchangeRates.put("USD_CAD", 1.33);
			_exchangeRates.put("USD_CHF", 1.00);
			_exchangeRates.put("USD_EUR", 0.89);
			_exchangeRates.put("USD_GBP", 0.77);
		//}
	}

	private static final String[] _CURRENCY_IDS = {
		"AUD", "CAD", "CHF", "EUR", "GBP", "USD"
	};

	private static final String _EXCHANGE_RATE_URL =
		"https://free.currencyconverterapi.com/api/v6/convert?compact=ultra&apiKey=" +
			PropertiesValues.CURRENCY_CONVERTER_API_KEY + "&q=";

	private static final Logger _log = LoggerFactory.getLogger(
		ExchangeRateUtil.class);

	private static final Map<String, Double> _exchangeRates =
		new HashMap<>();

}