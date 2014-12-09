package com.app.model;

/**
 * @author Jonathan McCann
 */
public class SearchQueryModel {

	public SearchQueryModel() {
	}

	public SearchQueryModel(int searchQueryId, String searchQuery) {
		_searchQueryId = searchQueryId;
		_searchQuery = searchQuery;
	}

	public String getSearchQuery() {
		return _searchQuery;
	}

	public int getSearchQueryId() {
		return _searchQueryId;
	}

	public void setSearchQuery(String searchQuery) {
		_searchQuery = searchQuery;
	}

	public void setSearchQueryId(int searchQueryId) {
		_searchQueryId = searchQueryId;
	}

	private String _searchQuery;
	private int _searchQueryId;

}