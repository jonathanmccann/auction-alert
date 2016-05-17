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
import com.app.model.NotificationPreferences;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;

import com.app.model.User;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringWriter;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
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

import org.joda.time.DateTime;

import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Jonathan McCann
 */
public class MailUtil {

	public static void sendSearchResultsToRecipient(
			int userId,
			Map<SearchQuery, List<SearchResult>> searchQueryResultMap)
		throws DatabaseConnectionException, SQLException {

		_log.info(
			"Sending search results for {} queries",
			searchQueryResultMap.size());

		Session session = authenticateOutboundEmailAddress();

		User user = UserUtil.getUserByUserId(userId);

		NotificationPreferences notificationPreferences =
			NotificationPreferencesUtil.getNotificationPreferencesByUserId(
				userId);

		boolean[] notificationDeliveryMethod = setNotificationDeliveryMethod(
			notificationPreferences);

		try {
			for (Map.Entry<SearchQuery, List<SearchResult>> mapEntry :
					searchQueryResultMap.entrySet()) {

				if (notificationDeliveryMethod[0]) {
					Message emailMessage = populateEmailMessage(
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
						Message textMessage = populateTextMessage(
							searchResult, user.getPhoneNumberEmailAddress(),
							user.getMobileOperatingSystem(),
							session);

						Transport.send(textMessage);
					}
				}
			}
		}
		catch (Exception e) {
			_log.error("Unable to send search result to recipients", e);
		}
	}

	private static Session authenticateOutboundEmailAddress() {
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

	private static Template getEmailTemplate() throws IOException {
		if (null != _emailTemplate) {
			return _emailTemplate;
		}

		Resource resource = new ClassPathResource("/template");

		_configuration.setDirectoryForTemplateLoading(resource.getFile());

		_emailTemplate = _configuration.getTemplate("/email_body.ftl");

		return _emailTemplate;
	}

	private static Template getTextTemplate(
		String mobileOperatingSystem) throws IOException {

		if (null != _textTemplate) {
			return _textTemplate;
		}

		Resource resource = new ClassPathResource("/template");

		_configuration.setDirectoryForTemplateLoading(resource.getFile());

		String template = "/text_body.ftl";

		if ("iOS".equalsIgnoreCase(mobileOperatingSystem)) {
			template = "/text_body_ios.ftl";
		}
		else if ("Android".equalsIgnoreCase(mobileOperatingSystem)) {
			template = "/text_body_android.ftl";
		}

		_textTemplate = _configuration.getTemplate(template);

		return _textTemplate;
	}

	private static Message populateEmailMessage(
			SearchQuery searchQuery, List<SearchResult> searchResults,
			String recipientEmailAddress, String emailFrom,
			Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(emailFrom));

		message.addRecipient(
			Message.RecipientType.TO,
			new InternetAddress(recipientEmailAddress));

		DateFormat dateFormat = _DATE_FORMAT.get();

		message.setSubject(
			"New Search Results - " + dateFormat.format(new Date()));

		populateMessage(
			searchQuery, searchResults, message, getEmailTemplate());

		return message;
	}

	private static void populateMessage(
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

	private static Message populateTextMessage(
			SearchResult searchResult, String recipientPhoneNumber,
			String mobileOperatingSystem, Session session)
		throws Exception {

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(searchResult);

		Message message = new MimeMessage(session);

		message.addRecipient(
			Message.RecipientType.TO,
			new InternetAddress(recipientPhoneNumber));

		populateMessage(
			null, searchResults, message,
			getTextTemplate(mobileOperatingSystem));

		return message;
	}

	private static boolean[] setNotificationDeliveryMethod(
			NotificationPreferences notificationPreferences)
		throws DatabaseConnectionException, SQLException {

		DateTime dateTime = new DateTime(
			DateTimeZone.forID(notificationPreferences.getTimeZone()));

		boolean[] notificationDeliveryMethod = new boolean[2];

		if (notificationPreferences.isBasedOnTime()) {
			setNotificationDeliveryMethodsBasedOnTime(
				notificationPreferences, dateTime, notificationDeliveryMethod);
		}
		else {
			setNotificationDeliveryMethodsNotBasedOnTime(
				notificationPreferences, notificationDeliveryMethod);
		}

		_log.debug("Sending via email: {}", notificationDeliveryMethod[0]);
		_log.debug("Sending via text: {}", notificationDeliveryMethod[1]);

		return notificationDeliveryMethod;
	}

	private static void setNotificationDeliveryMethodsBasedOnTime(
		NotificationPreferences notificationPreferences, DateTime dateTime,
		boolean[] notificationDeliveryMethod) {

		int hourOfDay = dateTime.getHourOfDay();
		int dayOfWeek = dateTime.getDayOfWeek();

		boolean isDaytime = true;
		boolean isWeekday = true;

		if ((hourOfDay < notificationPreferences.getStartOfDay()) ||
			hourOfDay >= notificationPreferences.getEndOfDay()) {

			isDaytime = false;
		}

		if ((dayOfWeek == _SATURDAY) || (dayOfWeek == _SUNDAY)) {
			isWeekday = false;
		}

		if (isWeekday && isDaytime) {
			notificationDeliveryMethod[0] =
				notificationPreferences.isWeekdayDayEmailNotification();
			notificationDeliveryMethod[1] =
				notificationPreferences.isWeekdayDayTextNotification();
		}
		else if (isWeekday && !isDaytime) {
			notificationDeliveryMethod[0] =
				notificationPreferences.isWeekdayNightEmailNotification();
			notificationDeliveryMethod[1] =
				notificationPreferences.isWeekdayNightTextNotification();
		}
		else if (!isWeekday && isDaytime) {
			notificationDeliveryMethod[0] =
				notificationPreferences.isWeekendDayEmailNotification();
			notificationDeliveryMethod[1] =
				notificationPreferences.isWeekendDayTextNotification();
		}
		else {
			notificationDeliveryMethod[0] =
				notificationPreferences.isWeekendNightEmailNotification();
			notificationDeliveryMethod[1] =
				notificationPreferences.isWeekendNightTextNotification();
		}
	}

	private static void setNotificationDeliveryMethodsNotBasedOnTime(
		NotificationPreferences notificationPreferences,
		boolean[] notificationDeliveryMethod) {

		notificationDeliveryMethod[0] =
			notificationPreferences.isEmailNotification();
		notificationDeliveryMethod[1] =
			notificationPreferences.isTextNotification();
	}

	private static final ThreadLocal<DateFormat> _DATE_FORMAT =
		new ThreadLocal<DateFormat>() {
			@Override
			protected DateFormat initialValue() {
				return new SimpleDateFormat("MM/dd/yyyy");
			}
		};

	private static final int _SATURDAY = 6;
	private static final int _SUNDAY = 7;

	private static final Logger _log = LoggerFactory.getLogger(MailUtil.class);

	private static final Configuration _configuration = new Configuration(
		Configuration.VERSION_2_3_21);
	private static Template _emailTemplate;
	private static Template _textTemplate;

}