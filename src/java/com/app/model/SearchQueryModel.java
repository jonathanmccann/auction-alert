package com.app.model;

import java.util.Date;

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

	public int getSearchQueryId() {
		return _searchQueryId;
	}

	public String getSearchQuery() {
		return _searchQuery;
	}

	public void setSearchQueryId(int searchQueryId) {
		_searchQueryId = searchQueryId;
	}

	public void setSearchQuery(String _searchQuery) {
		_searchQuery = _searchQuery;
	}

	private int _searchQueryId;
	private String _searchQuery;

}