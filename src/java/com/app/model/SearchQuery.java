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

package com.app.model;

/**
 * @author Jonathan McCann
 */
public class SearchQuery {

	public SearchQuery() {
	}

	public SearchQuery(int searchQueryId, String keywords) {
		_searchQueryId = searchQueryId;
		_keywords = keywords;
	}

	public SearchQuery(
		int searchQueryId, String keywords, String categoryId) {

		_searchQueryId = searchQueryId;
		_keywords = keywords;
		_categoryId = categoryId;
	}

	public String getCategoryId() {
		return _categoryId;
	}

	public String getKeywords() {
		return _keywords;
	}

	public int getSearchQueryId() {
		return _searchQueryId;
	}

	public void setCategoryId(String categoryId) {
		_categoryId = categoryId;
	}

	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public void setSearchQueryId(int searchQueryId) {
		_searchQueryId = searchQueryId;
	}

	private String _categoryId;
	private String _keywords;
	private int _searchQueryId;

}