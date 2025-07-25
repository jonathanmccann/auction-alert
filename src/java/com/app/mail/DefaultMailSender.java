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
import com.app.util.PropertiesValues;
import com.app.util.UserUtil;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
	public void sendAccountDeletionMessage(String emailAddress) {
		Session session = _authenticateOutboundEmailAddress();

		try {
			Message emailMessage = _populateMessage(
				emailAddress, "Account Deletion Successful",
				"account_deletion_email.vm", session);

			Transport.send(emailMessage);
		}
		catch (Exception e) {
			_log.error("Unable to send account deletion message", e);
		}
	}

	@Override
	public void sendCancellationMessage(String emailAddress) {
		Session session = _authenticateOutboundEmailAddress();

		try {
			Message emailMessage = _populateMessage(
				emailAddress, "Cancellation Successful",
				"cancellation_email.vm", session);

			Transport.send(emailMessage);
		}
		catch (Exception e) {
			_log.error("Unable to send cancellation message", e);
		}
	}

	@Override
	public void sendCardDetailsMessage(String emailAddress) {
		Session session = _authenticateOutboundEmailAddress();

		try {
			Message emailMessage = _populateMessage(
				emailAddress, "Card Details Updated", "card_details_email.vm",
				session);

			Transport.send(emailMessage);
		}
		catch (Exception e) {
			_log.error("Unable to send card details message", e);
		}
	}

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
		String emailAddress, String passwordResetToken) {

		Session session = _authenticateOutboundEmailAddress();

		try {
			Message emailMessage = _populatePasswordResetToken(
				emailAddress, passwordResetToken, session);

			Transport.send(emailMessage);
		}
		catch (Exception e) {
			_log.error("Unable to send password reset token", e);
		}
	}

	@Override
	public void sendPaymentFailedMessage(String emailAddress) {
		Session session = _authenticateOutboundEmailAddress();

		try {
			Message emailMessage = _populateMessage(
				emailAddress, "Payment Failed", "payment_failed_email.vm",
				session);

			Transport.send(emailMessage);
		}
		catch (Exception e) {
			_log.error("Unable to send payment failed message", e);
		}
	}

	@Override
	public void sendResubscribeMessage(String emailAddress) {
		Session session = _authenticateOutboundEmailAddress();

		try {
			Message emailMessage = _populateMessage(
				emailAddress, "Resubscribe Successful", "resubscribe_email.vm",
				session);

			Transport.send(emailMessage);
		}
		catch (Exception e) {
			_log.error("Unable to send resubscribe message", e);
		}
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
				searchQueryResultMap, user.getEmailAddress(), session);

			Transport.send(emailMessage);

			emailsSent++;
		}
		catch (Exception e) {
			_log.error(
				"Unable to send search results to userId: " + userId, e);
		}

		UserUtil.updateEmailsSent(user.getUserId(), emailsSent);
	}

	@Override
	public void sendWelcomeMessage(String emailAddress) {
		Session session = _authenticateOutboundEmailAddress();

		try {
			Message emailMessage = _populateMessage(
				emailAddress, "Welcome", "welcome_email.vm", session);

			Transport.send(emailMessage);
		}
		catch (Exception e) {
			_log.error("Unable to send welcome message", e);
		}
	}

	private static Session _authenticateOutboundEmailAddress() {
		if (_outboundEmailSessionProperties == null) {
			_outboundEmailSessionProperties = new Properties();

			_outboundEmailSessionProperties.put(
				"mail.smtp.auth", PropertiesValues.MAIL_SMTP_AUTH);

			_outboundEmailSessionProperties.put(
				"mail.smtp.host", PropertiesValues.MAIL_SMTP_HOST);

			_outboundEmailSessionProperties.put(
				"mail.smtp.port", PropertiesValues.MAIL_SMTP_PORT);

			_outboundEmailSessionProperties.put(
				"mail.smtp.starttls.enable",
				PropertiesValues.MAIL_SMTP_STARTTLS_ENABLE);

			_outboundEmailSessionProperties.put(
				"mail.smtp.starttls.required",
				PropertiesValues.MAIL_SMTP_STARTTLS_REQUIRED);

			_outboundEmailSessionProperties.put(
				"mail.smtp.ssl.protocols",
				PropertiesValues.MAIL_SMTP_SSL_PROTOCOLS);

			_outboundEmailSessionProperties.put(
				"mail.smtp.socketFactory.class",
				PropertiesValues.MAIL_SMTP_SOCKETFACTORY_CLASS);
		}

		return Session.getInstance(
			_outboundEmailSessionProperties,
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

		message.setFrom(new InternetAddress(emailAddress));

		message.addRecipient(
			Message.RecipientType.TO,
			new InternetAddress(PropertiesValues.OUTBOUND_EMAIL_ADDRESS));

		message.setSubject("You Have A New Message From " + emailAddress);
		message.setText(messageBody);

		return message;
	}

	private Message _populateEmailMessage(
			Map<SearchQuery, List<SearchResult>> searchQueryResultMap,
			String recipientEmailAddress, Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(
			new InternetAddress(
				PropertiesValues.OUTBOUND_EMAIL_ADDRESS, "Auction Alert"));

		message.addRecipient(
			Message.RecipientType.TO,
			new InternetAddress(recipientEmailAddress));

		message.setSubject("New Search Results - " + MailUtil.getCurrentDate());

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("emailAddress", recipientEmailAddress);
		rootMap.put("searchQueryResultMap", searchQueryResultMap);
		rootMap.put("numberTool", new NumberTool());
		rootMap.put("rootDomainName", PropertiesValues.ROOT_DOMAIN_NAME);

		String messageBody = VelocityEngineUtils.mergeTemplateIntoString(
			_velocityEngine, "template/email_body.vm", "UTF-8", rootMap);

		message.setContent(messageBody, "text/html");

		return message;
	}

	private Message _populateMessage(
			String emailAddress, String subject, String template,
			Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(
			new InternetAddress(
				PropertiesValues.OUTBOUND_EMAIL_ADDRESS, "Auction Alert"));

		message.addRecipient(
			Message.RecipientType.TO, new InternetAddress(emailAddress));

		message.setSubject(subject);

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("rootDomainName", PropertiesValues.ROOT_DOMAIN_NAME);

		String messageBody = VelocityEngineUtils.mergeTemplateIntoString(
			_velocityEngine, "template/" + template, "UTF-8", rootMap);

		message.setContent(messageBody, "text/html");

		return message;
	}

	private Message _populatePasswordResetToken(
			String emailAddress, String passwordResetToken, Session session)
		throws Exception {

		Message message = new MimeMessage(session);

		message.setFrom(
			new InternetAddress(
				PropertiesValues.OUTBOUND_EMAIL_ADDRESS, "Auction Alert"));

		message.addRecipient(
			Message.RecipientType.TO, new InternetAddress(emailAddress));

		message.setSubject("Password Reset Token");

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("passwordResetToken", passwordResetToken);
		rootMap.put("rootDomainName", PropertiesValues.ROOT_DOMAIN_NAME);

		String messageBody = VelocityEngineUtils.mergeTemplateIntoString(
			_velocityEngine, "template/password_token.vm", "UTF-8", rootMap);

		message.setContent(messageBody, "text/html");

		return message;
	}

	private static Properties _outboundEmailSessionProperties;

	private static final Logger _log = LoggerFactory.getLogger(
		DefaultMailSender.class);

	@Autowired
	private VelocityEngine _velocityEngine;

}