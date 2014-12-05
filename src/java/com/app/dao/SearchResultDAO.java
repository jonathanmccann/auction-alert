package com.app.dao;

import com.app.model.SearchResultModel;

import java.util.Date;
import java.util.List;

public interface SearchResultDAO {

	public int getItemId(int searchResultId);

	public String getItemTitle(int searchResultId);

	public String getTypeOfAuction(int searchResultId);

	public String getURL(int searchResultId);

	public Date getEndingTime(int searchResultId);

	public String getAuctionPrice(int searchResultId);

	public String getFixedPrice(int searchResultId);

	public SearchResultModel getSearchResult(int searchResultId);

	public List<SearchResultModel> getSearchResults();

	public void addSearchQueryResult(SearchResultModel searchResultModel);

	public void updateSearchQuery(
		int searchResultId, SearchResultModel searchResultModel);

	public void deleteSearchQuery(int searchResultId);

}