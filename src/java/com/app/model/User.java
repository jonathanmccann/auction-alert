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
		int userId, String emailAddress, String phoneNumber, String password,
		String salt) {

		_userId = userId;
		_emailAddress = emailAddress;
		_phoneNumber = phoneNumber;
		_password = password;
		_salt = salt;
	}

	public int getUserId() {
		return _userId;
	}

	public void setUserId(int userId) {
		_userId = userId;
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		_emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return _phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		_phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		_password = password;
	}

	public String getSalt() {
		return _salt;
	}

	public void setSalt(String salt) {
		_salt = salt;
	}

	private int _userId;
	private String _emailAddress;
	private String _phoneNumber;
	private String _password;
	private String _salt;

}