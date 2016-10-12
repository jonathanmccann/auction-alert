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

import java.sql.SQLException;

import java.util.List;
import java.util.Map;

/**
 * @author Jonathan McCann
 */
public interface MailSender {

	public void sendCancellationMessage(String emailAddress) throws Exception;

	public void sendCardDetailsMessage(String emailAddress) throws Exception;

	public void sendContactMessage(String emailAddress, String message)
		throws Exception;

	public void sendPasswordResetToken(
			String emailAddress, String passwordResetToken)
		throws Exception;

	public void sendSearchResultsToRecipient(
			int userId,
			Map<SearchQuery, List<SearchResult>> searchQueryResultMap)
		throws DatabaseConnectionException, SQLException;

	public void sendWelcomeMessage(String emailAddress) throws Exception;

}