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
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.util.PropertiesUtil;
import com.app.util.PropertiesValues;
import com.app.util.UserUtil;

import java.sql.SQLException;

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

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.NumberTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * @author Jonathan McCann
 */
public class DefaultMailSender implements MailSender {

	@Override
	public void sendContactMessage(String emailAddress, String message)
		throws Exception {

		Session session = _authenticateOutboundEmailAddress();

		Message emailMessage = _populateContactMessage(
			emailAddress, message, session);

		Transport.send(emailMessage);
	}

	@Override
	public void sendPasswordResetToken(
			String emailAddress, String passwordResetToken)
		throws Exception {

		Session session = _authenticateOutboundEmailAddress();

		Message message = _populatePasswordResetToken(
			emailAddress, passwordResetToken, session);

		Transport.send(message);
	}

	@Override
	public void sendSearchResultsToRecipient(
			int userId,
			Map<SearchQuery, List<SearchResult>> searchQueryResultMap)
		throws DatabaseConnectionException, SQLException {

		User user = UserUtil.getUserByUserId(userId);

		if (!user.isEmailNotification()) {
			return;
		}

		int emailsSent = user.getEmailsSent();

		if (emailsSent >= PropertiesValues.NUMBER_OF_EMAILS_PER_DAY) {
			_log.info(
				"User ID: {} has reached their email limit for the day",
				user.getUserId());

			return;
		}

		_log.debug(
			"Sending search results for {} queries for userId: {}",
			searchQueryResultMap.size(), userId);

		Session session = _authenticateOutboundEmailAddress();

		try {
			Message emailMessage = _populateEmailMessage(
				searchQueryResultMap, user.getEmailAddress(),
				PropertiesValues.OUTBOUND_EMAIL_ADDRESS,
				user.getUnsubscribeToken(), session);

			Transport.send(emailMessage);

			emailsSent++;
		}
		catch (Exception e) {
			_log.error("Unable to send search results to userId: {}", userId, e);
		}

		UserUtil.updateEmailsSent(user.getUserId(), emailsSent);
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

	private static Message _populateContactMessage(
			String emailAddress, String messageBody, Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(
			new InternetAddress(PropertiesValues.OUTBOUND_EMAIL_ADDRESS));

		message.addRecipient(
			Message.RecipientType.TO,
			new InternetAddress(PropertiesValues.OUTBOUND_EMAIL_ADDRESS));

		message.setSubject("You Have A New Message From " + emailAddress);
		message.setText(messageBody);

		return message;
	}

	private Message _populateEmailMessage(
			Map<SearchQuery, List<SearchResult>> searchQueryResultMap,
			String recipientEmailAddress, String emailFrom,
			String unsubscribeToken, Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(emailFrom));

		message.addRecipient(
			Message.RecipientType.TO,
			new InternetAddress(recipientEmailAddress));

		message.setSubject(
			"New Search Results - " + MailUtil.getCurrentDate());

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("emailAddress", recipientEmailAddress);
		rootMap.put("searchQueryResultMap", searchQueryResultMap);
		rootMap.put(
			"unsubscribeToken",
			MailUtil.escapeUnsubscribeToken(unsubscribeToken));
		rootMap.put("numberTool", new NumberTool());
		rootMap.put("rootDomainName", PropertiesValues.ROOT_DOMAIN_NAME);

		String messageBody = VelocityEngineUtils.mergeTemplateIntoString(
			velocityEngine, "template/email_body.vm", "UTF-8", rootMap);

		message.setContent(messageBody, "text/html");

		return message;
	}

	private Message _populatePasswordResetToken(
			String emailAddress, String passwordResetToken, Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(
			new InternetAddress(PropertiesValues.OUTBOUND_EMAIL_ADDRESS));

		message.addRecipient(
			Message.RecipientType.TO, new InternetAddress(emailAddress));

		message.setSubject("Password Reset Token");

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("passwordResetToken", passwordResetToken);
		rootMap.put("rootDomainName", PropertiesValues.ROOT_DOMAIN_NAME);

		String messageBody = VelocityEngineUtils.mergeTemplateIntoString(
			velocityEngine, "template/password_token.vm", "UTF-8", rootMap);

		message.setContent(messageBody, "text/html");

		return message;
	}

	@Autowired
	private VelocityEngine velocityEngine;

	private static final Logger _log = LoggerFactory.getLogger(
		DefaultMailSender.class);

}