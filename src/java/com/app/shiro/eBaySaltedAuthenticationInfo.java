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

import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

/**
 * @author Jonathan McCann
 */
public class eBaySaltedAuthenticationInfo implements SaltedAuthenticationInfo {

	public eBaySaltedAuthenticationInfo(
		String emailAddress, String password, String salt) {

		_emailAddress = emailAddress;
		_password = password;
		_salt = salt;
	}

	@Override
	public Object getCredentials() {
		return _password;
	}

	@Override
	public ByteSource getCredentialsSalt() {
		return new SimpleByteSource(Base64.decode(_salt));
	}

	@Override
	public PrincipalCollection getPrincipals() {
		return new SimplePrincipalCollection(_emailAddress, _emailAddress);
	}

	private final String _emailAddress;
	private final String _password;
	private final String _salt;

}