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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class SendGridUtil {

	public static void handleSendGridEvent(
		String sendGridJsonEvent, String key, String value) {

		if (ValidatorUtil.isNull(key) || ValidatorUtil.isNull(value) ||
			!key.equals(PropertiesValues.SENDGRID_WEBHOOK_KEY) ||
			!value.equals(PropertiesValues.SENDGRID_WEBHOOK_VALUE)) {

			_log.error("Attempt to access SendGrid event webhook endpoint");

			return;
		}

		_log.error("sendGridJsonEvent = {}", sendGridJsonEvent);
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SendGridUtil.class);

}