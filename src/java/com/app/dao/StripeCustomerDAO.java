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
import com.app.model.StripeCustomer;
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
public class StripeCustomerDAO {

	public void addCustomer(
			int userId, String customerId, String subscriptionId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding customer for user ID: {}", userId);

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

	public StripeCustomer getCustomer(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting customer with user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_CUSTOMER_SQL)) {

			preparedStatement.setInt(1, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return _createCustomerFromResultSet(resultSet);
			}
			else {
				return null;
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

	private static StripeCustomer _createCustomerFromResultSet(ResultSet resultSet)
		throws SQLException {

		StripeCustomer stripeCustomer = new StripeCustomer();

		stripeCustomer.setUserId(resultSet.getInt("userId"));
		stripeCustomer.setCustomerId(resultSet.getString("customerId"));
		stripeCustomer.setSubscriptionId(resultSet.getString("subscriptionId"));

		return stripeCustomer;
	}

	private static final String _ADD_CUSTOMER_SQL =
		"INSERT INTO StripeCustomer(userId, customerId, subscriptionId) " +
			"VALUES(?, ?, ?)";

	private static final String _DELETE_CUSTOMER_SQL =
		"DELETE FROM StripeCustomer WHERE userId = ?";

	private static final String _GET_CUSTOMER_SQL =
		"SELECT * FROM StripeCustomer WHERE userId = ?";

	private static final String _UPDATE_CUSTOMER_SQL =
		"UPDATE StripeCustomer SET customerId = ?, subscriptionId = ? WHERE " +
			"userId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		StripeCustomerDAO.class);

}