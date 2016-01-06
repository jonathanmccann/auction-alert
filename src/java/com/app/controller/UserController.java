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

package com.app.controller;


import com.app.exception.DatabaseConnectionException;
import com.app.model.User;
import com.app.util.UserUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author Jonathan McCann
 */
@Controller
public class UserController {

	@RequestMapping(value = "/log_in", method = RequestMethod.POST)
	public String logIn(
			String emailAddress, String password, Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		Subject currentUser = SecurityUtils.getSubject();

		if (!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(
				emailAddress, password);

			try {
				currentUser.login(token);

				_log.debug(
					"User with email address {} logged in successfully",
					emailAddress);

				User user = UserUtil.getUserByEmailAddress(emailAddress);

				currentUser.getSession().setAttribute(
					"userId", user.getUserId());
			}
			catch (Exception e) {
				model.put(
					"authenticationError",
					"Authentication failed. Please try again.");

				if (e instanceof UnknownAccountException) {
					_log.error(
						"There is no user with emailAddress {}",
						token.getPrincipal());
				}
				else if (e instanceof IncorrectCredentialsException) {
					_log.error(
						"Password for emailAddress {} is incorrect",
						token.getPrincipal());
				}
				else if (e instanceof LockedAccountException) {
					_log.error(
						"The account associated with emailAddress {} is locked",
						token.getPrincipal());
				}
			}
		}

		return "home";
	}

	private static final Logger _log = LoggerFactory.getLogger(
		UserController.class);

}