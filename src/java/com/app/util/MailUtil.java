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

import com.app.model.SearchQuery;
import com.app.model.SearchResult;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Jonathan McCann
 */
public class MailUtil {

	public static void sendSearchResultsToRecipients(
		Map<SearchQuery, List<SearchResult>> searchQueryResultMap) {

		_log.info(
			"Sending search results for {} queries",
			searchQueryResultMap.size());

		Session session = authenticateOutboundEmailAddress();

		List<String> recipientEmailAddresses = getRecipientEmailAddresses();

		List<String> recipientPhoneNumbers = getRecipientPhoneNumbers();

		setNotificationDeliveryMethods(
			recipientEmailAddresses, recipientPhoneNumbers);

		try {
			for (Map.Entry<SearchQuery, List<SearchResult>> mapEntry :
					searchQueryResultMap.entrySet()) {

				if (_sendViaEmail) {
					Message emailMessage = populateEmailMessage(
						mapEntry.getKey(), mapEntry.getValue(),
						recipientEmailAddresses,
						session.getProperty(
							PropertiesKeys.OUTBOUND_EMAIL_ADDRESS),
						session);

					Transport.send(emailMessage);
				}

				if (_sendViaText) {
					Message textMessage = populateTextMessage(
						mapEntry.getValue(), recipientPhoneNumbers, session);

					Transport.send(textMessage);
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

	private static void convertPhoneNumbersToEmailAddresses(
		List<String> recipientPhoneNumbers) {

		String phoneCarrierEmailSuffix = _carrierSuffixMap.get(
			PropertiesValues.RECIPIENT_PHONE_CARRIER);

		for (int i = 0; i < recipientPhoneNumbers.size(); i++) {
			recipientPhoneNumbers.set(
				i, recipientPhoneNumbers.get(i) + phoneCarrierEmailSuffix);
		}
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

	private static List<String> getRecipientEmailAddresses() {
		if (_initializedEmailAddresses) {
			return _recipientEmailAddresses;
		}

		_initializedEmailAddresses = true;

		String recipientEmailAddresses =
			PropertiesValues.RECIPIENT_EMAIL_ADDRESSES;

		if (ValidatorUtil.isNotNull(recipientEmailAddresses)) {
			List<String> recipientEmailAddressesList = Arrays.asList(
				recipientEmailAddresses.split(","));

			validateEmailAddresses(recipientEmailAddressesList);

			_recipientEmailAddresses = recipientEmailAddressesList;

			return _recipientEmailAddresses;
		}
		else {
			_recipientEmailAddresses = new ArrayList<>();

			return _recipientEmailAddresses;
		}
	}

	private static List<String> getRecipientPhoneNumbers() {
		if (_initializedPhoneNumbers) {
			return _recipientPhoneNumbers;
		}

		_initializedPhoneNumbers = true;

		String recipientPhoneNumbers = PropertiesValues.RECIPIENT_PHONE_NUMBERS;

		if (ValidatorUtil.isNotNull(recipientPhoneNumbers)) {
			List<String> recipientPhoneNumbersList = Arrays.asList(
				recipientPhoneNumbers.split(","));

			validatePhoneNumbers(recipientPhoneNumbersList);

			convertPhoneNumbersToEmailAddresses(recipientPhoneNumbersList);

			_recipientPhoneNumbers = recipientPhoneNumbersList;

			return _recipientPhoneNumbers;
		}
		else {
			_recipientPhoneNumbers = new ArrayList<>();

			return _recipientPhoneNumbers;
		}
	}

	private static Template getTextTemplate() throws IOException {
		if (null != _textTemplate) {
			return _textTemplate;
		}

		Resource resource = new ClassPathResource("/template");

		_configuration.setDirectoryForTemplateLoading(resource.getFile());

		String template = "/text_body.ftl";

		String recipientMobileOperatingSystem =
			PropertiesValues.RECIPIENT_MOBILE_OPERATING_SYSTEM;

		if ("iOS".equalsIgnoreCase(recipientMobileOperatingSystem)) {
			template = "/text_body_ios.ftl";
		}
		else if ("Android".equalsIgnoreCase(recipientMobileOperatingSystem)) {
			template = "/text_body_android.ftl";
		}

		_textTemplate = _configuration.getTemplate(template);

		return _textTemplate;
	}

	private static Message populateEmailMessage(
			SearchQuery searchQuery,
			List<SearchResult> searchResults,
			List<String> recipientEmailAddresses, String emailFrom,
			Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(emailFrom));

		for (String recipientEmailAddress : recipientEmailAddresses) {
			message.addRecipient(
				Message.RecipientType.CC,
				new InternetAddress(recipientEmailAddress));
		}

		DateFormat dateFormat = _DATE_FORMAT.get();

		message.setSubject(
			"New Search Results - " + dateFormat.format(new Date()));

		populateMessage(
			searchQuery, searchResults, message, getEmailTemplate());

		return message;
	}

	private static void populateMessage(
			SearchQuery searchQuery,
			List<SearchResult> searchResults, Message message,
			Template template)
		throws Exception {

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("searchQuery", searchQuery);
		rootMap.put("searchResults", searchResults);

		StringWriter stringWriter = new StringWriter();

		template.process(rootMap, stringWriter);

		message.setText(stringWriter.toString());
	}

	private static Message populateTextMessage(
			List<SearchResult> searchResults,
			List<String> recipientPhoneNumbers, Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		for (String recipientPhoneNumber : recipientPhoneNumbers) {
			message.addRecipient(
				Message.RecipientType.CC,
				new InternetAddress(recipientPhoneNumber));
		}

		populateMessage(null, searchResults, message, getTextTemplate());

		return message;
	}

	private static void setNotificationDeliveryMethods(
		List<String> recipientEmailAddresses,
		List<String> recipientPhoneNumbers) {

		if (PropertiesValues.SEND_NOTIFICATIONS_BASED_ON_TIME) {
			DateTime dateTime = new DateTime();

			int hourOfDay = dateTime.getHourOfDay();
			int dayOfWeek = dateTime.getDayOfWeek();

			if ((dayOfWeek == _SATURDAY) || (dayOfWeek == _SUNDAY)) {
				_sendViaEmail = false;
				_sendViaText = true;
			}
			else if ((hourOfDay < _START_OF_DAY) ||
					 (hourOfDay >= _END_OF_DAY)) {

				_sendViaEmail = false;
				_sendViaText = true;
			}
			else {
				_sendViaEmail = true;
				_sendViaText = false;
			}
		}

		if (recipientEmailAddresses.size() == 0) {
			_sendViaEmail = false;
		}

		if (recipientPhoneNumbers.size() == 0) {
			_sendViaText = false;
		}

		_log.debug("Sending via email: {}", _sendViaEmail);
		_log.debug("Sending via text: {}", _sendViaText);
	}

	private static void validateEmailAddresses(
		List<String> recipientEmailAddressesArray) {

		Iterator<String> iterator = recipientEmailAddressesArray.iterator();

		while (iterator.hasNext()) {
			String emailAddress = iterator.next();

			Matcher matcher = _emailAddressPattern.matcher(emailAddress);

			if (!matcher.matches()) {
				_log.debug(
					"{} is not a valid email address",
					emailAddress);

				iterator.remove();
			}
		}
	}

	private static void validatePhoneNumbers(
		List<String> recipientPhoneNumbersArray) {

		Iterator<String> iterator = recipientPhoneNumbersArray.iterator();

		while (iterator.hasNext()) {
			String phoneNumber = iterator.next();

			Matcher matcher = _phoneNumberPattern.matcher(phoneNumber);

			if (!matcher.matches()) {
				_log.debug(
					"{} is not a valid email address",
					phoneNumber);

				iterator.remove();
			}
		}
	}

	private static final ThreadLocal<DateFormat> _DATE_FORMAT =
		new ThreadLocal<DateFormat>() {
			@Override
			protected DateFormat initialValue() {
				return new SimpleDateFormat("MM/dd/yyyy");
			}
		};

	private static final int _END_OF_DAY = 17;

	private static final int _SATURDAY = 6;

	private static final int _START_OF_DAY = 7;

	private static final int _SUNDAY = 7;

	private static final Logger _log = LoggerFactory.getLogger(MailUtil.class);

	private static final Map<String, String> _carrierSuffixMap =
		new HashMap<>();
	private static final Configuration _configuration = new Configuration(
		Configuration.VERSION_2_3_21);
	private static final Pattern _emailAddressPattern = Pattern.compile(
		"[a-zA-Z0-9]*@[a-zA-Z0-9]*\\.[a-zA-Z]{1,6}");
	private static Template _emailTemplate;
	private static boolean _initializedEmailAddresses = false;
	private static boolean _initializedPhoneNumbers = false;
	private static final Pattern _phoneNumberPattern = Pattern.compile(
		"[0-9]{10,10}");
	private static List<String> _recipientEmailAddresses = new ArrayList<>();
	private static List<String> _recipientPhoneNumbers = new ArrayList<>();
	private static boolean _sendViaEmail = false;
	private static boolean _sendViaText = false;
	private static Template _textTemplate;

	static {
		_carrierSuffixMap.put("AT&T", "@txt.att.net");
		_carrierSuffixMap.put("T-Mobile", "@tmomail.net");
		_carrierSuffixMap.put("Verizon", "@vtext.com");
		_carrierSuffixMap.put("Sprint", "@messaging.sprintpcs.com");
		_carrierSuffixMap.put("Virgin Mobile", "@vmobl.com");
		_carrierSuffixMap.put("Tracfone", "@mmst5.tracfone.com");
		_carrierSuffixMap.put("Metro PCS", "@mymetropcs.com");
		_carrierSuffixMap.put("Boost Mobile", "@myboostmobile.com");
		_carrierSuffixMap.put("Cricket", "@sms.mycricket.com");
		_carrierSuffixMap.put("Alltel", "@message.alltel.com");
		_carrierSuffixMap.put("Ptel", "@ptel.com");
		_carrierSuffixMap.put("U.S. Cellular", "@email.uscc.net");
	}

}