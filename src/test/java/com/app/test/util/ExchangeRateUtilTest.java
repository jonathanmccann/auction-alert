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

import com.app.util.ExchangeRateUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class ExchangeRateUtilTest {

	@Test
	public void testConvertCurrency() throws Exception {
		setUpExchangeRateUtil();

		double price = ExchangeRateUtil.convertCurrency("USD", "CAD", _PRICE);

		Assert.assertEquals(_PRICE * _USD_TO_CAD, price, 0);

		price = ExchangeRateUtil.convertCurrency("USD", "GBP", _PRICE);

		Assert.assertEquals(_PRICE * _USD_TO_GBP, price, 0);
	}

	@Test
	public void testConvertCurrencyWithSameIds() throws Exception {
		double price = ExchangeRateUtil.convertCurrency("USD", "USD", _PRICE);

		Assert.assertEquals(_PRICE, price, 0);
	}

	@Test
	public void testParseExchangeRates() throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
			_EXCHANGE_RATES.getBytes(StandardCharsets.UTF_8.name()));

		InputStreamReader inputStreamReader = new InputStreamReader(
			inputStream);

		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		Class clazz = Class.forName(ExchangeRateUtil.class.getName());

		Object classInstance = clazz.newInstance();

		Method parseExchangeRates = clazz.getDeclaredMethod(
			"_parseExchangeRates", String.class, BufferedReader.class);

		parseExchangeRates.setAccessible(true);

		parseExchangeRates.invoke(classInstance, "USD", bufferedReader);

		Field exchangeRates = clazz.getDeclaredField("_exchangeRates");

		exchangeRates.setAccessible(true);

		Map<String, Map<String, Double>> exchangeRatesMap =
			(Map<String, Map<String, Double>>)exchangeRates.get(
				ExchangeRateUtil.class);

		Map<String, Double> exchangeRate = exchangeRatesMap.get("USD");

		Assert.assertEquals((Double)1.0, exchangeRate.get("AUD"));
		Assert.assertEquals((Double)2.0, exchangeRate.get("CAD"));
		Assert.assertEquals((Double)3.0, exchangeRate.get("CHF"));
		Assert.assertEquals((Double)4.0, exchangeRate.get("GBP"));
		Assert.assertEquals((Double)5.0, exchangeRate.get("EUR"));
		Assert.assertFalse(exchangeRate.containsKey("USD"));
	}

	private static void setUpExchangeRateUtil() throws Exception {
		Class clazz = Class.forName(ExchangeRateUtil.class.getName());

		Field exchangeRates = clazz.getDeclaredField("_exchangeRates");

		exchangeRates.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");

		modifiersField.setAccessible(true);
		modifiersField.setInt(
			exchangeRates, exchangeRates.getModifiers() & ~Modifier.FINAL);

		Map<String, Double> usdRates = new HashMap<>();

		usdRates.put("CAD", _USD_TO_CAD);
		usdRates.put("GBP", _USD_TO_GBP);

		Map<String, Map<String, Double>> rates = new HashMap<>();

		rates.put("USD", usdRates);

		exchangeRates.set("USD", rates);
	}

	private static final double _PRICE = 10.0;
	private static final double _USD_TO_CAD = 2.0;
	private static final double _USD_TO_GBP = 5.0;

	private static final String _EXCHANGE_RATES =
		"{\"base\":\"USD\",\"date\":\"2017-10-05\",\"rates\":{\"AUD\":1.0,\"CAD\":2.0,\"CHF\":3.0,\"GBP\":4.0,\"EUR\":5.0}}";

}