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
public class User {

	public User() {
	}

	public User(
		int userId, String emailAddress, String password, String salt,
		boolean emailNotification) {

		_userId = userId;
		_emailAddress = emailAddress;
		_password = password;
		_salt = salt;
		_emailNotification = emailNotification;
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public String getPassword() {
		return _password;
	}

	public String getSalt() {
		return _salt;
	}

	public int getUserId() {
		return _userId;
	}

	public boolean isActive() {
		return _active;
	}

	public boolean isEmailNotification() {
		return _emailNotification;
	}

	public void setActive(boolean active) {
		_active = active;
	}

	public void setEmailAddress(String emailAddress) {
		_emailAddress = emailAddress;
	}

	public void setEmailNotification(boolean emailNotification) {
		_emailNotification = emailNotification;
	}

	public void setPassword(String password) {
		_password = password;
	}

	public void setSalt(String salt) {
		_salt = salt;
	}

	public void setUserId(int userId) {
		_userId = userId;
	}

	private boolean _active;
	private String _emailAddress;
	private boolean _emailNotification;
	private String _password;
	private String _salt;
	private int _userId;

}