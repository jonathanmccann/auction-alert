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

/**
 * @author Jonathan McCann
 */
public class PropertiesValues {

	public static final String APPLICATION_ID =
		PropertiesUtil.getConfigurationProperty(PropertiesKeys.APPLICATION_ID);

	public static final String EBAY_TOKEN =
		PropertiesUtil.getConfigurationProperty(PropertiesKeys.EBAY_TOKEN);

	public static final String JDBC_DEFAULT_DRIVER_CLASS_NAME =
		PropertiesUtil.getConfigurationProperty(
			PropertiesKeys.JDBC_DEFAULT_DRIVER_CLASS_NAME);

	public static final String JDBC_DEFAULT_PASSWORD =
		PropertiesUtil.getConfigurationProperty(
			PropertiesKeys.JDBC_DEFAULT_PASSWORD);

	public static final String JDBC_DEFAULT_URL =
		PropertiesUtil.getConfigurationProperty(
			PropertiesKeys.JDBC_DEFAULT_URL);

	public static final String JDBC_DEFAULT_USERNAME =
		PropertiesUtil.getConfigurationProperty(
			PropertiesKeys.JDBC_DEFAULT_USERNAME);

	public static final String MAIL_SMTP_AUTH =
		PropertiesUtil.getConfigurationProperty(PropertiesKeys.MAIL_SMTP_AUTH);

	public static final String MAIL_SMTP_HOST =
		PropertiesUtil.getConfigurationProperty(PropertiesKeys.MAIL_SMTP_HOST);

	public static final String MAIL_SMTP_PORT =
		PropertiesUtil.getConfigurationProperty(PropertiesKeys.MAIL_SMTP_PORT);

	public static final String MAIL_SMTP_STARTTLS_ENABLE =
		PropertiesUtil.getConfigurationProperty(
			PropertiesKeys.MAIL_SMTP_STARTTLS_ENABLE);

	public static final int NUMBER_OF_SEARCH_RESULTS =
		Integer.parseInt(
			PropertiesUtil.getConfigurationProperty(
				PropertiesKeys.NUMBER_OF_SEARCH_RESULTS));

	public static final String OUTBOUND_EMAIL_ADDRESS =
		PropertiesUtil.getConfigurationProperty(
			PropertiesKeys.OUTBOUND_EMAIL_ADDRESS);

	public static final String OUTBOUND_EMAIL_ADDRESS_PASSWORD =
		PropertiesUtil.getConfigurationProperty(
			PropertiesKeys.OUTBOUND_EMAIL_ADDRESS_PASSWORD);

	public static final int TOTAL_NUMBER_OF_PREVIOUS_SEARCH_RESULT_IDS =
		Integer.parseInt(
			PropertiesUtil.getConfigurationProperty(
				PropertiesKeys.TOTAL_NUMBER_OF_PREVIOUS_SEARCH_RESULT_IDS));

	public static final int TOTAL_NUMBER_OF_SEARCH_QUERIES_ALLOWED =
		Integer.parseInt(
			PropertiesUtil.getConfigurationProperty(
				PropertiesKeys.TOTAL_NUMBER_OF_SEARCH_QUERIES_ALLOWED));

}