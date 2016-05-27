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
import com.app.util.PropertiesKeys;
import com.app.util.PropertiesUtil;
import com.app.util.PropertiesValues;
import com.app.util.UserUtil;
import com.app.util.ValidatorUtil;

import freemarker.template.Template;

import java.io.StringWriter;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class DefaultMailSender implements MailSender {

	@Override
	public void sendSearchResultsToRecipient(
			int userId,
			Map<SearchQuery, List<SearchResult>> searchQueryResultMap)
		throws DatabaseConnectionException, SQLException {

		_log.info(
			"Sending search results for {} queries for userId: {}",
			searchQueryResultMap.size(), userId);

		Session session = _authenticateOutboundEmailAddress();

		User user = UserUtil.getUserByUserId(userId);

		NotificationPreferences notificationPreferences =
			NotificationPreferencesUtil.getNotificationPreferencesByUserId(
				userId);

		boolean[] notificationDeliveryMethod =
			MailUtil.getNotificationDeliveryMethods(notificationPreferences);

		try {
			for (Map.Entry<SearchQuery, List<SearchResult>> mapEntry :
					searchQueryResultMap.entrySet()) {

				if (notificationDeliveryMethod[0]) {
					Message emailMessage = _populateEmailMessage(
						mapEntry.getKey(), mapEntry.getValue(),
						user.getEmailAddress(),
						session.getProperty(
							PropertiesKeys.OUTBOUND_EMAIL_ADDRESS),
						session);

					Transport.send(emailMessage);
				}

				if (notificationDeliveryMethod[1] &&
					ValidatorUtil.isNotNull(user.getPhoneNumber())) {

					List<SearchResult> searchResults = mapEntry.getValue();

					for (SearchResult searchResult : searchResults) {
						Message textMessage = _populateTextMessage(
							searchResult, user.getPhoneNumberEmailAddress(),
							user.getMobileOperatingSystem(), session);

						Transport.send(textMessage);
					}
				}
			}
		}
		catch (Exception e) {
			_log.error("Unable to send search results to userId: {}", userId, e);
		}
	}

	private static Session _authenticateOutboundEmailAddress() {
		return Session.getInstance(
			PropertiesUtil.getConfigurationProperties(),
			new Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(
						PropertiesValues.OUTBOUND_EMAIL_ADDRESS,
						PropertiesValues.OUTBOUND_EMAIL_ADDRESS_PASSWORD);
				}

			});
	}

	private static Message _populateEmailMessage(
			SearchQuery searchQuery, List<SearchResult> searchResults,
			String recipientEmailAddress, String emailFrom, Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(emailFrom));

		message.addRecipient(
			Message.RecipientType.TO,
			new InternetAddress(recipientEmailAddress));

		message.setSubject(
			"New Search Results - " + MailUtil.getCurrentDate());

		_populateMessage(
			searchQuery, searchResults, message, MailUtil.getEmailTemplate());

		return message;
	}

	private static void _populateMessage(
			SearchQuery searchQuery, List<SearchResult> searchResults,
			Message message, Template template)
		throws Exception {

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("searchQuery", searchQuery);
		rootMap.put("searchResults", searchResults);

		StringWriter stringWriter = new StringWriter();

		template.process(rootMap, stringWriter);

		message.setText(stringWriter.toString());
	}

	private static Message _populateTextMessage(
			SearchResult searchResult, String recipientPhoneNumberEmailAddress,
			String mobileOperatingSystem, Session session)
		throws Exception {

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(searchResult);

		Message message = new MimeMessage(session);

		message.addRecipient(
			Message.RecipientType.TO,
			new InternetAddress(recipientPhoneNumberEmailAddress));

		_populateMessage(
			null, searchResults, message,
			MailUtil.getTextTemplate(mobileOperatingSystem));

		return message;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		DefaultMailSender.class);

}