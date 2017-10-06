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

import com.app.util.ConstantsUtil;
import com.app.util.EbayAPIUtil;

import com.app.test.BaseTestCase;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.services.client.FindingServiceClientFactory;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jonathan McCann
 */
public class EbayAPIUtilTest extends BaseTestCase {

	@Before
	public void setUp() throws Exception {
		_clazz = Class.forName(EbayAPIUtil.class.getName());
	}

	@Test
	public void testGetServiceClient() throws Exception {
		setUpEbayAPIUtil();
		setUpProperties();

		Field serviceClientsField = _clazz.getDeclaredField("_serviceClients");

		serviceClientsField.setAccessible(true);

		Field modifiersField = Field.class.getDeclaredField("modifiers");

		modifiersField.setAccessible(true);
		modifiersField.setInt(
			serviceClientsField,
			serviceClientsField.getModifiers() & ~Modifier.FINAL);

		Map<String, FindingServicePortType> serviceClients =
			(Map<String, FindingServicePortType>)serviceClientsField.get(
				EbayAPIUtil.class);

		if (serviceClients.isEmpty()) {
			serviceClientsField.set(_clazz, new HashMap<>());
		}
		else {
			serviceClients.clear();
		}

		EbayAPIUtil.getServiceClient("EBAY-US");
		EbayAPIUtil.getServiceClient("EBAY-US");

		PowerMockito.verifyPrivate(
			FindingServiceClientFactory.class, Mockito.times(1)).invoke(
				"getServiceClient", Mockito.anyObject());

		serviceClients =
			(Map<String, FindingServicePortType>)serviceClientsField.get(
				EbayAPIUtil.class);

		Assert.assertEquals(1, serviceClients.size());
		Assert.assertTrue(serviceClients.containsKey("EBAY-US"));
	}

	@Test
	public void testLoadApiContext() throws Exception {
		setUpProperties();

		EbayAPIUtil.loadApiContext();

		ApiContext apiContext = EbayAPIUtil.getApiContext();

		ApiCredential apiCredential = apiContext.getApiCredential();

		Assert.assertEquals("eBay Token", apiCredential.geteBayToken());
		Assert.assertEquals(_API_SERVER_URL, apiContext.getApiServerUrl());
		Assert.assertEquals(SiteCodeType.US, apiContext.getSite());
	}

	@Test
	public void testLoadEbayServiceClients() throws Exception {
		setUpEbayAPIUtil();
		setUpProperties();

		ConstantsUtil.init();

		EbayAPIUtil.loadEbayServiceClients();

		PowerMockito.verifyPrivate(
			FindingServiceClientFactory.class, Mockito.times(13)).invoke(
				"getServiceClient", Mockito.anyObject());

		Field serviceClients = _clazz.getDeclaredField("_serviceClients");

		serviceClients.setAccessible(true);

		Map<String, FindingServicePortType> clients =
			(Map<String, FindingServicePortType>)serviceClients.get(
				EbayAPIUtil.class);

		Assert.assertEquals(13, clients.size());
		Assert.assertTrue(clients.containsKey("EBAY-US"));
		Assert.assertTrue(clients.containsKey("EBAY-ENCA"));
		Assert.assertTrue(clients.containsKey("EBAY-GB"));
		Assert.assertTrue(clients.containsKey("EBAY-AT"));
		Assert.assertTrue(clients.containsKey("EBAY-AU"));
		Assert.assertTrue(clients.containsKey("EBAY-FRBE"));
		Assert.assertTrue(clients.containsKey("EBAY-CH"));
		Assert.assertTrue(clients.containsKey("EBAY-DE"));
		Assert.assertTrue(clients.containsKey("EBAY-ES"));
		Assert.assertTrue(clients.containsKey("EBAY-FR"));
		Assert.assertTrue(clients.containsKey("EBAY-IE"));
		Assert.assertTrue(clients.containsKey("EBAY-IT"));
		Assert.assertTrue(clients.containsKey("EBAY-NL"));
	}

	private static Class _clazz;

	private static final String _API_SERVER_URL = "https://api.ebay.com/wsapi";

}