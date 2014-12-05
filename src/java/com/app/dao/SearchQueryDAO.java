package com.app.dao;

import com.app.model.SearchQueryModel;

import java.util.List;

public interface SearchQueryDAO {

	public String getSearchQuery(int searchQueryId) throws Exception;

	public List<SearchQueryModel> getSearchQueries() throws Exception;

	public void addSearchQuery(String searchQuery) throws Exception;

	public void updateSearchQuery(int searchQueryId, String searchQuery) throws Exception;

	public void deleteSearchQuery(int searchQueryId) throws Exception;

}