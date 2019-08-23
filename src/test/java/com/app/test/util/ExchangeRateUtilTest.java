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
import com.app.util.ExchangeRateUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Jonathan McCann
 */
public class ExchangeRateUtilTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpProperties();
	}

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

	private static final double _PRICE = 10.0;

}