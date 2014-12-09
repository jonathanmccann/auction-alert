package com.app.dao;

import com.app.model.SearchResultModel;

import java.util.Date;
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