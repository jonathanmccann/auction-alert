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
import com.app.model.SearchResultModel;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class SearchResultUtil {

	public static void addSearchResult(SearchResultModel searchResultModel)
		throws DatabaseConnectionException, SQLException {

		_searchResultDAO.addSearchResult(searchResultModel);
	}

	public static void deleteSearchQueryResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchResultDAO.deleteSearchQueryResults(searchQueryId);
	}

	public static void deleteSearchResult(int searchResultId)
		throws DatabaseConnectionException, SQLException {

		_searchResultDAO.deleteSearchResult(searchResultId);
	}

	public static List<SearchResultModel> getSearchQueryResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		return _searchResultDAO.getSearchQueryResults(searchQueryId);
	}

	public static SearchResultModel getSearchResult(int searchResultId)
		throws DatabaseConnectionException, SQLException {

		return _searchResultDAO.getSearchResult(searchResultId);
	}

	public static List<SearchResultModel> getSearchResults()
		throws DatabaseConnectionException, SQLException {

		return _searchResultDAO.getSearchResults();
	}
	public static void performSearch()
		throws DatabaseConnectionException, SQLException {

		List<SearchQuery> searchQueries =
			SearchQueryUtil.getSearchQueries();

		if (searchQueries.size() == 0) {
			_log.info("There are no search queries");

			return;
		}

		_log.info(
			"Getting eBay search results for {} search queries",
			searchQueries.size());

		Map<SearchQuery, List<SearchResultModel>> searchQueryResultMap =
			new HashMap<>();

		for (SearchQuery searchQuery : searchQueries) {
			List<SearchResultModel> searchResultModels =
				eBaySearchResultUtil.geteBaySearchResults(searchQuery);

			searchResultModels = _filterSearchResults(
				searchQuery, searchResultModels);

			if (!searchResultModels.isEmpty()) {
				searchQueryResultMap.put(searchQuery, searchResultModels);
			}
		}

		if (!searchQueryResultMap.isEmpty()) {
			MailUtil.sendSearchResultsToRecipients(searchQueryResultMap);
		}
	}

	private static List<SearchResultModel> _filterSearchResults(
			SearchQuery searchQuery,
			List<SearchResultModel> newSearchResultModels)
		throws DatabaseConnectionException, SQLException {

		List<String> searchQueryPreviousResults =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResults(
				searchQuery.getSearchQueryId());

		Iterator iterator = newSearchResultModels.iterator();

		while (iterator.hasNext()) {
			SearchResultModel searchResultModel =
				(SearchResultModel)iterator.next();

			if (searchQueryPreviousResults.contains(
					searchResultModel.getItemId())) {

				iterator.remove();
			}
		}

		if (!newSearchResultModels.isEmpty()) {
			_log.debug(
				"Found {} new search results for keywords: {}",
				newSearchResultModels.size(),
				searchQuery.getKeywords());

			List<SearchResultModel> existingSearchResultModels =
				getSearchQueryResults(
					searchQuery.getSearchQueryId());

			_saveNewResultsAndRemoveOldResults(
				existingSearchResultModels, newSearchResultModels);
		}

		return newSearchResultModels;
	}

	private static void _saveNewResultsAndRemoveOldResults(
			List<SearchResultModel> existingSearchResultModels,
			List<SearchResultModel> newSearchResultModels)
		throws DatabaseConnectionException, SQLException {

		int numberOfSearchResultsToRemove =
			existingSearchResultModels.size() + newSearchResultModels.size() - 5;

		if (numberOfSearchResultsToRemove > 0) {
			for (int i = 0; i < numberOfSearchResultsToRemove; i++) {
				SearchResultModel searchResult = existingSearchResultModels.get(
					i);

				deleteSearchResult(
					searchResult.getSearchResultId());
			}
		}

		for (SearchResultModel searchResultModel : newSearchResultModels) {
			addSearchResult(searchResultModel);

			int searchQueryPreviousResultsCount =
				SearchQueryPreviousResultUtil.
					getSearchQueryPreviousResultsCount(
						searchResultModel.getSearchQueryId());

			if (searchQueryPreviousResultsCount ==
					PropertiesValues.
						TOTAL_NUMBER_OF_PREVIOUS_SEARCH_RESULT_IDS) {

				SearchQueryPreviousResultUtil.
					deleteSearchQueryPreviousResult(
						searchResultModel.getSearchQueryId());
			}

			SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
				searchResultModel.getSearchQueryId(),
				searchResultModel.getItemId());
		}
	}

	@Autowired
	public void setSearchQueryPreviousResultDAO(
		SearchResultDAO searchResultDAO) {

		_searchResultDAO = searchResultDAO;
	}

	private static SearchResultDAO _searchResultDAO;

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultUtil.class);

}