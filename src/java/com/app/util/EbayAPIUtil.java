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

import com.ebay.api.client.auth.oauth2.CredentialUtil;

/**
 * @author Jonathan McCann
 */
public class EbayAPIUtil {

	public static void loadApiContext() {
		ClassLoader classLoader =
			Thread.currentThread().getContextClassLoader();

		CredentialUtil.load(
			classLoader.getResourceAsStream("ebay-config.yaml"));
	}

}