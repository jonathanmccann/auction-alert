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
import com.app.model.User;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * @author Jonathan McCann
 */
@Service
public class UserUtil {

	public static User getUserByEmailAddress(String emailAddress)
		throws DatabaseConnectionException, SQLException {

		return _userDAO.getUserByEmailAddress(emailAddress);
	}

	public static void register(String email, String plainTextPassword)
		throws Exception {

		User user = new User();

		user.setEmailAddress(email);

		generatePassword(user, plainTextPassword);

		_userDAO.addUser(email, user.getPassword(), user.getSalt());
	}

	private static void generatePassword(User user, String plainTextPassword) {
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();

		Object salt = rng.nextBytes();

		String hashedPasswordBase64 = new Sha512Hash(
			plainTextPassword, salt, 1024).toBase64();

		user.setPassword(hashedPasswordBase64);
		user.setSalt(salt.toString());
	}

	@Autowired
	public void setUserDAO(UserDAO userDAO) {
		_userDAO = userDAO;
	}

	private static UserDAO _userDAO;

}