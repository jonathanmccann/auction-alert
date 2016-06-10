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
import com.app.model.User;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
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
		InvalidEmailAddressException, SQLException {

		_validateEmailAddress(0, emailAddress);

		List<String> passwordAndSalt = _generatePasswordAndSalt(
			plainTextPassword);

		return _userDAO.addUser(
			emailAddress, passwordAndSalt.get(0), passwordAndSalt.get(1));
	}

	public static void deleteUserByUserId(int userId)
		throws DatabaseConnectionException, SQLException {

		_userDAO.deleteUserByUserId(userId);
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

	public static void updateUser(User user)
		throws
			DatabaseConnectionException,
			DuplicateEmailAddressException,
				InvalidEmailAddressException, SQLException {

		int userId = user.getUserId();

		String emailAddress = user.getEmailAddress();

		_validateEmailAddress(userId, emailAddress);

		_userDAO.updateUser(
			userId, emailAddress, user.isEmailNotification(), user.isActive());
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

	private static UserDAO _userDAO;

}