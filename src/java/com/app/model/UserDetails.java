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

package com.app.model;

/**
 * @author Jonathan McCann
 */
public class UserDetails {

	public UserDetails() {
	}

	public UserDetails(
		User user, NotificationPreferences notificationPreferences) {

		_user = user;
		_notificationPreferences = notificationPreferences;
	}

	public NotificationPreferences getNotificationPreferences() {
		return _notificationPreferences;
	}

	public User getUser() {
		return _user;
	}

	public void setNotificationPreferences(
		NotificationPreferences notificationPreferences) {

		_notificationPreferences = notificationPreferences;
	}

	public void setUser(User user) {
		_user = user;
	}

	private NotificationPreferences _notificationPreferences;
	private User _user;

}