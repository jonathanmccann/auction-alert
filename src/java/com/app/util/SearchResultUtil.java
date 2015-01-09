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

import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.dao.impl.SearchResultDAOImpl;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchQueryModel;
import com.app.model.SearchResultModel;
import com.app.model.eBaySearchResult;

import java.sql.SQLException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class SearchResultUtil {

	public static List<SearchResultModel> filterSearchResults(
			int searchQueryId, List<SearchResultModel> newSearchResultModels)
		throws SQLException {

		List<SearchResultModel> existingSearchResultModels =
			_searchResultDAOImpl.getSearchQueryResults(searchQueryId);

		newSearchResultModels.removeAll(existingSearchResultModels);

		if (!newSearchResultModels.isEmpty()) {
			_log.info(
				"Found {} new search results for search query ID: {}",
					newSearchResultModels.size(), searchQueryId);

			saveNewResultsAndRemoveOldResults(
				existingSearchResultModels, newSearchResultModels);
		}

		return newSearchResultModels;
	}

	public static void performSearch()
		throws DatabaseConnectionException, SQLException {

		List<SearchQueryModel> searchQueryModels =
			_searchQueryDAOImpl.getSearchQueries();

		_log.info(
			"Getting eBay search results for {} search queries",
			searchQueryModels.size());

		for (SearchQueryModel searchQueryModel : searchQueryModels) {
			List<SearchResultModel> searchResultModels =
				eBaySearchResult.geteBaySearchResults(searchQueryModel);

			searchResultModels = filterSearchResults(
				searchQueryModel.getSearchQueryId(), searchResultModels);

			if (!searchResultModels.isEmpty()) {
				MailUtil.sendSearchResultsToRecipients(
					searchQueryModel, searchResultModels);
			}
		}
	}

	public static void saveNewResultsAndRemoveOldResults(
			List<SearchResultModel> existingSearchResultModels,
			List<SearchResultModel> newSearchResultModels)
		throws SQLException {

		int numberOfSearchResultsToRemove =
			existingSearchResultModels.size() + newSearchResultModels.size() - 5;

		if (numberOfSearchResultsToRemove > 0) {
			for (int i = 0; i < numberOfSearchResultsToRemove; i++) {
				SearchResultModel searchResult = existingSearchResultModels.get(
					i);

				_searchResultDAOImpl.deleteSearchResult(
					searchResult.getSearchResultId());
			}
		}

		for (SearchResultModel searchResultModel : newSearchResultModels) {
			_searchResultDAOImpl.addSearchResult(searchResultModel);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultUtil.class);

	private static final SearchQueryDAOImpl _searchQueryDAOImpl =
		new SearchQueryDAOImpl();
	private static final SearchResultDAOImpl _searchResultDAOImpl =
		new SearchResultDAOImpl();

}