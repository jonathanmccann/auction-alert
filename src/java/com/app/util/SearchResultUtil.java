package com.app.util;

import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.dao.impl.SearchResultDAOImpl;
import com.app.model.SearchQueryModel;
import com.app.model.SearchResultModel;
import com.app.model.eBaySearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class SearchResultUtil {

	public static List<SearchResultModel> filterSearchResults(
			int searchQueryId, List<SearchResultModel> newSearchResultModels)
		throws SQLException {

		List<SearchResultModel> existingSearchResultModels =
			_searchResultDAOImpl.getSearchQueryResults(searchQueryId);

		newSearchResultModels.removeAll(existingSearchResultModels);

		if (!newSearchResultModels.isEmpty()) {
			saveNewResultsAndRemoveOldResults(
				existingSearchResultModels, newSearchResultModels);
		}

		_log.info(
			"Found {} search results for search query ID: {}",
				newSearchResultModels.size(), searchQueryId);

		return newSearchResultModels;
	}

	public static List<SearchResultModel> performeBaySearch(
		String searchQuery) {

		return eBaySearchResult.geteBaySearchResults(searchQuery);
	}

	public static void performSearch() throws SQLException {
		List<SearchQueryModel> searchQueryModels =
			_searchQueryDAOImpl.getSearchQueries();

		_log.info(
			"Getting eBay search results for {} search queries",
			searchQueryModels.size());

		for (SearchQueryModel searchQueryModel : searchQueryModels) {
			List<SearchResultModel> searchResults =
				performeBaySearch(searchQueryModel.getSearchQuery());

			searchResults = filterSearchResults(
				searchQueryModel.getSearchQueryId(), searchResults);

			if (!searchResults.isEmpty()) {
				textSearchResults(searchResults);
			}
		}
	}

	public static void saveNewResultsAndRemoveOldResults(
			List<SearchResultModel> existingSearchResultModels,
			List<SearchResultModel> newSearchResultModels)
		throws SQLException{

		int numberOfSearchResultsToRemove =
			existingSearchResultModels.size() + newSearchResultModels.size() - 5;

		if (numberOfSearchResultsToRemove > 0) {
			for (int i = 0; i < numberOfSearchResultsToRemove; i++) {
				SearchResultModel searchResult =
					existingSearchResultModels.get(i);

				_searchResultDAOImpl.deleteSearchResult(
					searchResult.getSearchResultId());
			}
		}

		for (SearchResultModel searchResultModel : newSearchResultModels) {
			_searchResultDAOImpl.addSearchResult(searchResultModel);
		}
	}

	private static void textSearchResults(
		List<SearchResultModel> searchResultModels) {

		// Text search results to appropriate phone number
		// Text via email - Send email to $NUMBER@txt.att.net

	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultUtil.class);

	private static final SearchQueryDAOImpl _searchQueryDAOImpl =
		new SearchQueryDAOImpl();

	private static final SearchResultDAOImpl _searchResultDAOImpl =
		new SearchResultDAOImpl();

}