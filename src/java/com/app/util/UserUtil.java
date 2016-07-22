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

import com.app.dao.UserDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.exception.DuplicateEmailAddressException;
import com.app.exception.InvalidEmailAddressException;
import com.app.exception.PasswordLengthException;
import com.app.exception.PasswordResetException;
import com.app.language.LanguageUtil;
import com.app.model.User;
import com.app.shiro.eBaySaltedAuthenticationInfo;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class UserUtil {

	public static User addUser(String emailAddress, String plainTextPassword)
		throws
			DatabaseConnectionException, DuplicateEmailAddressException,
			InvalidEmailAddressException, PasswordLengthException,
			SQLException {

		_validateEmailAddress(0, emailAddress);
		_validatePassword(plainTextPassword);

		List<String> passwordAndSalt = _generatePasswordAndSalt(
			plainTextPassword);

		return _userDAO.addUser(
			emailAddress, passwordAndSalt.get(0), passwordAndSalt.get(1));
	}

	public static void deleteUserByUserId(int userId)
		throws DatabaseConnectionException, SQLException {

		_userDAO.deleteUserByUserId(userId);
	}

	public static boolean exceedsMaximumNumberOfUsers()
		throws DatabaseConnectionException, SQLException {

		int searchQueryCount = _userDAO.getUserCount();

		if ((searchQueryCount + 1) > PropertiesValues.MAXIMUM_NUMBER_OF_USERS) {
			return true;
		}

		return false;
	}

	public static String generateUnsubscribeToken(String customerId) {
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();

		Object salt = rng.nextBytes();

		return new Sha512Hash(customerId, salt, 1024).toBase64();
	}

	public static User getCurrentUser()
		throws DatabaseConnectionException, SQLException {

		return getUserByUserId(getCurrentUserId());
	}

	public static int getCurrentUserId() {
		Subject subject = SecurityUtils.getSubject();

		Session session = subject.getSession();

		return (int)session.getAttribute("userId");
	}

	public static User getUserByEmailAddress(String emailAddress)
		throws DatabaseConnectionException, SQLException {

		return _userDAO.getUserByEmailAddress(emailAddress);
	}

	public static User getUserByUserId(int userId)
		throws DatabaseConnectionException, SQLException {

		return _userDAO.getUserByUserId(userId);
	}

	public static List<Integer> getUserIds(boolean active)
		throws DatabaseConnectionException, SQLException {

		return _userDAO.getUserIds(active);
	}

	public static void resetEmailsSent()
		throws DatabaseConnectionException, SQLException {

		_userDAO.resetEmailsSent();
	}

	public static void resetPassword(
			String emailAddress, String password, String passwordResetToken)
		throws Exception {

		User user = getUserByEmailAddress(emailAddress);

		Timestamp passwordResetExpiration = user.getPasswordResetExpiration();

		Date date = new Date();

		if (date.after(passwordResetExpiration) ||
			!user.getPasswordResetToken().equals(passwordResetToken)) {

			throw new PasswordResetException();
		}
		else {
			updatePassword(user.getUserId(), password);
		}
	}

	public static void unsubscribeUserFromEmailNotifications(
			String emailAddress)
		throws DatabaseConnectionException, SQLException {

		_userDAO.unsubscribeUserFromEmailNotifications(emailAddress);
	}

	public static void updateEmailsSent(int userId, int emailsSent)
		throws DatabaseConnectionException, SQLException {

		_userDAO.updateEmailsSent(userId, emailsSent);
	}

	public static void updatePassword(int userId, String plainTextPassword)
		throws
			DatabaseConnectionException, PasswordLengthException, SQLException {

		_validatePassword(plainTextPassword);

		List<String> passwordAndSalt = _generatePasswordAndSalt(
			plainTextPassword);

		_userDAO.updatePassword(
			userId, passwordAndSalt.get(0), passwordAndSalt.get(1));
	}

	public static String updatePasswordResetToken(int userId)
		throws DatabaseConnectionException, SQLException {

		RandomNumberGenerator rng = new SecureRandomNumberGenerator();

		Object randomBytes = rng.nextBytes();

		String passwordResetToken = randomBytes.toString();

		_userDAO.updatePasswordResetToken(
			userId, passwordResetToken);

		return passwordResetToken;
	}

	public static void updateUserDetails(
			String emailAddress, String currentPassword, String newPassword,
			boolean emailNotification)
		throws
			CredentialsException, DatabaseConnectionException,
			DuplicateEmailAddressException, InvalidEmailAddressException,
			SQLException {

		User user = getCurrentUser();

		_validateCredentials(
			user.getEmailAddress(), user.getPassword(), currentPassword,
			user.getSalt());

		_validateEmailAddress(getCurrentUserId(), emailAddress);

		List<String> passwordAndSalt = _generatePasswordAndSalt(
			newPassword);

		_userDAO.updateUserDetails(
			user.getUserId(), emailAddress, passwordAndSalt.get(0),
			passwordAndSalt.get(1), emailNotification);
	}

	public static void updateUserLoginDetails(
			Timestamp lastLoginDate, String lastLoginIpAddress)
		throws DatabaseConnectionException, SQLException {

		_userDAO.updateUserLoginDetails(
			getCurrentUserId(), lastLoginDate, lastLoginIpAddress);
	}

	public static void updateUserSubscription(
			int userId, String unsubscribeToken, String customerId,
			String subscriptionId, boolean active, boolean pendingCancellation)
		throws DatabaseConnectionException, SQLException {

		_userDAO.updateUserSubscription(
			userId, unsubscribeToken, customerId, subscriptionId,
			active, pendingCancellation);
	}

	@Autowired
	public void setHashedCredentialsMatcher(
		HashedCredentialsMatcher hashedCredentialsMatcher) {

		_hashedCredentialsMatcher = hashedCredentialsMatcher;
	}

	@Autowired
	public void setUserDAO(UserDAO userDAO) {
		_userDAO = userDAO;
	}

	private static List<String> _generatePasswordAndSalt(
		String plainTextPassword) {

		RandomNumberGenerator rng = new SecureRandomNumberGenerator();

		Object salt = rng.nextBytes();

		String hashedPasswordBase64 = new Sha512Hash(
			plainTextPassword, salt, 1024).toBase64();

		List<String> passwordAndSalt = new ArrayList<>();

		passwordAndSalt.add(hashedPasswordBase64);
		passwordAndSalt.add(salt.toString());

		return passwordAndSalt;
	}

	private static void _validateCredentials(
			String emailAddress, String encryptedPassword, String password,
			String salt)
		throws CredentialsException {

		boolean credentialsMatch = _hashedCredentialsMatcher.doCredentialsMatch(
			new UsernamePasswordToken(emailAddress, password),
			new eBaySaltedAuthenticationInfo(
				emailAddress, encryptedPassword, salt));

		if (!credentialsMatch) {
			throw new CredentialsException();
		}
	}

	private static void _validateEmailAddress(int userId, String emailAddress)
		throws
			DatabaseConnectionException,
			DuplicateEmailAddressException,
				InvalidEmailAddressException, SQLException {

		if (!ValidatorUtil.isValidEmailAddress(emailAddress)) {
			throw new InvalidEmailAddressException();
		}

		User user = _userDAO.getUserByEmailAddress(emailAddress);

		if ((user != null) && (userId != user.getUserId())) {
			throw new DuplicateEmailAddressException();
		}
	}

	private static void _validatePassword(String password)
		throws PasswordLengthException {

		if (ValidatorUtil.isNull(password) || (password.length() < 6)) {
			throw new PasswordLengthException();
		}
	}

	private static HashedCredentialsMatcher _hashedCredentialsMatcher;

	private static UserDAO _userDAO;

}