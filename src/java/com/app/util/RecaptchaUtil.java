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

import java.io.IOException;

import java.util.Map;

/**
 * @author Jonathan McCann
 */
public class RecaptchaUtil {

	public static boolean verifyRecaptchaResponse(String recaptchaResponse)
		throws IOException {

		if (ValidatorUtil.isNull(recaptchaResponse)) {
			return false;
		}

		try {
			HttpClient httpClient = HttpClients.createDefault();

			HttpGet httpGet = new HttpGet(
				_VERIFY_RECAPTCHA_URL + recaptchaResponse);

			HttpResponse response = httpClient.execute(httpGet);

			Gson gson = new Gson();

			Map<Object, Object> responseMap = gson.fromJson(
				EntityUtils.toString(response.getEntity()), Map.class);

			return (boolean)responseMap.get("success");
		}
		catch (Exception e) {
			return false;
		}
	}

	private static final String _VERIFY_RECAPTCHA_URL =
		"https://www.google.com/recaptcha/api/siteverify?secret=" +
			PropertiesValues.RECAPTCHA_SECRET_KEY + "&response=";

}