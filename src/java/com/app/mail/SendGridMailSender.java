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
	public void sendContactMessage(String emailAddress, String message)
		throws Exception {

		Mail mail = _populateContactMessage(emailAddress, message);

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

		_log.info(
			"Sending search results for {} queries for userId: {}",
			searchQueryResultMap.size(), userId);

		try {
			Mail mail = _populateEmailMessage(
				searchQueryResultMap, user.getEmailAddress(),
				user.getUnsubscribeToken());

			_sendEmail(mail);

			emailsSent++;
		}
		catch (Exception e) {
			_log.error("Unable to send search results to userId: {}", userId, e);
		}

		UserUtil.updateEmailsSent(user.getUserId(), emailsSent);
	}

	private Mail _populateContactMessage(
			String emailAddress, String message)
		throws Exception {

		Email emailTo = new Email(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);
		Email emailFrom = new Email(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);
		String subject = "You Have A New Message From " + emailAddress;
		Content content = new Content("text/html", message);

		return new Mail(emailFrom, subject, emailTo, content);
	}

	private Mail _populateEmailMessage(
			Map<SearchQuery, List<SearchResult>> searchQueryResultMap,
			String recipientEmailAddress, String unsubscribeToken)
		throws Exception {

		Email emailTo = new Email(recipientEmailAddress);
		Email emailFrom = new Email(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);
		String subject = "New Search Results - " + MailUtil.getCurrentDate();

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("emailAddress", recipientEmailAddress);
		rootMap.put("searchQueryResultMap", searchQueryResultMap);
		rootMap.put(
			"unsubscribeToken",
			MailUtil.escapeUnsubscribeToken(unsubscribeToken));
		rootMap.put("numberTool", new NumberTool());

		String message = VelocityEngineUtils.mergeTemplateIntoString(
			velocityEngine, "template/email_body.vm", "UTF-8", rootMap);

		Content content = new Content("text/html", message);

		return new Mail(emailFrom, subject, emailTo, content);
	}

	private void _sendEmail(Mail mail) throws Exception {
		SendGrid sendgrid = new SendGrid(PropertiesValues.SENDGRID_API_KEY);

		Request request = new Request();

		request.method = Method.POST;
		request.endpoint = "mail/send";
		request.body = mail.build();

		sendgrid.api(request);
	}

	@Autowired
	private VelocityEngine velocityEngine;

	private static final Logger _log = LoggerFactory.getLogger(
		SendGridMailSender.class);

}