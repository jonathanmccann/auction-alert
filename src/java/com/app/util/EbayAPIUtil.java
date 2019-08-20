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
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

/**
 * @author Jonathan McCann
 */
public class EbayAPIUtil {

	public static ApiContext getApiContext() {
		return _apiContext;
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

	private static ApiContext _apiContext;

}