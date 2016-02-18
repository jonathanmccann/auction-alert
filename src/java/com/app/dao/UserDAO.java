/**
 * Copyright (c) 2014-present Jonathan McCann
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package com.app.dao;

import com.app.exception.DatabaseConnectionException;
import com.app.model.User;
import com.app.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class UserDAO {

	public int addUser(String emailAddress, String password, String salt)
		throws Exception {

		_log.debug("Adding user with emailAddress: {}", emailAddress);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, emailAddress);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, salt);

			preparedStatement.executeUpdate();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();

			resultSet.next();

			return resultSet.getInt(1);
		}
	}

	public void deleteUserByEmailAddress(String emailAddress)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting user with email address: {}", emailAddress);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_USER_BY_EMAIL_ADDRESS_SQL)) {

			preparedStatement.setString(1, emailAddress);

			preparedStatement.executeUpdate();
		}
	}

	public void deleteUserByUserId(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_USER_BY_USER_ID_SQL)) {

			preparedStatement.setInt(1, userId);

			preparedStatement.executeUpdate();
		}
	}

	public User getUserByEmailAddress(String emailAddress)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting user with emailAddress: {}", emailAddress);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_USER_BY_EMAIL_ADDRESS_SQL)) {

			preparedStatement.setString(1, emailAddress);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return createUserFromResultSet(resultSet);
			}
			else {
				return null;
			}
		}
	}

	public User getUserByUserId(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting user with user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_USER_BY_USER_ID_SQL)) {

			preparedStatement.setInt(1, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return createUserFromResultSet(resultSet);
			}
			else {
				return null;
			}
		}
	}

	public List<Integer> getUserIds()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting all of the user IDs");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_USER_IDS);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			List<Integer> userIds = new ArrayList<>();

			while (resultSet.next()) {
				userIds.add(resultSet.getInt("userId"));
			}

			return userIds;
		}
	}

	public void updateUser(int userId, String emailAddress)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Updating user ID: {} to email address: {}", userId,
			emailAddress);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_USER_SQL)) {

			preparedStatement.setString(1, emailAddress);
			preparedStatement.setInt(2, userId);

			preparedStatement.executeUpdate();
		}
	}

	private static User createUserFromResultSet(ResultSet resultSet)
		throws SQLException{

		User user = new User();

		user.setEmailAddress(resultSet.getString("emailAddress"));
		user.setUserId(resultSet.getInt("userId"));
		user.setPhoneNumber(resultSet.getString("phoneNumber"));
		user.setPassword(resultSet.getString("password"));
		user.setSalt(resultSet.getString("salt"));

		return user;
	}

	private static final String _ADD_USER_SQL =
		"INSERT INTO User_(emailAddress, password, salt) VALUES(?, ?, ?)";

	private static final String _DELETE_USER_BY_EMAIL_ADDRESS_SQL =
		"DELETE FROM User_ WHERE emailAddress = ?";

	private static final String _DELETE_USER_BY_USER_ID_SQL =
		"DELETE FROM User_ WHERE userId = ?";

	private static final String _GET_USER_BY_EMAIL_ADDRESS_SQL =
		"SELECT * FROM User_ WHERE emailAddress = ?";

	private static final String _GET_USER_BY_USER_ID_SQL =
		"SELECT * FROM User_ WHERE userId = ?";

	private static final String _GET_USER_IDS =
		"SELECT userId FROM User_";

	private static final String _UPDATE_USER_SQL =
		"UPDATE User_ SET emailAddress = ? WHERE userId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		UserDAO.class);

}