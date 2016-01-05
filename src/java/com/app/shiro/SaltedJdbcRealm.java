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

package com.app.shiro;

import com.app.model.User;
import com.app.util.UserUtil;
import com.app.util.ValidatorUtil;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class SaltedJdbcRealm extends JdbcRealm {

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token)
		throws AuthenticationException {

		UsernamePasswordToken userPassToken = (UsernamePasswordToken)token;

		final String username = userPassToken.getUsername();

		if (ValidatorUtil.isNull(username)) {
			_log.error("Username is null");

			return null;
		}

		try {
			User user = UserUtil.getUserByEmailAddress(username);

			if (user == null) {
				_log.error("No account found for username: {}", username);

				return null;
			}

			return new eBaySaltedAuthenticationInfo(
				username, user.getPassword(), user.getSalt());
		}
		catch (Exception e) {
			throw new AuthenticationException(e);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SaltedJdbcRealm.class);

}