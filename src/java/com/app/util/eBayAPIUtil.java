package com.app.util;

import com.ebay.services.client.ClientConfig;
import com.ebay.services.client.FindingServiceClientFactory;
import com.ebay.services.finding.FindingServicePortType;

import java.util.Properties;

public class eBayAPIUtil {
	public static FindingServicePortType getServiceClient() {
		return _serviceClient;
	}

	public static void loadeBayServiceClient() {
		Properties properties = PropertiesUtil.getConfigurationProperties();

		ClientConfig config = new ClientConfig();

		config.setApplicationId(
			properties.getProperty(PropertiesUtil.APPLICATION_ID));

		_serviceClient = FindingServiceClientFactory.getServiceClient(config);
	}

	private static FindingServicePortType _serviceClient;
}