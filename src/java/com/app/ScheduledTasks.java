package com.app;

import com.app.dao.impl.SearchQueryDAOImpl;
import com.app.dao.impl.SearchResultDAOImpl;
import com.app.model.SearchQueryModel;
import com.app.model.SearchResultModel;
import com.app.model.eBaySearchResultModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Jonathan McCann
 */
@EnableScheduling
public class ScheduledTasks {

	@Scheduled(fixedRate = 300000)
	public static void main() {
		try {
			List<SearchQueryModel> searchQueries =
				_searchQueryDAOImpl.getSearchQueries();

			_log.info(
				"Getting eBay search results for {} search queries",
					searchQueries.size());

			for (SearchQueryModel searchQueryModel : searchQueries) {
				List<SearchResultModel> searchResults =
					performSearch(searchQueryModel.getSearchQuery());

				searchResults = filterSearchResults(
					searchQueryModel.getSearchQueryId(), searchResults);

				if (searchResults.size() > 0) {
					textSearchResults(searchResults);
				}
			}
		}
		catch (SQLException sqle) {
			_log.error("Unable to get all of the search queries");
		}
	}

	private static List<SearchResultModel> filterSearchResults(
			int searchQueryId, List<SearchResultModel> newSearchResultModels)
		throws SQLException {

		List<SearchResultModel> existingSearchResultModels =
			_searchResultDAOImpl.getSearchQueryResults(searchQueryId);

		newSearchResultModels.removeAll(existingSearchResultModels);

		if (newSearchResultModels.size() > 0) {
			saveNewResultsAndRemoveOldResults(
				existingSearchResultModels, newSearchResultModels);
		}

		_log.info(
			"Found {} search results for search query ID: {}",
				newSearchResultModels.size(), searchQueryId);

		return newSearchResultModels;
	}

	private static List<SearchResultModel> performSearch(
		String searchQuery) {

		return eBaySearchResultModel.geteBaySearchResults(searchQuery);
	}

	private static void saveNewResultsAndRemoveOldResults(
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
		ScheduledTasks.class);

	private static SearchQueryDAOImpl _searchQueryDAOImpl =
		new SearchQueryDAOImpl();

	private static SearchResultDAOImpl _searchResultDAOImpl =
		new SearchResultDAOImpl();

}