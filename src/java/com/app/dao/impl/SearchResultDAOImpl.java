package com.app.dao.impl;

import com.app.dao.SearchResultDAO;
import com.app.model.SearchResultModel;
import com.app.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class SearchResultDAOImpl implements SearchResultDAO {

	@Override
	public SearchResultModel getSearchResult(int searchResultId)
		throws Exception {

		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM SearchQueryResult WHERE searchResultId = ?");

			preparedStatement.setInt(1, searchResultId);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				SearchResultModel searchResult = new SearchResultModel();

				searchResult.setSearchResultId(
					resultSet.getInt("searchResultId"));
				searchResult.setItemId(
					resultSet.getString("itemId"));
				searchResult.setItemTitle(
					resultSet.getString("itemTitle"));
				searchResult.setTypeOfAuction(
					resultSet.getString("typeOfAuction"));
				searchResult.setItemURL(
					resultSet.getString("itemURL"));
				searchResult.setEndingTime(
					resultSet.getDate("endingTime"));
				searchResult.setAuctionPrice(
					resultSet.getDouble("auctionPrice"));
				searchResult.setFixedPrice(
					resultSet.getDouble("fixedPrice"));

				return searchResult;
			}
			else {
				return new SearchResultModel();
			}
		}
		finally {
			if (connection != null) {
				connection.close();
			}

			if (resultSet != null) {
				resultSet.close();
			}
		}
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