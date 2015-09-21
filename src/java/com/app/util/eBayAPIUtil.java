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

/**
 * @author Jonathan McCann
 */
public class eBayAPIUtil {

	public static ApiContext getApiContext() {
		return _apiContext;
	}

	public static FindingServicePortType getServiceClient() {
		return _serviceClient;
	}

	public static void loadApiContext() {
		ApiCredential apiCredential = new ApiCredential();
		apiCredential.seteBayToken(PropertiesValues.EBAY_TOKEN);

		_apiContext = new ApiContext();
		_apiContext.setApiCredential(apiCredential);
		_apiContext.setApiServerUrl("https://api.ebay.com/wsapi");
		_apiContext.setSite(SiteCodeType.US);
	}

	public static void loadeBayServiceClient() {
		ClientConfig config = new ClientConfig();

		config.setApplicationId(PropertiesValues.APPLICATION_ID);

		_serviceClient = FindingServiceClientFactory.getServiceClient(config);
	}

	public static void loadeBayServiceClient(String applicationId) {
		ClientConfig config = new ClientConfig();

		config.setApplicationId(applicationId);

		_serviceClient = FindingServiceClientFactory.getServiceClient(config);
	}

	private static ApiContext _apiContext;
	private static FindingServicePortType _serviceClient;

}