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
import com.app.model.NotificationPreferences;
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
public class NotificationPreferencesDAO {

	public void addNotificationPreferences(
			NotificationPreferences notificationPreferences)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding notification preferences for user ID: {}",
			notificationPreferences.getUserId());

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement =
				connection.prepareStatement(_ADD_NOTIFICATION_PREFERENCES)) {

			populateAddNotificationPreferencesPreparedStatement(
				preparedStatement, notificationPreferences);

			preparedStatement.executeUpdate();
		}
	}

	public void deleteNotificationPreferencesByUserId(int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Deleting notification preferences for user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_DELETE_NOTIFICATION_PREFERENCES_SQL)) {

			preparedStatement.setInt(1, userId);

			preparedStatement.executeUpdate();
		}
	}

	public NotificationPreferences getNotificationPreferencesByUserId(
			int userId)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Getting notification preferences for user ID: {}", userId);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement =
				connection.prepareStatement(_GET_NOTIFICATION_PREFERENCES)) {

			preparedStatement.setInt(1, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return createNotificationPreferencesFromResultSet(resultSet);
			}
			else {
				throw new SQLException(
					"There are no notification preferences for user ID: " +
						userId);
			}
		}
	}

	public void updateNotificationPreferences(
			NotificationPreferences notificationPreferences)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Updating notification preferences for user ID: {}",
			notificationPreferences.getUserId());

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement =
				connection.prepareStatement(_UPDATE_NOTIFICATION_PREFERENCES)) {

			populateUpdateNotificationPreferencesPreparedStatement(
				preparedStatement, notificationPreferences);

			preparedStatement.executeUpdate();
		}
	}

	private static NotificationPreferences createNotificationPreferencesFromResultSet(
			ResultSet resultSet)
		throws SQLException {

		NotificationPreferences notificationPreferences =
			new NotificationPreferences();

		notificationPreferences.setUserId(resultSet.getInt("userId"));
		notificationPreferences.setEmailNotification(
			resultSet.getBoolean("emailNotification"));
		notificationPreferences.setTextNotification(
			resultSet.getBoolean("textNotification"));
		notificationPreferences.setBasedOnTime(
			resultSet.getBoolean("basedOnTime"));
		notificationPreferences.setStartOfDay(resultSet.getInt("startOfDay"));
		notificationPreferences.setEndOfDay(resultSet.getInt("endOfDay"));
		notificationPreferences.setTimeZone(resultSet.getString("timeZone"));
		notificationPreferences.setWeekdayDayEmailNotification(
			resultSet.getBoolean("weekdayDayEmailNotification"));
		notificationPreferences.setWeekdayDayTextNotification(
			resultSet.getBoolean("weekdayDayTextNotification"));
		notificationPreferences.setWeekdayNightEmailNotification(
			resultSet.getBoolean("weekdayNightEmailNotification"));
		notificationPreferences.setWeekdayNightTextNotification(
			resultSet.getBoolean("weekdayNightTextNotification"));
		notificationPreferences.setWeekendDayEmailNotification(
			resultSet.getBoolean("weekendDayEmailNotification"));
		notificationPreferences.setWeekendDayTextNotification(
			resultSet.getBoolean("weekendDayTextNotification"));
		notificationPreferences.setWeekendNightEmailNotification(
			resultSet.getBoolean("weekendNightEmailNotification"));
		notificationPreferences.setWeekendNightTextNotification(
			resultSet.getBoolean("weekendNightTextNotification"));

		return notificationPreferences;
	}

	private static void populateAddNotificationPreferencesPreparedStatement(
			PreparedStatement preparedStatement,
			NotificationPreferences notificationPreferences)
		throws SQLException{

		preparedStatement.setInt(1, notificationPreferences.getUserId());
		preparedStatement.setBoolean(
			2, notificationPreferences.isEmailNotification());
		preparedStatement.setBoolean(
			3, notificationPreferences.isTextNotification());
		preparedStatement.setBoolean(
			4, notificationPreferences.isBasedOnTime());
		preparedStatement.setInt(5, notificationPreferences.getStartOfDay());
		preparedStatement.setInt(6, notificationPreferences.getEndOfDay());
		preparedStatement.setString(7, notificationPreferences.getTimeZone());
		preparedStatement.setBoolean(
			8, notificationPreferences.isWeekdayDayEmailNotification());
		preparedStatement.setBoolean(
			9, notificationPreferences.isWeekdayDayTextNotification());
		preparedStatement.setBoolean(
			10, notificationPreferences.isWeekdayNightEmailNotification());
		preparedStatement.setBoolean(
			11, notificationPreferences.isWeekdayNightTextNotification());
		preparedStatement.setBoolean(
			12, notificationPreferences.isWeekendDayEmailNotification());
		preparedStatement.setBoolean(
			13, notificationPreferences.isWeekendDayTextNotification());
		preparedStatement.setBoolean(
			14, notificationPreferences.isWeekendNightEmailNotification());
		preparedStatement.setBoolean(
			15, notificationPreferences.isWeekendNightTextNotification());
	}

	private static void populateUpdateNotificationPreferencesPreparedStatement(
			PreparedStatement preparedStatement,
			NotificationPreferences notificationPreferences)
		throws SQLException{

		preparedStatement.setBoolean(
			1, notificationPreferences.isEmailNotification());
		preparedStatement.setBoolean(
			2, notificationPreferences.isTextNotification());
		preparedStatement.setBoolean(
			3, notificationPreferences.isBasedOnTime());
		preparedStatement.setInt(4, notificationPreferences.getStartOfDay());
		preparedStatement.setInt(5, notificationPreferences.getEndOfDay());
		preparedStatement.setString(6, notificationPreferences.getTimeZone());
		preparedStatement.setBoolean(
			7, notificationPreferences.isWeekdayDayEmailNotification());
		preparedStatement.setBoolean(
			8, notificationPreferences.isWeekdayDayTextNotification());
		preparedStatement.setBoolean(
			9, notificationPreferences.isWeekdayNightEmailNotification());
		preparedStatement.setBoolean(
			10, notificationPreferences.isWeekdayNightTextNotification());
		preparedStatement.setBoolean(
			11, notificationPreferences.isWeekendDayEmailNotification());
		preparedStatement.setBoolean(
			12, notificationPreferences.isWeekendDayTextNotification());
		preparedStatement.setBoolean(
			13, notificationPreferences.isWeekendNightEmailNotification());
		preparedStatement.setBoolean(
			14, notificationPreferences.isWeekendNightTextNotification());
		preparedStatement.setInt(15, notificationPreferences.getUserId());
	}

	private static final String _ADD_NOTIFICATION_PREFERENCES =
		"INSERT INTO NotificationPreferences(userId, emailNotification, " +
			"textNotification, basedOnTime, startOfDay, endOfDay, timeZone, " +
				"weekdayDayEmailNotification, weekdayDayTextNotification, " +
					"weekdayNightEmailNotification, weekdayNightTextNotification, " +
						"weekendDayEmailNotification, weekendDayTextNotification, " +
							"weekendNightEmailNotification, weekendNightTextNotification) " +
								"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String _DELETE_NOTIFICATION_PREFERENCES_SQL =
		"DELETE FROM NotificationPreferences WHERE userId = ?";

	private static final String _GET_NOTIFICATION_PREFERENCES =
		"SELECT * FROM NotificationPreferences WHERE userId = ?";

	private static final String _UPDATE_NOTIFICATION_PREFERENCES =
		"UPDATE NotificationPreferences SET emailNotification = ?, " +
			"textNotification = ?, basedOnTime = ?, startOfDay = ?, endOfDay = ?, " +
				"timeZone = ?, weekdayDayEmailNotification = ?, weekdayDayTextNotification = ?, " +
					"weekdayNightEmailNotification = ?, weekdayNightTextNotification = ?, " +
						"weekendDayEmailNotification = ?, weekendDayTextNotification = ?, " +
							"weekendNightEmailNotification = ?, weekendNightTextNotification = ? " +
								"WHERE userId = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		NotificationPreferencesDAO.class);

}