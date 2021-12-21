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

import com.app.exception.OAuthTokenException;

import com.ebay.api.client.auth.oauth2.OAuth2Api;
import com.ebay.api.client.auth.oauth2.model.AccessToken;
import com.ebay.api.client.auth.oauth2.model.Environment;
import com.ebay.api.client.auth.oauth2.model.OAuthResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author Jonathan McCann
 */
public class OAuthTokenUtil {

	public static String getAccessToken() throws Exception {
		OAuth2Api oauth2Api = new OAuth2Api();

		OAuthResponse oauth2Response = oauth2Api.getApplicationToken(
			Environment.PRODUCTION, SCOPE_LIST_PRODUCTION);

		Optional<AccessToken> applicationToken =
			oauth2Response.getAccessToken();

		if (!applicationToken.isPresent()) {
			throw new OAuthTokenException();
		}

		AccessToken accessToken = applicationToken.get();

		return accessToken.getToken();
	}

	public static String executeRequest(String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);

		return execute(httpGet);
	}

	public static String executeRequest(String url, Map<String, String> headers)
		throws Exception {

		HttpGet httpGet = new HttpGet(url);

		for (Map.Entry<String,String> header : headers.entrySet()) {
			httpGet.addHeader(header.getKey(), header.getValue());
		}

		return execute(httpGet);
	}

	private static String execute(HttpGet httpGet) throws Exception {
		HttpClient httpClient = HttpClients.createDefault();

		httpGet.addHeader("Authorization", "Bearer " + getAccessToken());

		HttpResponse response = httpClient.execute(httpGet);

		return EntityUtils.toString(response.getEntity());
	}

	private static final List<String> SCOPE_LIST_PRODUCTION =
		new ArrayList<String>() {{
			add("https://api.ebay.com/oauth/api_scope");
		}};

}