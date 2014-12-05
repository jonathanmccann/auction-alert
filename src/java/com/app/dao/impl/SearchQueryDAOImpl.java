package com.app.dao.impl;

import com.app.dao.SearchQueryDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.SearchQueryModel;
import com.app.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchQueryDAOImpl implements SearchQueryDAO {

	@Override
	public String getSearchQuery(int searchQueryId) throws SQLException {
		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT searchQuery FROM SearchQuery WHERE searchQueryId = ?");

			preparedStatement.setInt(1, searchQueryId);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getString("searchQuery");
			}
			else {
				throw new SQLException();
			}
		}
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Cannot find search query for search query ID: " +
				searchQueryId);

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
	public List<SearchQueryModel> getSearchQueries() throws SQLException {
		Connection connection = null;
		ResultSet resultSet = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM SearchQuery");

			resultSet = preparedStatement.executeQuery();

			List<SearchQueryModel> searchQueryModels =
				new ArrayList<SearchQueryModel>();

			while (resultSet.next()) {
				int searchQueryId = resultSet.getInt("searchQueryId");

				String searchQuery = resultSet.getString("searchQuery");

				SearchQueryModel searchQueryModel = new SearchQueryModel(
					searchQueryId, searchQuery);

				searchQueryModels.add(searchQueryModel);
			}

			return searchQueryModels;
		}
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Unable to return all search queries.");

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
	public void addSearchQuery(String searchQuery) throws SQLException {
		Connection connection = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"INSERT INTO SearchQuery(searchQuery) VALUES(?)");

			preparedStatement.setString(1, searchQuery);

			preparedStatement.executeUpdate();
		}
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Unable to add search query: " + searchQuery);

			throw new SQLException(exception);
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public void updateSearchQuery(int searchQueryId, String searchQuery)
		throws SQLException {

		Connection connection = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"UPDATE SearchQuery SET searchQuery = ? WHERE " +
					"searchQueryId = ?");

			preparedStatement.setString(1, searchQuery);
			preparedStatement.setInt(2, searchQueryId);

			preparedStatement.executeUpdate();
		}
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Unable to update search query: " + searchQuery +
				" for search query ID: " + searchQueryId);

			throw new SQLException(exception);
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public void deleteSearchQuery(int searchQueryId) throws SQLException {
		Connection connection = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"DELETE FROM SearchQuery WHERE searchQueryId = ?");

			preparedStatement.setInt(1, searchQueryId);

			preparedStatement.executeUpdate();
		}
		catch (DatabaseConnectionException | SQLException exception) {
			_log.error("Unable to delete search query for search query ID: " +
				searchQueryId);

			throw new SQLException(exception);
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SearchQueryDAOImpl.class);

}