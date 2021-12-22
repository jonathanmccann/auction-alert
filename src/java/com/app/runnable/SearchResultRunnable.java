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

import com.app.mail.MailSender;
import com.app.mail.MailSenderFactory;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.util.EbaySearchResultUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;

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

			if (searchQueries.isEmpty()) {
				_log.debug(
					"There are no search queries for userId: {}", _userId);

				return;
			}

			_log.debug(
				"Getting eBay search results for {} search queries",
				searchQueries.size());

			Map<SearchQuery, List<SearchResult>> searchQueryResultMap =
				new HashMap<>();

			for (SearchQuery searchQuery : searchQueries) {
				List<SearchResult> searchResults =
					EbaySearchResultUtil.getEbaySearchResults(searchQuery);

				searchResults = SearchResultUtil.filterSearchResults(
					searchQuery, searchResults);

				if (!searchResults.isEmpty()) {
					searchQueryResultMap.put(searchQuery, searchResults);
				}
			}

			if (!searchQueryResultMap.isEmpty()) {
				//SearchResultUtil.applyUndeliveredSearchResults(
					//_userId, searchQueryResultMap);

				MailSender mailSender = MailSenderFactory.getInstance();

				mailSender.sendSearchResultsToRecipient(
					_userId, searchQueryResultMap);
			}

			long endTime = System.nanoTime();

			_log.debug(
				"Performing searches for userId: {} with {} queries took {} " +
					"milliseconds",
				_userId, searchQueries.size(), (endTime - startTime) / 1000000);
		}
		catch (Exception e) {
			_log.error("Unable to perform search for userId: " + _userId, e);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultRunnable.class);

	private final int _userId;

}