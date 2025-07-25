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
		PropertiesUtil.getConfigurationProperty("application.id");

	public static final String EBAY_CAMPAIGN_ID =
		PropertiesUtil.getConfigurationProperty("ebay.campaign.id");

	public static final String EBAY_PUBLISHER_ID =
		PropertiesUtil.getConfigurationProperty("ebay.publisher.id");

	public static final String EBAY_TOKEN =
		PropertiesUtil.getConfigurationProperty("ebay.token");

	public static final String JDBC_DEFAULT_PASSWORD =
		PropertiesUtil.getConfigurationProperty("jdbc.default.password");

	public static final String JDBC_DEFAULT_URL =
		PropertiesUtil.getConfigurationProperty("jdbc.default.url");

	public static final String JDBC_DEFAULT_USERNAME =
		PropertiesUtil.getConfigurationProperty("jdbc.default.username");

	public static final int LOGIN_ATTEMPT_LIMIT = Integer.parseInt(
		PropertiesUtil.getConfigurationProperty("login.attempt.limit"));

	public static final String MAIL_SMTP_AUTH =
		PropertiesUtil.getConfigurationProperty("mail.smtp.auth");

	public static final String MAIL_SMTP_HOST =
		PropertiesUtil.getConfigurationProperty("mail.smtp.host");

	public static final String MAIL_SMTP_PORT =
		PropertiesUtil.getConfigurationProperty("mail.smtp.port");

	public static final String MAIL_SMTP_SOCKETFACTORY_CLASS =
		PropertiesUtil.getConfigurationProperty("mail.smtp.socketFactory.class");

	public static final String MAIL_SMTP_SSL_PROTOCOLS =
		PropertiesUtil.getConfigurationProperty("mail.smtp.ssl.protocols");

	public static final String MAIL_SMTP_STARTTLS_ENABLE =
		PropertiesUtil.getConfigurationProperty("mail.smtp.starttls.enable");

	public static final String MAIL_SMTP_STARTTLS_REQUIRED =
		PropertiesUtil.getConfigurationProperty("mail.smtp.starttls.required");

	public static final int MAXIMUM_NUMBER_OF_SEARCH_RESULTS = Integer.parseInt(
		PropertiesUtil.getConfigurationProperty(
			"maximum.number.of.search.results"));

	public static final int MAXIMUM_NUMBER_OF_SEARCH_QUERIES = Integer.parseInt(
		PropertiesUtil.getConfigurationProperty(
			"maximum.number.of.search.queries"));

	public static final int MAXIMUM_NUMBER_OF_USERS = Integer.parseInt(
		PropertiesUtil.getConfigurationProperty("maximum.number.of.users"));

	public static final int NUMBER_OF_EMAILS_PER_DAY = Integer.parseInt(
		PropertiesUtil.getConfigurationProperty("number.of.emails.per.day"));

	public static final int NUMBER_OF_SEARCH_RESULTS = Integer.parseInt(
		PropertiesUtil.getConfigurationProperty("number.of.search.results"));

	public static final String OUTBOUND_EMAIL_ADDRESS =
		PropertiesUtil.getConfigurationProperty("outbound.email.address");

	public static final String OUTBOUND_EMAIL_ADDRESS_PASSWORD =
		PropertiesUtil.getConfigurationProperty(
			"outbound.email.address.password");

	public static final String RECAPTCHA_SECRET_KEY =
		PropertiesUtil.getConfigurationProperty("recaptcha.secret.key");

	public static final String RECAPTCHA_SITE_KEY =
		PropertiesUtil.getConfigurationProperty("recaptcha.site.key");

	public static final String ROOT_DOMAIN_NAME =
		PropertiesUtil.getConfigurationProperty("root.domain.name");

	public static final String SENDGRID_API_KEY =
		PropertiesUtil.getConfigurationProperty("sendgrid.api.key");

	public static final String SENDGRID_WEBHOOK_KEY =
		PropertiesUtil.getConfigurationProperty("sendgrid.webhook.key");

	public static final String SENDGRID_WEBHOOK_VALUE =
		PropertiesUtil.getConfigurationProperty("sendgrid.webhook.value");

	public static final String STRIPE_PUBLISHABLE_KEY =
		PropertiesUtil.getConfigurationProperty("stripe.publishable.key");

	public static final String STRIPE_SECRET_KEY =
		PropertiesUtil.getConfigurationProperty("stripe.secret.key");

	public static final String STRIPE_SIGNING_SECRET =
		PropertiesUtil.getConfigurationProperty("stripe.signing.secret");

	public static final String STRIPE_SUBSCRIPTION_PLAN_ID =
		PropertiesUtil.getConfigurationProperty("stripe.subscription.plan.id");

}