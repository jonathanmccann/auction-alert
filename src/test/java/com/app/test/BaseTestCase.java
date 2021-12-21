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

import com.app.model.Category;
import com.app.util.CategoryUtil;
import com.app.util.DatabaseUtil;
import com.app.util.PropertiesUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DelegatingSubject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.util.ThreadState;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import javax.mail.Message;
import javax.mail.Transport;

/**
 * @author Jonathan McCann
 */
@PowerMockIgnore("javax.net.ssl.*")
public abstract class BaseTestCase {

	protected static void _addCategory(
			String categoryId, String categoryName, String categoryParentId,
			int categoryLevel)
		throws Exception {

		List<Category> categories = new ArrayList<>();

		Category category = new Category(
			categoryId, categoryName, categoryParentId, categoryLevel);

		categories.add(category);

		CategoryUtil.addCategories(categories);
	}

	protected static Message assertTransportCalled(int times)
		throws Exception {

		if (times == 0) {
			PowerMockito.verifyStatic(Mockito.never());

			Transport.send(Mockito.anyObject());

			return null;
		}
		else {
			PowerMockito.verifyStatic(Mockito.times(1));

			ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(
				Message.class);

			Transport.send(argumentCaptor.capture());

			return argumentCaptor.getValue();
		}
	}

	protected static void setUpDatabase() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");

		setUpDatabaseProperties();

		Resource resource = new ClassPathResource(_TEST_DATABASE_PATH);

