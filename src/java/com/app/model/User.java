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
		boolean emailNotification, String marketplaceId) {

		_userId = userId;
		_emailAddress = emailAddress;
		_password = password;
		_salt = salt;
		_emailNotification = emailNotification;
		_marketplaceId = marketplaceId;
	}

	public String getCurrentPassword() {
		return _currentPassword;
	}

	public String getCustomerId() {
		return _customerId;
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public int getEmailsSent() {
		return _emailsSent;
	}

	public Timestamp getLastLoginDate() {
		return _lastLoginDate;
	}

	public String getLastLoginIpAddress() {
		return _lastLoginIpAddress;
	}

	public String getNewPassword() {
		return _newPassword;
	}

	public String getPassword() {
		return _password;
	}

	public Timestamp getPasswordResetExpiration() {
		return _passwordResetExpiration;
	}

	public String getPasswordResetToken() {
		return _passwordResetToken;
	}

	public String getMarketplaceId() {
		return _marketplaceId;
	}

	public String getSalt() {
		return _salt;
	}

	public String getSubscriptionId() {
		return _subscriptionId;
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

	public void setCurrentPassword(String currentPassword) {
		_currentPassword = currentPassword;
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

	public void setEmailsSent(int emailsSent) {
		_emailsSent = emailsSent;
	}

	public void setLastLoginDate(Timestamp lastLoginDate) {
		_lastLoginDate = lastLoginDate;
	}

	public void setLastLoginIpAddress(String lastLoginIpAddress) {
		_lastLoginIpAddress = lastLoginIpAddress;
	}

	public void setNewPassword(String newPassword) {
		_newPassword = newPassword;
	}

	public void setPassword(String password) {
		_password = password;
	}

	public void setPasswordResetExpiration(Timestamp passwordResetExpiration) {
		_passwordResetExpiration = passwordResetExpiration;
	}

	public void setPasswordResetToken(String passwordResetToken) {
		_passwordResetToken = passwordResetToken;
	}

	public void setPendingCancellation(boolean pendingCancellation) {
		_pendingCancellation = pendingCancellation;
	}

	public void setMarketplaceId(String marketplaceId) {
		_marketplaceId = marketplaceId;
	}

	public void setSalt(String salt) {
		_salt = salt;
	}

	public void setSubscriptionId(String subscriptionId) {
		_subscriptionId = subscriptionId;
	}

	public void setUserId(int userId) {
		_userId = userId;
	}

	private boolean _active;
	private String _currentPassword;
	private String _customerId;
	private String _emailAddress;
	private boolean _emailNotification;
	private int _emailsSent;
	private Timestamp _lastLoginDate;
	private String _lastLoginIpAddress;
	private String _newPassword;
	private String _password;
	private Timestamp _passwordResetExpiration;
	private String _passwordResetToken;
	private boolean _pendingCancellation;
	private String _marketplaceId;
	private String _salt;
	private String _subscriptionId;
	private int _userId;

}