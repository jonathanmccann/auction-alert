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

package com.app.test;

import com.app.mail.MailSender;
import com.app.mail.MailSenderFactory;
import com.app.mail.SendGridMailSender;
import com.app.util.DatabaseUtil;
import com.app.util.PropertiesUtil;
import com.app.util.UserUtil;
import com.app.util.EbayAPIUtil;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

/**
 * @author Jonathan McCann
 */
@PrepareForTest({MailSenderFactory.class, SecurityUtils.class, UserUtil.class})
@RunWith(PowerMockRunner.class)
@WebAppConfiguration
public abstract class BaseTestCase {

	protected static void setUpApiContext() {
		EbayAPIUtil.loadApiContext("ebay.token");
	}

	protected static void setUpDatabase() throws Exception {
		String databasePassword = System.getProperty("jdbc.default.password");
		String databaseURL = System.getProperty("jdbc.default.url");
		String databaseUsername = System.getProperty("jdbc.default.username");

		DatabaseUtil.setDatabaseProperties(
			databaseURL, databaseUsername, databasePassword);

		Resource resource = new ClassPathResource(_TEST_DATABASE_PATH);

		ScriptUtils.executeSqlScript(
			DatabaseUtil.getDatabaseConnection(), resource);
	}

	protected static void setUpInvalidUserUtil() throws Exception {
		PowerMockito.spy(UserUtil.class);

		PowerMockito.doReturn(
			_INVALID_USER_ID
		).when(
			UserUtil.class, "getCurrentUserId"
		);
	}

