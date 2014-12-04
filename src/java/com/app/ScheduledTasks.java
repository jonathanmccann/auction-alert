package com.app;

import com.app.model.SearchResultModel;
import com.app.model.eBaySearchResultModel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Jonathan McCann
 */
@EnableScheduling
public class ScheduledTasks {

	@Scheduled(fixedRate = 300000)
	public static void main() {

		// Get search queries
		// Call performSearch on queries
		// Call filterSearchResults to remove already seen results
		// Call textSearchResults to deliver new results

		List<String> searchQueries = new ArrayList<String>();

		searchQueries.add("test search query");

		performSearch(searchQueries);
	}

	private static List<SearchResultModel> performSearch(
		List<String> searchQueries) {

		return eBaySearchResultModel.geteBaySearchResults(searchQueries);
	}

	private List<SearchResultModel> filterSearchResults(
		List<SearchResultModel> searchResultModels) {

		// Get last five results from database
		// Remove all from passed in list
		// Return list

		return searchResultModels;
	}

	private void textSearchResults(List<SearchResultModel> searchResultModels) {

		// Text search results to appropriate phone number

	}

}