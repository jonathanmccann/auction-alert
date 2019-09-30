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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class SearchResultUtil {

	public static void addSearchResults(
			int searchQueryId, List<SearchResult> searchResults)
		throws DatabaseConnectionException, SQLException {

		_searchResultDAO.addSearchResults(searchQueryId, searchResults);
	}

	public static void applyUndeliveredSearchResults(
			int userId,
			Map<SearchQuery, List<SearchResult>> searchQueryResultMap)
		throws DatabaseConnectionException, SQLException {

		List<SearchResult> undeliveredSearchResults =
			getUndeliveredSearchResults(userId);

		if (undeliveredSearchResults.size() == 0) {
			return;
		}

		for (SearchResult undeliveredSearchResult : undeliveredSearchResults) {
			SearchQuery searchQuery = SearchQueryUtil.getSearchQuery(
				undeliveredSearchResult.getSearchQueryId());

			List<SearchResult> searchResults = searchQueryResultMap.get(
				searchQuery);

			if (searchResults == null) {
				searchResults = new ArrayList<>();

				searchResults.add(undeliveredSearchResult);

				searchQueryResultMap.put(searchQuery, searchResults);
			}
			else {
				searchResults.add(undeliveredSearchResult);
			}
		}
	}

	public static void deleteSearchQueryResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchResultDAO.deleteSearchQueryResults(searchQueryId);
	}

	public static void deleteSearchResults(
			int searchQueryId, int numberOfSearchResultsToRemove)
		throws DatabaseConnectionException, SQLException {

		_searchResultDAO.deleteSearchResults(
			searchQueryId, numberOfSearchResultsToRemove);
	}

	public static List<SearchResult> filterSearchResults(
			SearchQuery searchQuery, List<SearchResult> newSearchResults)
		throws DatabaseConnectionException, SQLException {

		List<SearchResult> existingSearchResults = getSearchQueryResults(
			searchQuery.getSearchQueryId());

		newSearchResults = _removePreviouslyNotifiedResults(
			existingSearchResults, newSearchResults);

		if (!newSearchResults.isEmpty()) {
			_log.debug(
				"Found {} new search results for keywords: {}",
				newSearchResults.size(), searchQuery.getKeywords());

			int searchQueryId = searchQuery.getSearchQueryId();

			_deleteOldResults(
				searchQueryId, existingSearchResults.size(),
				newSearchResults.size());

			addSearchResults(searchQueryId, newSearchResults);
		}

		return newSearchResults;
	}

	public static List<SearchResult> getSearchQueryResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		return _searchResultDAO.getSearchQueryResults(searchQueryId);
	}

	public static List<SearchResult> getUndeliveredSearchResults(int userId)
		throws DatabaseConnectionException, SQLException {

		return _searchResultDAO.getUndeliveredSearchResults(userId);
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

		_log.debug(
			"Performing searches for {} users took {} milliseconds",
			userIds.size(), (endTime - startTime) / 1000000);
	}

	@Autowired
	public void setSearchResultDAO(SearchResultDAO searchResultDAO) {
		_searchResultDAO = searchResultDAO;
	}

	public static void updateSearchResultsDeliveredStatus(
			List<Integer> searchResultIds, boolean delivered)
		throws DatabaseConnectionException, SQLException {

		_searchResultDAO.updateSearchResultsDeliveredStatus(
			searchResultIds, delivered);
	}

	private static void _deleteOldResults(
			int searchQueryId, int existingSearchResultsSize,
			int newSearchResultsSize)
		throws DatabaseConnectionException, SQLException {

		int numberOfSearchResultsToRemove =
			existingSearchResultsSize + newSearchResultsSize -
				PropertiesValues.MAXIMUM_NUMBER_OF_SEARCH_RESULTS;

		if (numberOfSearchResultsToRemove > 0) {
			deleteSearchResults(searchQueryId, numberOfSearchResultsToRemove);
		}
	}

	private static List<SearchResult> _removePreviouslyNotifiedResults(
			List<SearchResult> existingSearchResults,
			List<SearchResult> newSearchResults)
		throws DatabaseConnectionException, SQLException {

		return newSearchResults.stream()
			.filter(searchResult -> !existingSearchResults.stream()
				.map(SearchResult::getItemId)
				.collect(Collectors.toSet())
				.contains(searchResult.getItemId()))
			.collect(Collectors.toList());
	}

	private static final int _THREAD_POOL_SIZE = 2;

	private static final long _THREAD_TIMEOUT_SECONDS = 15;

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultUtil.class);

	private static SearchResultDAO _searchResultDAO;

}