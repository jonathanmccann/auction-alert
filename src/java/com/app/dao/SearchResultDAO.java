package com.app.dao;

import com.app.model.SearchResultModel;

import java.util.Date;
import java.util.List;

public interface SearchResultDAO {

	public SearchResultModel getSearchResult(int searchResultId) throws Exception;

	public List<SearchResultModel> getSearchResults();

	public void addSearchQueryResult(SearchResultModel searchResultModel);

	public void updateSearchQuery(
		int searchResultId, SearchResultModel searchResultModel);

	public void deleteSearchQuery(int searchResultId);

}