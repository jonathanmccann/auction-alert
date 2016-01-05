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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class UserDAO {

	public void addUser(String emailAddress, String password, String salt)
		throws Exception {

		_log.debug("Adding user with emailAddress: {}", emailAddress);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_USER_SQL)) {

			preparedStatement.setString(1, emailAddress);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, salt);

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

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					User user = new User();

					user.setEmailAddress(emailAddress);
					user.setUserId(resultSet.getLong("userId"));
					user.setPhoneNumber(resultSet.getString("phoneNumber"));
					user.setPassword(resultSet.getString("password"));
					user.setSalt(resultSet.getString("salt"));

					return user;
				}
				else {
					throw new SQLException();
				}
			}
		}
	}

	private static final String _ADD_USER_SQL =
		"INSERT INTO User_(emailAddress, password, salt) VALUES(?, ?, ?)";

	private static final String _GET_USER_BY_EMAIL_ADDRESS_SQL =
		"SELECT * FROM User_ WHERE emailAddress = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		UserDAO.class);

}