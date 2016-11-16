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

package com.app.language;

import java.text.MessageFormat;

import java.util.ResourceBundle;

/**
 * @author Jonathan McCann
 */
public class LanguageUtil {

	public static String formatMessage(
		String messageKey, Object... parameters) {

		return MessageFormat.format(
			_resourceBundle.getString(messageKey), parameters);
	}

	public static String getMessage(String messageKey) {
		return _resourceBundle.getString(messageKey);
	}

	private static final ResourceBundle _resourceBundle =
		ResourceBundle.getBundle("language");

}