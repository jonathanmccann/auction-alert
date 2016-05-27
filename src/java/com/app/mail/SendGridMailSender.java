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

package com.app.mail;

import com.app.exception.DatabaseConnectionException;
import com.app.model.NotificationPreferences;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.util.NotificationPreferencesUtil;
import com.app.util.PropertiesValues;
import com.app.util.UserUtil;
import com.app.util.ValidatorUtil;

import com.sendgrid.SendGrid;

import freemarker.template.Template;

import java.io.StringWriter;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class SendGridMailSender implements MailSender {

	@Override
	public void sendSearchResultsToRecipient(
			int userId,
			Map<SearchQuery, List<SearchResult>> searchQueryResultMap)
		throws DatabaseConnectionException, SQLException {

		_log.info(
			"Sending search results for {} queries for userId: {}",
			searchQueryResultMap.size(), userId);

		User user = UserUtil.getUserByUserId(userId);

		NotificationPreferences notificationPreferences =
			NotificationPreferencesUtil.getNotificationPreferencesByUserId(
				userId);

		boolean[] notificationDeliveryMethod =
			MailUtil.getNotificationDeliveryMethods(notificationPreferences);

		try {
			for (Map.Entry<SearchQuery, List<SearchResult>> mapEntry :
					searchQueryResultMap.entrySet()) {

				SendGrid sendgrid = new SendGrid(
					PropertiesValues.SENDGRID_API_KEY);

				if (notificationDeliveryMethod[0]) {
					SendGrid.Email email = _populateEmailMessage(
						mapEntry.getKey(), mapEntry.getValue(),
						user.getEmailAddress());

					sendgrid.send(email);
				}

				if (notificationDeliveryMethod[1] &&
					ValidatorUtil.isNotNull(user.getPhoneNumber())) {

					List<SearchResult> searchResults = mapEntry.getValue();

					for (SearchResult searchResult : searchResults) {
						SendGrid.Email email = _populateTextMessage(
							searchResult, user.getPhoneNumberEmailAddress(),
							user.getMobileOperatingSystem());

						sendgrid.send(email);
					}
				}
			}
		}
		catch (Exception e) {
			_log.error("Unable to send search results to userId: {}", userId, e);
		}
	}

	private SendGrid.Email _populateEmailMessage(
			SearchQuery searchQuery, List<SearchResult> searchResults,
			String recipientEmailAddress)
		throws Exception {

		SendGrid.Email email = new SendGrid.Email();

		email.addTo(recipientEmailAddress);
		email.setFrom(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);
		email.setSubject(
			"New Search Results - " + MailUtil.getCurrentDate());

		_populateMessage(
			searchQuery, searchResults, email, MailUtil.getEmailTemplate());

		return email;
	}

	private void _populateMessage(
			SearchQuery searchQuery, List<SearchResult> searchResults,
			SendGrid.Email email, Template template)
		throws Exception {

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("searchQuery", searchQuery);
		rootMap.put("searchResults", searchResults);

		StringWriter stringWriter = new StringWriter();

		template.process(rootMap, stringWriter);

		email.setText(stringWriter.toString());
	}

	private SendGrid.Email _populateTextMessage(
			SearchResult searchResult, String recipientPhoneNumberEmailAddress,
			String mobileOperatingSystem)
		throws Exception {

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(searchResult);

		SendGrid.Email email = new SendGrid.Email();

		email.addTo(recipientPhoneNumberEmailAddress);
		email.setFrom(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);

		_populateMessage(
			null, searchResults, email,
			MailUtil.getTextTemplate(mobileOperatingSystem));

		return email;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SendGridMailSender.class);

}