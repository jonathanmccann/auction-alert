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

package com.app.util;

import com.app.dao.NotificationPreferencesDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.model.NotificationPreferences;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class NotificationPreferencesUtil {

	public static void addNotificationPreferences(
			NotificationPreferences notificationPreferences)
		throws DatabaseConnectionException, SQLException {

		_notificationPreferencesDAO.addNotificationPreferences(
			notificationPreferences);
	}

	public static void deleteNotificationPreferencesByUserId(
			int userId)
		throws DatabaseConnectionException, SQLException {

		_notificationPreferencesDAO.deleteNotificationPreferencesByUserId(
			userId);
	}

	public static NotificationPreferences getNotificationPreferencesByUserId(
			int userId)
		throws DatabaseConnectionException, SQLException {

		return _notificationPreferencesDAO.getNotificationPreferencesByUserId(
			userId);
	}

	public static void updateNotificationPreferences(
			NotificationPreferences notificationPreferences)
		throws DatabaseConnectionException, SQLException {

		_notificationPreferencesDAO.updateNotificationPreferences(
			notificationPreferences);
	}

	@Autowired
	public void setUserDAO(NotificationPreferencesDAO notificationDAO) {
		_notificationPreferencesDAO = notificationDAO;
	}

	private static NotificationPreferencesDAO _notificationPreferencesDAO;

}