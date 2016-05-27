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

package com.app.runnable;

import com.app.exception.DatabaseConnectionException;
import com.app.mail.MailSender;
import com.app.mail.MailSenderFactory;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;
import com.app.util.eBaySearchResultUtil;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class SearchResultRunnable implements Runnable {

	public SearchResultRunnable(int userId) {
		_userId = userId;
	}

	@Override
	public void run() {
		try {
			long startTime = System.nanoTime();

			List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
				_userId, true);

			if (searchQueries.size() == 0) {
				_log.info(
					"There are no search queries for userId: {}", _userId);

				return;
			}

			_log.info(
				"Getting eBay search results for {} search queries",
				searchQueries.size());

			Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
				new HashMap<>();

			for (SearchQuery searchQuery : searchQueries) {
				List<SearchResult> searchResults =
					eBaySearchResultUtil.geteBaySearchResults(searchQuery);

				searchResults = SearchResultUtil.filterSearchResults(
					searchQuery, searchResults);

				if (!searchResults.isEmpty()) {
					searchQueryResultMap.put(searchQuery, searchResults);
				}
			}

			if (!searchQueryResultMap.isEmpty()) {
				MailSender mailSender = MailSenderFactory.getInstance();

				mailSender.sendSearchResultsToRecipient(
					_userId, searchQueryResultMap);
			}

			long endTime = System.nanoTime();

			_log.debug(
				"Performing searches for userId: {} took {} seconds", _userId,
				(endTime - startTime) / 1000000000);
		}
		catch (DatabaseConnectionException | SQLException e) {
			_log.error("Unable to perform search for userId: {}", _userId, e);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultRunnable.class);

	private final int _userId;

}