package com.app.dao.impl;

import com.app.dao.SearchQueryDAO;
import com.app.model.SearchQueryModel;
import com.app.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SearchQueryDAOImpl implements SearchQueryDAO {

	@Override
	public String getSearchQuery(int searchQueryId) throws Exception {
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
				return "";
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
	public List<SearchQueryModel> getSearchQueries() throws Exception {
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
	public void addSearchQuery(String searchQuery) throws Exception {
		Connection connection = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"INSERT INTO SearchQuery(searchQuery) VALUES(?)");

			preparedStatement.setString(1, searchQuery);

			preparedStatement.executeUpdate();
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public void updateSearchQuery(int searchQueryId, String searchQuery)
		throws Exception {

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
		finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public void deleteSearchQuery(int searchQueryId) throws Exception {
		Connection connection = null;

		try {
			connection = DatabaseUtil.getDatabaseConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"DELETE FROM SearchQuery WHERE searchQueryId = ?");

			preparedStatement.setInt(1, searchQueryId);

			preparedStatement.executeUpdate();
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}