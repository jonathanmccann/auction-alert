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

package com.app.exception;

/**
 * @author Jonathan McCann
 */
public class DatabaseConnectionException extends Exception {

	public DatabaseConnectionException() {
	}

	public DatabaseConnectionException(String msg) {
		super(msg);
	}

	public DatabaseConnectionException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DatabaseConnectionException(Throwable cause) {
		super(cause);
	}

}