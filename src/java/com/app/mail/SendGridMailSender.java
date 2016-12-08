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

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;

import java.io.IOException;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.NumberTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * @author Jonathan McCann
 */
public class SendGridMailSender implements MailSender {

	@Override
	public void sendAccountDeletionMessage(String emailAddress) {
		Mail mail = _populateMessage(
			emailAddress, "Account Deletion Successful",
			"account_deletion_email.vm");

		_sendEmail(mail);
	}

	@Override
	public void sendCancellationMessage(String emailAddress) {
		Mail mail = _populateMessage(
			emailAddress, "Cancellation Successful", "cancellation_email.vm");

		_sendEmail(mail);
	}

	@Override
	public void sendCardDetailsMessage(String emailAddress) {
		Mail mail = _populateMessage(
			emailAddress, "Card Details Updated", "card_details_email.vm");

		_sendEmail(mail);
	}

	@Override
	public void sendContactMessage(String emailAddress, String message)
		throws IOException {

		Mail mail = _populateContactMessage(emailAddress, message);

		SendGrid sendgrid = new SendGrid(PropertiesValues.SENDGRID_API_KEY);

		Request request = new Request();

		request.method = Method.POST;
		request.endpoint = "mail/send";
		request.body = mail.build();

		sendgrid.api(request);
	}

	@Override
	public void sendPasswordResetToken(
		String emailAddress, String passwordResetToken) {

		Mail mail = _populatePasswordResetToken(
			emailAddress, passwordResetToken);

		_sendEmail(mail);
	}

	@Override
	public void sendPaymentFailedMessage(String emailAddress) {
		Mail mail = _populateMessage(
			emailAddress, "Payment Failed", "payment_failed_email.vm");

		_sendEmail(mail);
	}

	@Override
	public void sendResubscribeMessage(String emailAddress) {
		Mail mail = _populateMessage(
			emailAddress, "Resubscribe Successful", "resubscribe_email.vm");

		_sendEmail(mail);
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

		Mail mail = _populateEmailMessage(
			searchQueryResultMap, user.getEmailAddress(),
			user.getUnsubscribeToken());

		try {
			_sendEmail(mail);

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
		Mail mail = _populateMessage(
			emailAddress, "Welcome", "welcome_email.vm");

		_sendEmail(mail);
	}

	private Mail _populateContactMessage(String emailAddress, String message) {
		Email emailTo = new Email(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);
		Email emailFrom = new Email(emailAddress);
		String subject = "You Have A New Message From " + emailAddress;
		Content content = new Content("text/html", message);

		return new Mail(emailFrom, subject, emailTo, content);
	}

	private Mail _populateEmailMessage(
		Map<SearchQuery, List<SearchResult>> searchQueryResultMap,
		String recipientEmailAddress, String unsubscribeToken) {

		Email emailTo = new Email(recipientEmailAddress);
		Email emailFrom = new Email(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);

		emailFrom.setName("Auction Alert");

		String subject = "New Search Results - " + MailUtil.getCurrentDate();

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("emailAddress", recipientEmailAddress);
		rootMap.put("searchQueryResultMap", searchQueryResultMap);
		rootMap.put(
			"unsubscribeToken",
			MailUtil.escapeUnsubscribeToken(unsubscribeToken));
		rootMap.put("numberTool", new NumberTool());
		rootMap.put("rootDomainName", PropertiesValues.ROOT_DOMAIN_NAME);

		String message = VelocityEngineUtils.mergeTemplateIntoString(
			_velocityEngine, "template/email_body.vm", "UTF-8", rootMap);

		Content content = new Content("text/html", message);

		return new Mail(emailFrom, subject, emailTo, content);
	}

	private Mail _populateMessage(
		String emailAddress, String subject, String template) {

		Email emailTo = new Email(emailAddress);
		Email emailFrom = new Email(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);

		emailFrom.setName("Auction Alert");

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("rootDomainName", PropertiesValues.ROOT_DOMAIN_NAME);

		String messageBody = VelocityEngineUtils.mergeTemplateIntoString(
			_velocityEngine, "template/" + template, "UTF-8", rootMap);

		Content content = new Content("text/html", messageBody);

		return new Mail(emailFrom, subject, emailTo, content);
	}

	private Mail _populatePasswordResetToken(
		String emailAddress, String passwordResetToken) {

		Email emailTo = new Email(emailAddress);
		Email emailFrom = new Email(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);

		emailFrom.setName("Auction Alert");

		String subject = "Password Reset Token";

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("passwordResetToken", passwordResetToken);
		rootMap.put("rootDomainName", PropertiesValues.ROOT_DOMAIN_NAME);

		String messageBody = VelocityEngineUtils.mergeTemplateIntoString(
			_velocityEngine, "template/password_token.vm", "UTF-8", rootMap);

		Content content = new Content("text/html", messageBody);

		return new Mail(emailFrom, subject, emailTo, content);
	}

	private void _sendEmail(Mail mail) {
		try {
			SendGrid sendgrid = new SendGrid(PropertiesValues.SENDGRID_API_KEY);

			Request request = new Request();

			request.method = Method.POST;
			request.endpoint = "mail/send";
			request.body = mail.build();

			sendgrid.api(request);
		}
		catch (Exception e) {
			_log.error("Unable to send email", e);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SendGridMailSender.class);

	@Autowired
	private VelocityEngine _velocityEngine;

}