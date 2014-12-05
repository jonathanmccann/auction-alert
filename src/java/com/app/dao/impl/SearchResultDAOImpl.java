package com.app.dao.impl;

import com.app.dao.SearchResultDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchResultModel;
import com.app.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchResultDAOImpl implements SearchResultDAO {

	@Override
	public SearchResultModel getSearchResult(int searchResultId)
		throws SQLException {

		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM SearchResult WHERE searchResultId = ?");

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
					new Date(resultSet.getLong("endingTime")));
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
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Cannot find search query for search result ID: " +
				searchResultId);

			throw new SQLException(exception);
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
	public List<SearchResultModel> getSearchResults() throws SQLException {
		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM SearchResult");

			resultSet = preparedStatement.executeQuery();

			List<SearchResultModel> searchResults =
				new ArrayList<SearchResultModel>();

			while (resultSet.next()) {
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
					new Date(resultSet.getLong("endingTime")));
				searchResult.setAuctionPrice(
					resultSet.getDouble("auctionPrice"));
				searchResult.setFixedPrice(
					resultSet.getDouble("fixedPrice"));

				searchResults.add(searchResult);
			}

			return searchResults;
		}
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Unable to return all search results.");

			throw new SQLException(exception);
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
	public void addSearchResult(SearchResultModel searchResultModel)
		throws SQLException {

		Connection connection = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"INSERT INTO SearchResult(itemId, itemTitle, typeOfAuction, " +
					"itemURL, endingTime, auctionPrice, fixedPrice) " +
						"VALUES(?, ? ,?, ?, ?, ?, ?)");

			preparedStatement.setString(1, searchResultModel.getItemId());
			preparedStatement.setString(2, searchResultModel.getItemTitle());
			preparedStatement.setString(3, searchResultModel.getTypeOfAuction());
			preparedStatement.setString(4, searchResultModel.getItemURL());
			preparedStatement.setLong(
				5, searchResultModel.getEndingTime().getTime());
			preparedStatement.setDouble(6, searchResultModel.getAuctionPrice());
			preparedStatement.setDouble(7, searchResultModel.getFixedPrice());

			preparedStatement.executeUpdate();
		}
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Unable to add search result for item ID: " +
				searchResultModel.getItemId());

			throw new SQLException(exception);
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public void addSearchResult(
			String itemId, String itemTitle, double auctionPrice,
			double fixedPrice, String itemURL,
			Date endingTime, String typeOfAuction)
		throws SQLException {

		Connection connection = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"INSERT INTO SearchResult(itemId, itemTitle, typeOfAuction, " +
					"itemURL, endingTime, auctionPrice, fixedPrice) " +
						"VALUES(?, ? ,?, ?, ?, ?, ?)");

			preparedStatement.setString(1, itemId);
			preparedStatement.setString(2, itemTitle);
			preparedStatement.setString(3, typeOfAuction);
			preparedStatement.setString(4, itemURL);
			preparedStatement.setLong(5, endingTime.getTime());
			preparedStatement.setDouble(6, auctionPrice);
			preparedStatement.setDouble(7, fixedPrice);

			preparedStatement.executeUpdate();
		}
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Unable to add search result for item ID: " + itemId);

			throw new SQLException(exception);
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public void deleteSearchResult(int searchResultId) throws SQLException {
		Connection connection = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"DELETE FROM SearchResult WHERE searchResultId = ?");

			preparedStatement.setInt(1, searchResultId);

			preparedStatement.executeUpdate();
		}
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Unable to delete search result for search result ID: " +
				searchResultId);

			throw new SQLException(exception);
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchResultDAOImpl.class);

}