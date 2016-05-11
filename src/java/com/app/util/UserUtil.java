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
import com.app.exception.InvalidPhoneNumberException;
import com.app.model.User;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan McCann
 */
@Service
public class UserUtil {

	public static User addUser(
			String emailAddress, String phoneNumber, String plainTextPassword)
		throws Exception {

		phoneNumber = sanitizePhoneNumber(phoneNumber);

		validate(0, emailAddress, phoneNumber);

		List<String> passwordAndSalt = generatePasswordAndSalt(
			plainTextPassword);

		return _userDAO.addUser(
			emailAddress, phoneNumber, passwordAndSalt.get(0),
			passwordAndSalt.get(1));
	}

	public static void deleteUserByUserId(int userId)
		throws DatabaseConnectionException, SQLException {

		_userDAO.deleteUserByUserId(userId);
	}

	public static User getUserByEmailAddress(String emailAddress)
		throws DatabaseConnectionException, SQLException {

		return _userDAO.getUserByEmailAddress(emailAddress);
	}

	public static User getUserByUserId(int userId)
		throws DatabaseConnectionException, SQLException {

		return _userDAO.getUserByUserId(userId);
	}

	public static List<Integer> getUserIds()
		throws DatabaseConnectionException, SQLException {

		return _userDAO.getUserIds();
	}

	public static int getCurrentUserId() {
		Subject subject = SecurityUtils.getSubject();

		Session session = subject.getSession();

		return (int)session.getAttribute("userId");
	}

	public static void updateUser(User user)
		throws
			DatabaseConnectionException, DuplicateEmailAddressException,
				InvalidEmailAddressException, InvalidPhoneNumberException,
					SQLException {

		int userId = user.getUserId();

		String emailAddress = user.getEmailAddress();
		String phoneNumber = sanitizePhoneNumber(user.getPhoneNumber());

		validate(userId, emailAddress, phoneNumber);

		_userDAO.updateUser(userId, emailAddress, phoneNumber);
	}

	private static List<String> generatePasswordAndSalt(
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

	@Autowired
	public void setUserDAO(UserDAO userDAO) {
		_userDAO = userDAO;
	}

	private static String sanitizePhoneNumber(String phoneNumber) {
		return phoneNumber.replaceAll("[^\\d]", "");
	}

	private static void validate(
			int userId, String emailAddress, String phoneNumber)
		throws
			DatabaseConnectionException, DuplicateEmailAddressException,
				InvalidEmailAddressException, InvalidPhoneNumberException,
					SQLException {

		validateEmailAddress(userId, emailAddress);
		validatePhoneNumber(phoneNumber);
	}

	private static void validateEmailAddress(int userId, String emailAddress)
		throws
			DatabaseConnectionException, DuplicateEmailAddressException,
				InvalidEmailAddressException, SQLException {

		if (!ValidatorUtil.isValidEmailAddress(emailAddress)) {
			throw new InvalidEmailAddressException();
		}

		User user = _userDAO.getUserByEmailAddress(emailAddress);

		if ((user != null) && (userId != user.getUserId())) {
			throw new DuplicateEmailAddressException();
		}
	}

	private static void validatePhoneNumber(String phoneNumber)
		throws InvalidPhoneNumberException {

		if (!ValidatorUtil.isValidPhoneNumber(phoneNumber)) {
			throw new InvalidPhoneNumberException();
		}
	}

	private static UserDAO _userDAO;

}