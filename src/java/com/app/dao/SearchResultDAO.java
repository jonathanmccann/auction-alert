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

package com.app.dao;

import com.app.model.SearchResultModel;

import java.util.List;

/**
 * @author Jonathan McCann
 */
public interface SearchResultDAO {

	public void addSearchResult(SearchResultModel searchResultModel)
		throws Exception;

	public void deleteSearchQueryResults(int searchQueryId) throws Exception;

	public void deleteSearchResult(int searchResultId) throws Exception;

	public List<SearchResultModel> getSearchQueryResults(int searchQueryId)
		throws Exception;

	public SearchResultModel getSearchResult(int searchResultId)
		throws Exception;

	public List<SearchResultModel> getSearchResults() throws Exception;

}