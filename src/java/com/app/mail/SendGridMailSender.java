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

import com.sendgrid.SendGrid;

import freemarker.template.Template;

import java.io.StringWriter;

import java.sql.SQLException;

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

		User user = UserUtil.getUserByUserId(userId);

		if (!user.isEmailNotification()) {
			return;
		}

		_log.info(
			"Sending search results for {} queries for userId: {}",
			searchQueryResultMap.size(), userId);

		try {
			for (Map.Entry<SearchQuery, List<SearchResult>> mapEntry :
					searchQueryResultMap.entrySet()) {

				SendGrid sendgrid = new SendGrid(
					PropertiesValues.SENDGRID_API_KEY);

				SendGrid.Email email = _populateEmailMessage(
					mapEntry.getKey(), mapEntry.getValue(),
					user.getEmailAddress(), user.getUnsubscribeToken());

				sendgrid.send(email);
			}
		}
		catch (Exception e) {
			_log.error("Unable to send search results to userId: {}", userId, e);
		}
	}

	private SendGrid.Email _populateEmailMessage(
			SearchQuery searchQuery, List<SearchResult> searchResults,
			String recipientEmailAddress, String unsubscribeToken)
		throws Exception {

		SendGrid.Email email = new SendGrid.Email();

		email.addTo(recipientEmailAddress);
		email.setFrom(PropertiesValues.OUTBOUND_EMAIL_ADDRESS);
		email.setSubject(
			"New Search Results - " + MailUtil.getCurrentDate());

		Template template = MailUtil.getEmailTemplate();

		Map<String, Object> rootMap = new HashMap<>();

		rootMap.put("emailAddress", recipientEmailAddress);
		rootMap.put("searchQuery", searchQuery);
		rootMap.put("searchResults", searchResults);
		rootMap.put("unsubscribeToken", unsubscribeToken);

		StringWriter stringWriter = new StringWriter();

		template.process(rootMap, stringWriter);

		email.setText(stringWriter.toString());

		return email;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SendGridMailSender.class);

}