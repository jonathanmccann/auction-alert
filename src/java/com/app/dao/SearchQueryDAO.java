package com.app.dao;

import java.util.List;

public interface SearchQueryDAO {

	public String getSearchQuery(int searchQueryID);

	public List<String> getSearchQueries();

	public void addSearchQuery(String searchQuery);

	public void updateSearchQuery(int searchQueryID, String searchQuery);

	public void deleteSearchQuery(int searchQueryID);

}