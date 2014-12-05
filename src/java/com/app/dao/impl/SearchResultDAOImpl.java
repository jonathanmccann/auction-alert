package com.app.dao.impl;

import com.app.dao.SearchResultDAO;
import com.app.model.SearchResultModel;

import java.util.Date;
import java.util.List;

public class SearchResultDAOImpl implements SearchResultDAO {

	@Override
	public int getItemId(int searchResultId) {
		return 0;
	}

	@Override
	public String getItemTitle(int searchResultId) {
		return null;
	}

	@Override
	public String getTypeOfAuction(int searchResultId) {
		return null;
	}

	@Override
	public String getURL(int searchResultId) {
		return null;
	}

	@Override
	public Date getEndingTime(int searchResultId) {
		return null;
	}

	@Override
	public String getAuctionPrice(int searchResultId) {
		return null;
	}

	@Override
	public String getFixedPrice(int searchResultId) {
		return null;
	}

	@Override
	public SearchResultModel getSearchResult(int searchResultId) {
		return null;
	}

	@Override
	public List<SearchResultModel> getSearchResults() {
		return null;
	}

	@Override
	public void addSearchQueryResult(SearchResultModel searchResultModel) {

	}

	@Override
	public void updateSearchQuery(int searchResultId, SearchResultModel searchResultModel) {

	}

	@Override
	public void deleteSearchQuery(int searchResultId) {

	}

}