		ScriptUtils.executeSqlScript(
			DatabaseUtil.getDatabaseConnection(), resource);
	}

	protected static void setUpDatabaseProperties() throws Exception {
		String databasePassword = System.getProperty("jdbc.default.password");
		String databaseURL = System.getProperty("jdbc.default.url");
		String databaseUsername = System.getProperty("jdbc.default.username");

		DatabaseUtil.setDatabaseProperties(
			databaseURL, databaseUsername, databasePassword);
	}

	protected static void setUpInvalidDatabaseProperties() throws Exception {
		DatabaseUtil.setDatabaseProperties(null, null, null);
	}

	protected static void setUpProperties() throws Exception {
		Class<?> clazz = BaseTestCase.class;

		URL resource = clazz.getResource("/test-config.properties");

		PropertiesUtil.loadConfigurationProperties(resource.getPath());
	}

	protected static void setUpSecurityUtilsSession(int userId)
		throws Exception {

		setUpSecurityUtilsSession(true, userId);
	}

	protected static void setUpSecurityUtilsSession(
			boolean authenticated, int userId)
		throws Exception {

		DefaultSecurityManager defaultSecurityManager =
			new DefaultSecurityManager();

		ThreadContext.bind(defaultSecurityManager);

		Session session = new SimpleSession();

		session.setAttribute("userId", userId);

		Subject subject = new DelegatingSubject(
			null, authenticated, null, session, defaultSecurityManager);

		ThreadState threadState = new SubjectThreadState(subject);

		threadState.bind();
	}

	protected static void setUpSecurityUtilsSession(
			int userId, int loginAttempts)
		throws Exception {

		DelegatingSubject delegatingSubject = Mockito.mock(
			DelegatingSubject.class);

		DefaultSecurityManager defaultSecurityManager =
			new DefaultSecurityManager();

		ThreadContext.bind(defaultSecurityManager);

		Session session = new SimpleSession();

		session.setAttribute("userId", userId);
		session.setAttribute("loginAttempts", loginAttempts);

		ThreadState threadState = new SubjectThreadState(delegatingSubject);

		threadState.bind();

		Mockito.doNothing().when(
			delegatingSubject
		).login(
			Mockito.any(AuthenticationToken.class)
		);

		Mockito.when(
			delegatingSubject.isAuthenticated()
		).thenReturn(
			false
		);

		Mockito.when(
			delegatingSubject.getSession()
		).thenReturn(
			session
		);
	}

	protected static void setUpSecurityUtilsSessionWithException(
			int userId, Exception e)
		throws Exception {

		DelegatingSubject delegatingSubject = Mockito.mock(
			DelegatingSubject.class);

		DefaultSecurityManager defaultSecurityManager =
			new DefaultSecurityManager();

		ThreadContext.bind(defaultSecurityManager);

		Session session = new SimpleSession();

		session.setAttribute("userId", userId);

		ThreadState threadState = new SubjectThreadState(delegatingSubject);

		threadState.bind();

		Mockito.doThrow(
			e
		).when(
			delegatingSubject
		).login(
			Mockito.any(AuthenticationToken.class)
		);

		Mockito.when(
			delegatingSubject.isAuthenticated()
		).thenReturn(
			false
		);

		Mockito.when(
			delegatingSubject.getSession()
		).thenReturn(
			session
		);
	}

	protected static void setUpTransport() throws Exception {
		PowerMockito.spy(Transport.class);

		PowerMockito.doNothing().when(
			Transport.class, "send", Mockito.anyObject()
		);
	}

	protected static void _initializeVelocityTemplate(
			Class clazz, Object classInstance)
		throws Exception {

		Field velocityEngine = clazz.getDeclaredField("_velocityEngine");

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

	protected static final double _GBP_TO_USD = 0.2;

	protected static final double _USD_TO_CAD = 2.0;

	protected static final double _USD_TO_GBP = 5.0;

	protected static final int _CATEGORY_LEVEL = 1;

	protected static final int _INVALID_USER_ID = -1;

	protected static final int _USER_ID = 1;

	protected static final String _CATEGORY_ID = "categoryId";

	protected static final String _CATEGORY_NAME = "categoryName";

	protected static final String _CATEGORY_PARENT_ID = "categoryParentId";

	protected static final String _CATEGORY_RELEASE_NAME = "category";

	protected static final String _ACCOUNT_DELETION_EMAIL = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">We're sorry to see you go</h2>\n" +
		"\n" +
		"\t\t\t\t\t<p style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em; width: 75%\">\n" +
		"\t\t\t\t\t\tYour account deletion request has been processed successfully. We hope you enjoyed your time at Auction Alert.<br><br>\n" +
		"\t\t\t\t\t\tPlease resubscribe at any time to enjoy its benefits once again.\n" +
		"\t\t\t\t\t</p>\n" +
		"\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

	protected static final String _CANCELLATION_EMAIL = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">We're sorry to see you go</h2>\n" +
		"\n" +
		"\t\t\t\t\t<p style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em; width: 75%\">\n" +
		"\t\t\t\t\t\tYour cancellation request has been processed successfully. We hope you enjoyed your time at Auction Alert.<br><br>\n" +
		"\t\t\t\t\t\tPlease resubscribe at any time to enjoy its benefits once again.\n" +
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
		"\t\t\t\t\t<p style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em; width: 75%\">\n" +
		"\t\t\t\t\t\tYour card details have been successfully updated.<br><br>\n" +
		"\t\t\t\t\t\tPlease look for the charge to appear on the new card starting on your next billing cycle.\n" +
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
		"\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<br> <br>\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tFixed Price: $29.99\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
		"\t\t\t\t\t\t\t\t\t</td>\n" +
		"\t\t\t\t\t\t\t\t</tr>\n" +
		"\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
		"\t\t\t\t\t\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t<a style=\"display: block;\" href=\"http://www.test.com/my_account\">Unsubscribe</a>\n" +
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
		"\t\t\t\t\t<p style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em; width: 75%\">\n" +
		"\t\t\t\t\t\tYour resubscription request has been processed successfully.<br><br>\n" +
		"\t\t\t\t\t\tStart enjoying the benefits of <a href=\"http://www.test.com/add_search_query\">email alerts</a> immediately.\n" +
		"\t\t\t\t\t</p>\n" +
		"\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

	protected static final String _PAYMENT_FAILED_EMAIL = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">Payment Failed</h2>\n" +
		"\n" +
		"\t\t\t\t\t<p style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em;\">\n" +
		"\t\t\t\t\t\tWe were unable to process your payment for your monthly subscription. Please visit <a href=\"http://www.test.com/my_account\">your account</a> to update your credit card.<br><br>\n" +
		"\t\t\t\t\t\tAs of now, your account is inactive and you will no longer receive any email notifications.\n" +
		"\t\t\t\t\t</p>\n" +
		"\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t</footer>\n" +
		"\t</body>\n" +
		"</html>";

	protected static final String _PASSWORD_RESET_TOKEN_EMAIL = "<html>\n" +
		"\t<body>\n" +
		"\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size: 16px\">\n" +
		"\t\t\t<tr>\n" +
		"\t\t\t\t<td align=\"center\">\n" +
		"\t\t\t\t\t<h2 style=\"color: #666f77; font-weight: 300; line-height: 1em; margin: 0 0 1em 0; text-transform: uppercase; letter-spacing: 0.125em; font-size: 1.5em; line-height: 1.5em;\">Auction Alert</h2>\n" +
		"\n" +
		"\t\t\t\t\tYour requested password reset token is: <br> <br>\n" +
		"\n" +
		"\t\t\t\t\tSample password reset token <br> <br>\n" +
		"\n" +
		"\t\t\t\t\tPlease use this token to <a href=\"http://www.test.com/reset_password\">reset your password</a>. This token will be valid for one hour. <br>\n" +
		"\n" +
		"\t\t\t\t\tIf you did not request this password reset token, please contact the administrator immediately. <br>\n" +
		"\t\t\t\t</td>\n" +
		"\t\t\t</tr>\n" +
		"\t\t</table>\n" +
		"\t\t<footer style=\"background: #f8f8f8; padding: 4em 0 6em 0; text-align: center; color: #bbb\">\n" +
		"\t\t\t<p style=\"text-align: center\">\n" +
		"\t\t\t\t© <a href=\"http://www.test.com\">Auction Alert</a>. All rights reserved.\n" +
		"\t\t\t</p>\n" +
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
		"\t\t\t\t\t<p style=\"color: #666f77; font-weight: 300; line-height: 0.5em; margin: 0 0 1em 0; letter-spacing: 0.125em; font-size: 1.10em; line-height: 1.5em;\">\n" +
		"\t\t\t\t\t\tYou can begin by <a href=\"http://www.test.com/add_search_query\">adding search queries</a> and start receiving updates right to your email.<br><br>\n" +
		"\t\t\t\t\t\tLearn more about the site in our <a href=\"http://www.test.com/faq\">FAQ</a> and feel free to <a href=\"http://www.test.com/contact\">contact us</a> with any questions.\n" +
		"\t\t\t\t\t</p>\n" +
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