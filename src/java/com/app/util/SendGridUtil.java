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

import com.app.exception.DatabaseConnectionException;
import com.app.json.sendgrid.SendGridBounceJsonResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jonathan McCann
 */
public class SendGridUtil {

	public static void handleSendGridEvent(
		String sendGridJsonEvent, String key, String value) {

		if (ValidatorUtil.isNull(key) || ValidatorUtil.isNull(value) ||
			!key.equals(PropertiesValues.SENDGRID_WEBHOOK_KEY) ||
			!value.equals(PropertiesValues.SENDGRID_WEBHOOK_VALUE)) {

			_log.error("Attempt to access SendGrid event webhook endpoint");

			return;
		}

		_log.error("sendGridJsonEvent = {}", sendGridJsonEvent);

		JsonDeserializer<SendGridBounceJsonResponse> deserializer =
			(json, typeOfT, context) -> {
				JsonObject jsonObject = json.getAsJsonObject();

				String jsonSearchResultIds =
					jsonObject.get("searchResultIds").getAsString();

				List<Integer> searchResultIds = Arrays.stream(
					jsonSearchResultIds.split(",")
				).map(
					Integer::parseInt
				).collect(
					Collectors.toList()
				);

				return new SendGridBounceJsonResponse(
					searchResultIds, jsonObject.get("type").getAsString());
			};

		Gson gson = new GsonBuilder()
			.registerTypeAdapter(
				SendGridBounceJsonResponse.class, deserializer
			).create();

		Type listType =
			new TypeToken<ArrayList<SendGridBounceJsonResponse>>(){}.getType();

		List<SendGridBounceJsonResponse> sendGridBounceJsonResponses =
			gson.fromJson(sendGridJsonEvent, listType);

		for (SendGridBounceJsonResponse sendGridBounceJsonResponse :
			sendGridBounceJsonResponses) {

			String type = sendGridBounceJsonResponse.getType();

			_log.error("Type = {}", type);

			if (!type.equals(_BLOCKED_TYPE)) {
				continue;
			}

			List<Integer> searchResultIds =
				sendGridBounceJsonResponse.getSearchResultIds();

			_log.error("Search Result IDs = {}", searchResultIds);

			try {
				SearchResultUtil.updateSearchResultsDeliveredStatus(
					searchResultIds, false);
			}
			catch (DatabaseConnectionException | SQLException e) {
				_log.error(
					"Unable to set search result IDs as undelivered - {}",
					searchResultIds);
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SendGridUtil.class);

	private static final String _BLOCKED_TYPE = "blocked";

}