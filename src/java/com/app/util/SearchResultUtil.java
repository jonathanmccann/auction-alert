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

import com.app.dao.SearchResultDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.runnable.SearchResultRunnable;

import java.sql.SQLException;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class SearchResultUtil {

	public static int addSearchResult(SearchResult searchResult)
		throws DatabaseConnectionException, SQLException {

		return _searchResultDAO.addSearchResult(searchResult);
	}

	public static void deleteSearchQueryResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchResultDAO.deleteSearchQueryResults(searchQueryId);
	}

	public static void deleteSearchResult(int searchResultId)
		throws DatabaseConnectionException, SQLException {

		_searchResultDAO.deleteSearchResult(searchResultId);
	}

	public static List<SearchResult> filterSearchResults(
			SearchQuery searchQuery, List<SearchResult> newSearchResults)
		throws DatabaseConnectionException, SQLException {

		_removePreviouslyNotifiedResults(
			searchQuery.getSearchQueryId(), newSearchResults);

		if (!newSearchResults.isEmpty()) {
			_log.debug(
				"Found {} new search results for keywords: {}",
				newSearchResults.size(), searchQuery.getKeywords());

			List<SearchResult> existingSearchResults = getSearchQueryResults(
				searchQuery.getSearchQueryId());

			_deleteOldResults(existingSearchResults, newSearchResults.size());

			_addNewResults(newSearchResults);
		}

		return newSearchResults;
	}

	public static List<SearchResult> getSearchQueryResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		return _searchResultDAO.getSearchQueryResults(searchQueryId);
	}

	public static void performSearch()
		throws DatabaseConnectionException, SQLException {

		long startTime = System.nanoTime();

		ExecutorService executor = Executors.newFixedThreadPool(
			_THREAD_POOL_SIZE);

		List<Integer> userIds = UserUtil.getUserIds(true);

		for (int userId : userIds) {
			SearchResultRunnable searchResultRunnable =
				new SearchResultRunnable(userId);

			executor.execute(searchResultRunnable);
		}

		executor.shutdown();

		try {
			executor.awaitTermination(
				_THREAD_TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		catch (InterruptedException ie) {
			_log.error("The executor encountered an exception", ie);
		}

		long endTime = System.nanoTime();

		_log.info(
			"Performing searches for {} users took {} milliseconds",
			userIds.size(), (endTime - startTime) / 1000000);
	}

	@Autowired
	public void setSearchQueryPreviousResultDAO(
		SearchResultDAO searchResultDAO) {

		_searchResultDAO = searchResultDAO;
	}

	private static void _addNewResults(List<SearchResult> newSearchResults)
		throws DatabaseConnectionException, SQLException {

		for (SearchResult searchResult : newSearchResults) {
			addSearchResult(searchResult);

			int searchQueryPreviousResultsCount =
				SearchQueryPreviousResultUtil.
					getSearchQueryPreviousResultsCount(
						searchResult.getSearchQueryId());

			if (searchQueryPreviousResultsCount ==
					PropertiesValues.
						TOTAL_NUMBER_OF_PREVIOUS_SEARCH_RESULT_IDS) {

				SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResult(
					searchResult.getSearchQueryId());
			}

			SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
				searchResult.getSearchQueryId(), searchResult.getItemId());
		}
	}

	private static void _deleteOldResults(
			List<SearchResult> existingSearchResults, int newSearchResultsSize)
		throws DatabaseConnectionException, SQLException {

		int numberOfSearchResultsToRemove =
			existingSearchResults.size() + newSearchResultsSize - 5;

		if (numberOfSearchResultsToRemove > 0) {
			for (int i = 0; i < numberOfSearchResultsToRemove; i++) {
				SearchResult searchResult = existingSearchResults.get(i);

				deleteSearchResult(searchResult.getSearchResultId());
			}
		}
	}

	private static void _removePreviouslyNotifiedResults(
			int searchQueryId, List<SearchResult> newSearchResults)
		throws DatabaseConnectionException, SQLException {

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(
				searchQueryId);

		if (!searchQueryPreviousResults.isEmpty()) {
			Iterator iterator = newSearchResults.iterator();

			while (iterator.hasNext()) {
				SearchResult searchResult = (SearchResult)iterator.next();

				if (searchQueryPreviousResults.contains(
						searchResult.getItemId())) {

					iterator.remove();
				}
			}
		}
	}

	private static final int _THREAD_POOL_SIZE =
		Runtime.getRuntime().availableProcessors() + 1;

	private static final long _THREAD_TIMEOUT_SECONDS = 15;

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultUtil.class);

	private static SearchResultDAO _searchResultDAO;

}