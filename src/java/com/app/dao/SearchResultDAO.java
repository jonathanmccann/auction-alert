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

	public void addSearchResult(
		int searchQueryId, String itemId, String itemTitle, double auctionPrice,
		double fixedPrice, String itemURL, Date endingTime,
		String typeOfAuction) throws Exception;

	public void deleteSearchResult(int searchResultId) throws Exception;

	public SearchResultModel getSearchResult(int searchResultId)
		throws Exception;

	public List<SearchResultModel> getSearchResults() throws Exception;

}