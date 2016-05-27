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

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;

import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

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

	protected static String getCurrentDate() {
		DateFormat dateFormat = _DATE_FORMAT.get();

		return dateFormat.format(new Date());
	}

	protected static Template getEmailTemplate() throws IOException {
		if (null != _emailTemplate) {
			return _emailTemplate;
		}

		Resource resource = new ClassPathResource("/template");

		_configuration.setDirectoryForTemplateLoading(resource.getFile());

		_emailTemplate = _configuration.getTemplate("/email_body.ftl");

		return _emailTemplate;
	}

	protected static Template getTextTemplate(String mobileOperatingSystem)
		throws IOException {

		if (!_templatesInitialized) {
			Resource resource = new ClassPathResource("/template");

			_configuration.setDirectoryForTemplateLoading(resource.getFile());

			_androidTextTemplate = _configuration.getTemplate(
				"/text_body_android.ftl");
			_iosTextTemplate = _configuration.getTemplate("/text_body_ios.ftl");
			_textTemplate = _configuration.getTemplate("/text_body.ftl");

			_templatesInitialized = true;
		}

		if ("iOS".equalsIgnoreCase(mobileOperatingSystem)) {
			return _iosTextTemplate;
		}
		else if ("Android".equalsIgnoreCase(mobileOperatingSystem)) {
			return _androidTextTemplate;
		}
		else {
			return _textTemplate;
		}
	}

	protected static boolean[] getNotificationDeliveryMethods(
		NotificationPreferences notificationPreferences)
		throws DatabaseConnectionException, SQLException {

		DateTime dateTime = new DateTime(
			DateTimeZone.forID(notificationPreferences.getTimeZone()));

		boolean[] notificationDeliveryMethod = new boolean[2];

		if (notificationPreferences.isBasedOnTime()) {
			_setNotificationDeliveryMethodsBasedOnTime(
				notificationPreferences, dateTime, notificationDeliveryMethod);
		}
		else {
			_setNotificationDeliveryMethodsNotBasedOnTime(
				notificationPreferences, notificationDeliveryMethod);
		}

		_log.debug("Sending via email: {}", notificationDeliveryMethod[0]);
		_log.debug("Sending via text: {}", notificationDeliveryMethod[1]);

		return notificationDeliveryMethod;
	}

	private static void _setNotificationDeliveryMethodsBasedOnTime(
		NotificationPreferences notificationPreferences, DateTime dateTime,
		boolean[] notificationDeliveryMethod) {

		int hourOfDay = dateTime.getHourOfDay();
		int dayOfWeek = dateTime.getDayOfWeek();

		boolean isDaytime = true;
		boolean isWeekday = true;

		if ((hourOfDay < notificationPreferences.getStartOfDay()) ||
			(hourOfDay >= notificationPreferences.getEndOfDay())) {

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

	private static void _setNotificationDeliveryMethodsNotBasedOnTime(
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

	private static final Logger _log = LoggerFactory.getLogger(
		MailSender.class);

	private static final Configuration _configuration = new Configuration(
		Configuration.VERSION_2_3_21);

	private static boolean _templatesInitialized;
	private static Template _androidTextTemplate;
	private static Template _iosTextTemplate;
	private static Template _emailTemplate;
	private static Template _textTemplate;

}