	protected void setUpMailSender() throws Exception {
		MailSender mockMailSender = Mockito.mock(MailSender.class);

		Mockito.doNothing().when(
			mockMailSender
		).sendCancellationMessage(
			Mockito.anyString()
		);

		Mockito.doNothing().when(
			mockMailSender
		).sendContactMessage(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.doNothing().when(
			mockMailSender
		).sendPasswordResetToken(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.doNothing().when(
			mockMailSender
		).sendWelcomeMessage(
			Mockito.anyString()
		);

		PowerMockito.spy(MailSenderFactory.class);

		Mockito.when(
			MailSenderFactory.getInstance()
		).thenReturn(
			mockMailSender
		);
	}

	protected static void setUpProperties() throws Exception {
		Class<?> clazz = BaseTestCase.class;

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());
	}

	protected static void setUpSecurityUtils(boolean authenticated)
		throws Exception {

		PowerMockito.spy(SecurityUtils.class);

		DefaultSecurityManager defaultSecurityManager =
			new DefaultSecurityManager();

		Subject subject = new DelegatingSubject(
			null, authenticated, null, null, defaultSecurityManager);

		PowerMockito.doReturn(
			subject
		).when(
			SecurityUtils.class, "getSubject"
		);
	}

	protected static void setUpServiceClient() {
		EbayAPIUtil.loadEbayServiceClient(System.getProperty("application.id"));
	}

	protected static void setUpUserUtil() throws Exception {
		PowerMockito.spy(UserUtil.class);

		PowerMockito.doReturn(
			_USER_ID
		).when(
			UserUtil.class, "getCurrentUserId"
		);

		PowerMockito.doReturn(
			true
		).when(
			UserUtil.class, "isCurrentUserActive"
		);
	}

	protected static void _initializeVelocityTemplate(
			Class clazz, Object classInstance)
		throws Exception {

		Field velocityEngine = clazz.getDeclaredField("velocityEngine");

		velocityEngine.setAccessible(true);

		VelocityEngineFactoryBean velocityEngineFactoryBean =
			new VelocityEngineFactoryBean();

		Map<String, Object> velocityPropertiesMap = new HashMap<>();

		velocityPropertiesMap.put("resource.loader", "class");
		velocityPropertiesMap.put(
			"class.resource.loader.class",
			"org.apache.velocity.runtime.resource.loader." +
				"ClasspathResourceLoader");

		velocityEngineFactoryBean.setVelocityPropertiesMap(
			velocityPropertiesMap);

		velocityEngine.set(
			classInstance, velocityEngineFactoryBean.createVelocityEngine());
	}

	protected static final int _INVALID_USER_ID = 2;

	protected static final int _USER_ID = 1;

	protected static final String _CANCELLATION_EMAIL = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">We're sorry to see you go</h2>\n" +
		"\n" +
		"\t\t\t\t\t<p style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em;\">\n" +
		"\t\t\t\t\t\tYour cancellation request has been processed successfully. We hope you enjoyed your time at Auction Alert. Please resubscribe at any time to enjoy its benefits once again.\n" +
		"\t\t\t\t\t</p>\n" +
		"\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

	protected static final String _CARD_DETAILS_EMAIL = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">Card Details Updated</h2>\n" +
		"\n" +
		"\t\t\t\t\t<p style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em;\">\n" +
		"\t\t\t\t\t\tYour card details have been successfully updated. Please look for the charge to appear on the new card starting on your next billing cycle.\n" +
		"\t\t\t\t\t</p>\n" +
		"\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

	protected static final String _EMAIL_CONTENT = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">Auction Alert</h2>\n" +
		"\n" +
		"\t\t\t\t\t\t\t\t\t\t\t<table>\n" +
		"\t\t\t\t\t\t\t\n" +
		"\t\t\t\t\t\t\t<h3 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.25em; line-height: 1.5em; text-align: center;\">Test keywords</h3>\n" +
		"\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
		"\t\t\t\t\t\t\t\t\t<td>\n" +
		"\t\t\t\t\t\t\t\t\t\t<div style=\"display: inline-block; padding: 10px; width: 140px;\">\n" +
		"\t\t\t\t\t\t\t\t\t\t\t<img alt=\"http://www.ebay.com/123.jpg\" src=\"http://www.ebay.com/123.jpg\">\n" +
		"\t\t\t\t\t\t\t\t\t\t</div>\n" +
		"\n" +
		"\t\t\t\t\t\t\t\t\t\t<div style=\"display: inline-block; text-align: left; vertical-align: top;\">\n" +
		"\t\t\t\t\t\t\t\t\t\t\t<a href=\"http://www.ebay.com/itm/1234\">itemTitle</a> <br> <br>\n" +
		"\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tAuction Price: $14.99\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tFixed Price: $29.99\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
		"\t\t\t\t\t\t\t\t\t</td>\n" +
		"\t\t\t\t\t\t\t\t</tr>\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
		"\t\t\t\t\t\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t<a style=\"display: block;\" href=\"http://www.test.com/email_unsubscribe?emailAddress=test@test.com&unsubscribeToken=unsubscribeToken\">Unsubscribe</a>\n" +
		"\t\t\t<p style=\"text-align: center\">\n" +
		"\t\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t\t</p>\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

	protected static final String _RESUBSCRIBE_EMAIL = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">Welcome Back</h2>\n" +
		"\n" +
		"\t\t\t\t\t<p style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em;\">\n" +
		"\t\t\t\t\t\tYour resubscription request has been processed successfully. Start enjoying the benefits of email alerts and real time monitoring immediately.\n" +
		"\t\t\t\t\t</p>\n" +
		"\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

	protected static final String _WELCOME_EMAIL = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">Welcome to Auction Alert</h2>\n" +
		"\n" +
		"\t\t\t\t\t<h3 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">What can you do now?</h3>\n" +
		"\n" +
		"\t\t\t\t\t<ul style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em;\">\n" +
		"\t\t\t\t\t\t<li><a href=\"http://www.test.com/add_search_query\">Start adding search queries</a></li>\n" +
		"\t\t\t\t\t\t<li><a href=\"http://www.test.com/monitor\">Monitor a favorite search in real time</a></li>\n" +
		"\t\t\t\t\t\t<li><a href=\"http://www.test.com/faq\">Learn more in the FAQ</a></li>\n" +
		"\t\t\t\t\t</ul>\n" +
		"\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

	private static final String _TEST_DATABASE_PATH = "/sql/testdb.sql";

}