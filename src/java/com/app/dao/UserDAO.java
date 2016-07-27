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
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

/**
 * @author Jonathan McCann
 */
public class UserDAO {

	@CacheEvict(value = "userIds", allEntries = true)
	public User addUser(String emailAddress, String password, String salt)
		throws DatabaseConnectionException, SQLException {

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

			return new User(
				resultSet.getInt(1), emailAddress, password, salt, true);
		}
	}

	@CacheEvict(value = "userIds", allEntries = true)
	public void deactivateUser(String customerId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deactivating user with customer ID: {}", customerId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DEACTIVATE_USER_SQL)) {

			preparedStatement.setString(1, customerId);

			preparedStatement.executeUpdate();
		}
	}

	@Caching(
		evict = {
			@CacheEvict(value = "userByUserId", key = "#userId"),
			@CacheEvict(value = "userIds", allEntries = true)
		}
	)
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
				return _createUserFromResultSet(resultSet);
			}
			else {
				return null;
			}
		}
	}

	@Cacheable(value = "userByUserId", key = "#userId")
	public User getUserByUserId(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting user with user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_USER_BY_USER_ID_SQL)) {

			preparedStatement.setInt(1, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return _createUserFromResultSet(resultSet);
			}
			else {
				return null;
			}
		}
	}

	public int getUserCount() throws DatabaseConnectionException, SQLException {
		_log.debug("Getting user count");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_USER_COUNT_SQL)) {

			int searchQueryCount = 0;

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				searchQueryCount = resultSet.getInt(1);
			}

			return searchQueryCount;
		}
	}

	@Cacheable(value = "userIds")
	public List<Integer> getUserIds(boolean active)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting all of the user IDs where active is: {}", active);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_USER_IDS)) {

			preparedStatement.setBoolean(1, active);

			ResultSet resultSet = preparedStatement.executeQuery();

			List<Integer> userIds = new ArrayList<>();

			while (resultSet.next()) {
				userIds.add(resultSet.getInt("userId"));
			}

			return userIds;
		}
	}

	@CacheEvict(value = "userByUserId", allEntries = true)
	public void resetEmailsSent()
		throws DatabaseConnectionException, SQLException {

		_log.debug("Resetting emails sent for all users");

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_RESET_EMAILS_SENT_SQL)) {

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "userByUserId", allEntries = true)
	public void unsubscribeUserFromEmailNotifications(String emailAddress)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Unsubscribing user email address: {}", emailAddress);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UNSUBSCRIBE_USER_FROM_EMAIL_NOTIFICATIONS_SQL)) {

			preparedStatement.setString(1, emailAddress);

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "userByUserId", key = "#userId")
	public void updateEmailsSent(int userId, int emailsSent)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Updating emails sent for user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_EMAILS_SENT_SQL)) {

			preparedStatement.setInt(1, emailsSent);
			preparedStatement.setInt(2, userId);

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "userByUserId", key = "#userId")
	public void updatePassword(int userId, String password, String salt)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Updating emails sent for user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_PASSWORD_SQL)) {

			preparedStatement.setString(1, password);
			preparedStatement.setString(2, salt);
			preparedStatement.setInt(3, userId);

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "userByUserId", key = "#userId")
	public void updatePasswordResetToken(int userId, String passwordResetToken)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Updating password reset token for user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_PASSWORD_RESET_TOKEN_SQL)) {

			Date currentDate = new Date();

			Calendar calendar = Calendar.getInstance();

			calendar.setTime(currentDate);
			calendar.add(Calendar.HOUR, 1);

			Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

			preparedStatement.setString(1, passwordResetToken);
			preparedStatement.setTimestamp(2, timestamp);
			preparedStatement.setInt(3, userId);

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "userByUserId", key = "#userId")
	public void updateUser(
			int userId, String emailAddress, boolean emailNotification,
			String customerId, String subscriptionId, boolean active,
			boolean pendingCancellation)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Updating user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_USER_SQL)) {

			preparedStatement.setString(1, emailAddress);
			preparedStatement.setBoolean(2, emailNotification);
			preparedStatement.setString(3, customerId);
			preparedStatement.setString(4, subscriptionId);
			preparedStatement.setBoolean(5, active);
			preparedStatement.setBoolean(6, pendingCancellation);
			preparedStatement.setInt(7, userId);

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "userByUserId", key = "#userId")
	public void updateUserDetails(
			int userId, String emailAddress, String password, String salt,
			boolean emailNotification)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Updating user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_USER_DETAILS_SQL)) {

			preparedStatement.setString(1, emailAddress);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, salt);
			preparedStatement.setBoolean(4, emailNotification);
			preparedStatement.setInt(5, userId);

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "userByUserId", key = "#userId")
	public void updateUserEmailDetails(
			int userId, String emailAddress, boolean emailNotification)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Updating user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_USER_EMAIL_DETAILS_SQL)) {

			preparedStatement.setString(1, emailAddress);
			preparedStatement.setBoolean(2, emailNotification);
			preparedStatement.setInt(3, userId);

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "userByUserId", key = "#userId")
	public void updateUserLoginDetails(
			int userId, Timestamp lastLoginDate, String lastLoginIpAddress)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Updating login details for user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_USER_LOGIN_DETAILS_SQL)) {

			preparedStatement.setTimestamp(1, lastLoginDate);
			preparedStatement.setString(2, lastLoginIpAddress);
			preparedStatement.setInt(3, userId);

			preparedStatement.executeUpdate();
		}
	}

	@CacheEvict(value = "userByUserId", key = "#userId")
	public void updateUserSubscription(
			int userId, String unsubscribeToken, String customerId,
			String subscriptionId, boolean active, boolean pendingCancellation)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Updating user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_UPDATE_USER_SUBSCRIPTION_SQL)) {

			preparedStatement.setString(1, unsubscribeToken);
			preparedStatement.setString(2, customerId);
			preparedStatement.setString(3, subscriptionId);
			preparedStatement.setBoolean(4, active);
			preparedStatement.setBoolean(5, pendingCancellation);
			preparedStatement.setInt(6, userId);

			preparedStatement.executeUpdate();
		}
	}

	private static User _createUserFromResultSet(ResultSet resultSet)
		throws SQLException {

		User user = new User();

		user.setEmailAddress(resultSet.getString("emailAddress"));
		user.setUserId(resultSet.getInt("userId"));
		user.setPassword(resultSet.getString("password"));
		user.setSalt(resultSet.getString("salt"));
		user.setEmailNotification(resultSet.getBoolean("emailNotification"));
		user.setUnsubscribeToken(resultSet.getString("unsubscribeToken"));
		user.setEmailsSent(resultSet.getInt("emailsSent"));
		user.setCustomerId(resultSet.getString("customerId"));
		user.setSubscriptionId(resultSet.getString("subscriptionId"));
		user.setActive(resultSet.getBoolean("active"));
		user.setPendingCancellation(resultSet.getBoolean("pendingCancellation"));
		user.setLastLoginDate(resultSet.getTimestamp("lastLoginDate"));
		user.setLastLoginIpAddress(resultSet.getString("lastLoginIpAddress"));
		user.setPasswordResetToken(resultSet.getString("passwordResetToken"));
		user.setPasswordResetExpiration(
			resultSet.getTimestamp("passwordResetExpiration"));

		return user;
	}

	private static final String _ADD_USER_SQL =
		"INSERT INTO User_(emailAddress, password, salt) VALUES(?, ?, ?)";

	public static final String _DEACTIVATE_USER_SQL =
		"UPDATE User_ SET active = false, pendingCancellation = false WHERE " +
			"customerId = ?";

	private static final String _DELETE_USER_BY_USER_ID_SQL =
		"DELETE FROM User_ WHERE userId = ?";

	private static final String _GET_USER_BY_EMAIL_ADDRESS_SQL =
		"SELECT * FROM User_ WHERE emailAddress = ?";

	private static final String _GET_USER_BY_USER_ID_SQL =
		"SELECT * FROM User_ WHERE userId = ?";

	private static final String _GET_USER_COUNT_SQL =
		"SELECT COUNT(*) FROM User_";

	private static final String _GET_USER_IDS =
		"SELECT userId FROM User_ WHERE active = ? ORDER BY userId";

	public static final String _RESET_EMAILS_SENT_SQL =
		"UPDATE User_ SET emailsSent = 0";

	public static final String _UPDATE_PASSWORD_RESET_TOKEN_SQL =
		"UPDATE User_ SET passwordResetToken = ?, passwordResetExpiration= ? " +
			"WHERE userId = ?";

	public static final String _UNSUBSCRIBE_USER_FROM_EMAIL_NOTIFICATIONS_SQL =
		"UPDATE User_ SET emailNotification = false WHERE emailAddress = ?";

	public static final String _UPDATE_EMAILS_SENT_SQL =
		"UPDATE User_ SET emailsSent = ? where userId = ?";

	public static final String _UPDATE_PASSWORD_SQL =
		"UPDATE User_ SET password = ?, salt = ?, passwordResetToken = NULL " +
			"WHERE userId = ?";

	private static final String _UPDATE_USER_SQL =
		"UPDATE User_ SET emailAddress = ?, emailNotification = ?, " +
			"customerId = ?, subscriptionId = ?, active = ?, " +
				"pendingCancellation = ? WHERE userId = ?";

	private static final String _UPDATE_USER_DETAILS_SQL =
		"UPDATE User_ SET emailAddress = ?, password = ?, salt = ?, " +
			"emailNotification = ? WHERE userId = ?";

	private static final String _UPDATE_USER_EMAIL_DETAILS_SQL =
		"UPDATE User_ SET emailAddress = ?, emailNotification = ? WHERE " +
			"userId = ?";

	public static final String _UPDATE_USER_LOGIN_DETAILS_SQL =
		"UPDATE User_ SET lastLoginDate = ?, lastLoginIpAddress = ? WHERE " +
			"userId = ?";

	private static final String _UPDATE_USER_SUBSCRIPTION_SQL =
		"UPDATE User_ SET unsubscribeToken = ?, customerId = ?, " +
			"subscriptionId = ?, active = ?, pendingCancellation = ? " +
				"WHERE userId = ?";

	private static final Logger _log = LoggerFactory.getLogger(UserDAO.class);

}