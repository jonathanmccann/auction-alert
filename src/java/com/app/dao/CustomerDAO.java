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
import com.app.model.Customer;
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
public class CustomerDAO {

	public void addCustomer(
			int userId, String customerId, String subscriptionId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding customer with user ID: {} and customer ID: {}", userId,
			customerId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_CUSTOMER_SQL)) {

			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, customerId);
			preparedStatement.setString(3, subscriptionId);

			preparedStatement.executeUpdate();
		}
	}

	public void deleteCustomer(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting customer with user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_CUSTOMER_SQL)) {

			preparedStatement.setInt(1, userId);

			preparedStatement.executeUpdate();
		}
	}

	public Customer getCustomer(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting customer with user IDL {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_CUSTOMER_SQL)) {

			preparedStatement.setInt(1, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return _createCustomerFromResultSet(resultSet);
			}
			else {
				throw new SQLException(
					"There is no customer for user ID: " + userId);
			}
		}
	}

	public void updateCustomer(
			int userId, String customerId, String subscriptionId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Updating customer with user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_CUSTOMER_SQL)) {

			preparedStatement.setString(1, customerId);
			preparedStatement.setString(2, subscriptionId);
			preparedStatement.setInt(3, userId);

			preparedStatement.executeUpdate();
		}
	}

	private static Customer _createCustomerFromResultSet(ResultSet resultSet)
		throws SQLException {

		Customer customer = new Customer();

		customer.setUserId(resultSet.getInt("userId"));
		customer.setCustomerId(resultSet.getString("customerId"));
		customer.setSubscriptionId(resultSet.getString("subscriptionId"));

		return customer;
	}

	private static final String _ADD_CUSTOMER_SQL =
		"INSERT INTO Customer(userId, customerId, subscriptionId) " +
			"VALUES(?, ?, ?)";

	private static final String _DELETE_CUSTOMER_SQL =
		"DELETE FROM Customer WHERE userId = ?";

	private static final String _GET_CUSTOMER_SQL =
		"SELECT * FROM Customer WHERE userId = ?";

	private static final String _UPDATE_CUSTOMER_SQL =
		"UPDATE Customer SET customerId = ?, subscriptionId = ? WHERE userId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		CustomerDAO.class);

}