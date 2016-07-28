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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

/**
 * @author Jonathan McCann
 */
public class RecaptchaUtil {

	public static boolean verifyRecaptchaResponse(String recaptchaResponse)
		throws IOException {

		if (ValidatorUtil.isNull(recaptchaResponse)) {
			return false;
		}

		BufferedReader bufferedReader = null;

		try {
			URL url = new URL(_VERIFY_RECAPTCHA_URL + recaptchaResponse);

			HttpsURLConnection connection =
				(HttpsURLConnection)url.openConnection();

			bufferedReader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));

			Gson gson = new Gson();

			Map<Object, Object> response =
				gson.fromJson(bufferedReader, Map.class);

			return (boolean)response.get("success");
		}
		catch (Exception e) {
			return false;
		}
		finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}

	private static final String _VERIFY_RECAPTCHA_URL =
		"https://www.google.com/recaptcha/api/siteverify?secret=" +
			PropertiesValues.RECAPTCHA_SECRET_KEY + "&response=";

}