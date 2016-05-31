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

package com.app.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Jonathan McCann
 */
public class MailUtil {

	protected static String getCurrentDate() {
		DateFormat dateFormat = _DATE_FORMAT.get();

		return dateFormat.format(new Date());
	}

	protected static Template getEmailTemplate() throws IOException {
		if (null != _emailTemplate) {
			return _emailTemplate;
		}

		Resource resource = new ClassPathResource("/template");

		_configuration.setDirectoryForTemplateLoading(resource.getFile());

		_emailTemplate = _configuration.getTemplate("/email_body.ftl");

		return _emailTemplate;
	}

	private static final ThreadLocal<DateFormat> _DATE_FORMAT =
		new ThreadLocal<DateFormat>() {

			@Override
			protected DateFormat initialValue() {
				return new SimpleDateFormat("MM/dd/yyyy");
			}

		};

	private static final Configuration _configuration = new Configuration(
		Configuration.VERSION_2_3_21);

	private static Template _emailTemplate;

}