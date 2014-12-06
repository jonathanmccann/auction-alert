package com.app.dao;

import com.app.model.SearchQueryModel;

import java.util.List;

/**
 * @author Jonathan McCann
 */
public interface SearchQueryDAO {

	public void addSearchQuery(String searchQuery) throws Exception;

	public void deleteSearchQuery(int searchQueryId) throws Exception;

	public List<SearchQueryModel> getSearchQueries() throws Exception;

	public String getSearchQuery(int searchQueryId) throws Exception;

	public void updateSearchQuery(int searchQueryId, String searchQuery)
		throws Exception;

}