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

import java.sql.Timestamp;

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

	public String getCustomerId() {
		return _customerId;
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public Timestamp getLastLoginDate() {
		return _lastLoginDate;
	}

	public String getLastLoginIpAddress() {
		return _lastLoginIpAddress;
	}

	public String getPassword() {
		return _password;
	}

	public String getSalt() {
		return _salt;
	}

	public String getSubscriptionId() {
		return _subscriptionId;
	}

	public String getUnsubscribeToken() {
		return _unsubscribeToken;
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

	public boolean isPendingCancellation() {
		return _pendingCancellation;
	}

	public void setActive(boolean active) {
		_active = active;
	}

	public void setCustomerId(String customerId) {
		_customerId = customerId;
	}

	public void setEmailAddress(String emailAddress) {
		_emailAddress = emailAddress;
	}

	public void setEmailNotification(boolean emailNotification) {
		_emailNotification = emailNotification;
	}

	public void setLastLoginDate(Timestamp lastLoginDate) {
		_lastLoginDate = lastLoginDate;
	}

	public void setLastLoginIpAddress(String lastLoginIpAddress) {
		_lastLoginIpAddress = lastLoginIpAddress;
	}

	public void setPassword(String password) {
		_password = password;
	}

	public void setPendingCancellation(boolean pendingCancellation) {
		_pendingCancellation = pendingCancellation;
	}

	public void setSalt(String salt) {
		_salt = salt;
	}

	public void setSubscriptionId(String subscriptionId) {
		_subscriptionId = subscriptionId;
	}

	public void setUnsubscribeToken(String unsubscribeToken) {
		_unsubscribeToken = unsubscribeToken;
	}

	public void setUserId(int userId) {
		_userId = userId;
	}

	private boolean _active;
	private String _customerId;
	private String _emailAddress;
	private boolean _emailNotification;
	private Timestamp _lastLoginDate;
	private String _lastLoginIpAddress;
	private String _password;
	private boolean _pendingCancellation;
	private String _salt;
	private String _subscriptionId;
	private String _unsubscribeToken;
	private int _userId;

}