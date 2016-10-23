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

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.services.client.ClientConfig;
import com.ebay.services.client.FindingServiceClientFactory;
import com.ebay.services.finding.FindingServicePortType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Jonathan McCann
 */
public class EbayAPIUtil {

	public static ApiContext getApiContext() {
		return _apiContext;
	}

	public static FindingServicePortType getServiceClient(String globalId) {
		FindingServicePortType serviceClient = _serviceClients.get(globalId);

		if (null != serviceClient) {
			return serviceClient;
		}

		ClientConfig config = new ClientConfig();

		config.setApplicationId(PropertiesValues.APPLICATION_ID);
		config.setGlobalId(globalId);

		serviceClient = FindingServiceClientFactory.getServiceClient(config);

		_serviceClients.put(globalId, serviceClient);

		return serviceClient;
	}

	public static void loadApiContext() {
		loadApiContext(PropertiesValues.EBAY_TOKEN);
	}

	public static void loadApiContext(String ebayToken) {
		ApiCredential apiCredential = new ApiCredential();

		apiCredential.seteBayToken(ebayToken);

		_apiContext = new ApiContext();
		_apiContext.setApiCredential(apiCredential);
		_apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
		_apiContext.setSite(SiteCodeType.US);
	}

	public static void loadEbayServiceClients() {
		loadEbayServiceClients(PropertiesValues.APPLICATION_ID);
	}

	public static void loadEbayServiceClients(String applicationId) {
		ClientConfig config = new ClientConfig();

		config.setApplicationId(applicationId);

		Map<String, String> globalIds = SearchQueryUtil.getGlobalIds();

		Set<String> keys = globalIds.keySet();

		for (String key : keys) {
			config.setGlobalId(key);

			_serviceClients.put(
				key, FindingServiceClientFactory.getServiceClient(config));
		}
	}

	private static Map<String, FindingServicePortType> _serviceClients =
		new HashMap<>();

	private static ApiContext _apiContext;

}