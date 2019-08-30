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

package com.app.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.app.language.LanguageUtil;
import com.app.model.SearchQuery;
import com.app.model.SearchResult;
import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.ConstantsUtil;
import com.app.util.PropertiesValues;
import com.app.util.RecaptchaUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;
import com.app.util.UserUtil;

import com.stripe.exception.APIConnectionException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSubscriptionCollection;
import com.stripe.model.DeletedCustomer;
import com.stripe.model.Event;
import com.stripe.model.EventData;
import com.stripe.model.Subscription;

import com.stripe.net.Webhook;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Message;
import javax.mail.Transport;
import javax.net.ssl.HttpsURLConnection;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest({
	Customer.class, RecaptchaUtil.class, Subscription.class, URL.class,
	Webhook.class
})
public class UserControllerTest extends BaseTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpProperties();

		setUpDatabase();

		ConstantsUtil.init();
	}

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		//_FIRST_USER = UserUtil.addUser("test@test.com", "password");
	}

	@After
	public void tearDown() throws Exception {
		if (_FIRST_USER != null) {
			UserUtil.deleteUserByUserId(_FIRST_USER.getUserId());
		}

		if (_SECOND_USER != null) {
			UserUtil.deleteUserByUserId(_SECOND_USER.getUserId());
		}
	}

	@Test
	public void testCreateAccount() throws Exception {
		setUpCustomer();
		setUpSecurityUtilsSubject(true);
		setUpTransport();

		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:home"));
		resultActions.andExpect(redirectedUrl("home"));

		_SECOND_USER = UserUtil.getUserByEmailAddress("test@test.com");

		Assert.assertEquals("testCustomerId", _SECOND_USER.getCustomerId());
		Assert.assertEquals("testSubscriptionId", _SECOND_USER.getSubscriptionId());

		assertTransportCalled(1);
	}

	@Test
	public void testCreateAccountExceedingMaximumNumberOfUsers()
		throws Exception {

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");
		_SECOND_USER = UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("emailAddress", "test3@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:create_account"));
		resultActions.andExpect(redirectedUrl("create_account"));
	}

	@Test
	public void testCreateAccountWithDuplicateEmailAddress() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("emailAddress", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:create_account"));
		resultActions.andExpect(redirectedUrl("create_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("duplicate-email-address")));
	}

	@Test
	public void testCreateAccountWithInvalidEmailAddress() throws Exception {
		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("emailAddress", "test");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:create_account"));
		resultActions.andExpect(redirectedUrl("create_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("invalid-email-address")));
	}

	@Test
	public void testCreateAccountWithInvalidPassword() throws Exception {
		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("emailAddress", "test@test.com");
		request.param("password", "");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:create_account"));
		resultActions.andExpect(redirectedUrl("create_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("invalid-password-length")));
	}

	@Test
	public void testCreateAccountWithInvalidStripeToken()
		throws Exception {

		setUpInvalidCustomer();

		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:create_account"));
		resultActions.andExpect(redirectedUrl("create_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("incorrect-payment-information")));
	}

	@Test
	public void testDeleteSubscription() throws Exception {
		setUpSubscription();
		setUpTransport();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("success"));
		resultActions.andExpect(flash().attribute(
			"success", LanguageUtil.getMessage("subscription-updated")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());

		assertTransportCalled(1);
	}

	@Test
	public void testDeleteSubscriptionWithNullSubscriptionId()
		throws Exception {

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", null, true,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("subscription-already-cancelled")));

		User user = UserUtil.getCurrentUser();

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertNull(user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testDeleteSubscriptionWithInactiveUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", false,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("subscription-already-cancelled")));

		User user = UserUtil.getCurrentUser();

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertFalse(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testDeleteSubscriptionWithPendingCancellation()
		throws Exception {

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			true);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("subscription-already-cancelled")));

		User user = UserUtil.getCurrentUser();

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());
	}

	@Test
	public void testDeleteSubscriptionWithCancellationException()
		throws Exception {

		setUpInvalidSubscription();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("subscription-cancellation")));

		User user = UserUtil.getCurrentUser();

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testGetDeleteAccountWithActiveUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		this.mockMvc.perform(get("/delete_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("delete_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/delete_account.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true))
			.andExpect(model().attributeExists("recaptchaSiteKey"))
			.andExpect(model().attribute(
				"recaptchaSiteKey", PropertiesValues.RECAPTCHA_SITE_KEY));
	}

	@Test
	public void testGetDeleteAccountWithInactiveUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		this.mockMvc.perform(get("/delete_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("delete_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/delete_account.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false))
			.andExpect(model().attributeExists("recaptchaSiteKey"))
			.andExpect(model().attribute(
				"recaptchaSiteKey", PropertiesValues.RECAPTCHA_SITE_KEY));
	}

	@Test
	public void testDeleteAccount() throws Exception {
		setUpCustomer();
		setUpRecaptchaUtil(true);

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_FIRST_USER.getUserId());
		searchQuery.setKeywords("Test keywords");
		searchQuery.setActive(true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchResult searchResult = new SearchResult(
			searchQueryId, "1234", "First Item", "$10.00", "$14.99",
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		List<SearchResult> searchResults = new ArrayList<>();

		searchResults.add(searchResult);

		SearchResultUtil.addSearchResults(searchQueryId, searchResults);

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user);

		MockHttpServletRequestBuilder request = post("/delete_account");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");
		request.param("g-recaptcha-response", "recaptchaResponse");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:log_out"));
		resultActions.andExpect(redirectedUrl("log_out"));

		user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNull(user);

		try {
			SearchQueryUtil.getSearchQuery(searchQueryId);
		}
		catch (SQLException sqle) {
			Assert.assertEquals(SQLException.class, sqle.getClass());
		}

		searchResults = SearchResultUtil.getSearchQueryResults(searchQueryId);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteAccountWithIncorrectEmailAddress() throws Exception {
		setUpRecaptchaUtil(true);

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = post("/delete_account");

		request.param("emailAddress", "incorrect@test.com");
		request.param("password", "password");
		request.param("g-recaptcha-response", "recaptchaResponse");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:delete_account"));
		resultActions.andExpect(redirectedUrl("delete_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("user-deletion-failure")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user);
	}

	@Test
	public void testDeleteAccountWithIncorrectPassword() throws Exception {
		setUpRecaptchaUtil(true);

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = post("/delete_account");

		request.param("emailAddress", "test@test.com");
		request.param("password", "incorrectPassword");
		request.param("g-recaptcha-response", "recaptchaResponse");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:delete_account"));
		resultActions.andExpect(redirectedUrl("delete_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("user-deletion-failure")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user);
	}

	@Test
	public void testDeleteAccountWithInvalidRecaptcha() throws Exception {
		setUpRecaptchaUtil(false);

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		MockHttpServletRequestBuilder request = post("/delete_account");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");
		request.param("g-recaptcha-response", "recaptchaResponse");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:delete_account"));
		resultActions.andExpect(redirectedUrl("delete_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("recaptcha-failure")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user);
	}

	@Test
	public void testGetContactWithAuthenticatedAndActiveUser()
		throws Exception {

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		this.mockMvc.perform(get("/contact"))
			.andExpect(status().isOk())
			.andExpect(view().name("contact"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/contact.jsp"))
			.andExpect(model().attributeExists("emailAddress"))
			.andExpect(model().attribute("emailAddress", "test@test.com"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetContactWithAuthenticatedAndInactiveUser()
		throws Exception {

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		this.mockMvc.perform(get("/contact"))
			.andExpect(status().isOk())
			.andExpect(view().name("contact"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/contact.jsp"))
			.andExpect(model().attributeExists("emailAddress"))
			.andExpect(model().attribute("emailAddress", "test@test.com"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testGetContactWithUnauthenticatedAndActiveUser()
		throws Exception {

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		setUpSecurityUtilsSession(false, _FIRST_USER.getUserId());

		this.mockMvc.perform(get("/contact"))
			.andExpect(status().isOk())
			.andExpect(view().name("contact"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/contact.jsp"))
			.andExpect(model().attributeDoesNotExist("emailAddress"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testGetContactWithUnauthenticatedAndInactiveUser()
		throws Exception {

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(false, _FIRST_USER.getUserId());

		this.mockMvc.perform(get("/contact"))
			.andExpect(status().isOk())
			.andExpect(view().name("contact"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/contact.jsp"))
			.andExpect(model().attributeDoesNotExist("emailAddress"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", false));
	}

	@Test
	public void testGetCreateAccount() throws Exception {
		setUpSecurityUtilsSubject(false);

		this.mockMvc.perform(get("/create_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("create_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/create_account.jsp"))
			.andExpect(model().attributeExists("error"))
			.andExpect(model().attribute("error", ""))
			.andExpect(model().attributeExists("exceedsMaximumNumberOfUsers"))
			.andExpect(model().attribute("exceedsMaximumNumberOfUsers", false))
			.andExpect(model().attributeExists("stripePublishableKey"))
			.andExpect(
				model().attribute(
					"stripePublishableKey", "Stripe Publishable Key"));
	}

	@Test
	public void testGetCreateAccountWithAuthenticatedUser() throws Exception {
		setUpSecurityUtilsSubject(true);

		this.mockMvc.perform(get("/create_account"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account?error="))
			.andExpect(model().attributeExists("error"))
			.andExpect(model().attribute("error", ""))
			.andExpect(
				model().attributeDoesNotExist("exceedsMaximumNumberOfUsers"));
	}

	@Test
	public void testGetForgotPassword() throws Exception {
		this.mockMvc.perform(get("/forgot_password"))
			.andExpect(status().isOk())
			.andExpect(view().name("forgot_password"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/forgot_password.jsp"))
			.andExpect(model().attribute(
				"recaptchaSiteKey", PropertiesValues.RECAPTCHA_SITE_KEY))
			.andExpect(model().attribute("success", ""));
	}

	@Test
	public void testGetHomeWithPendingCancellation() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		setUpSubscription();

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true, true);

		this.mockMvc.perform(get("/home"))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/home.jsp"))
			.andExpect(model().attribute("isActive", true))
			.andExpect(model().attribute(
				"nextChargeDate",
				LanguageUtil.formatMessage(
					"subscription-will-end-on", "December 31")))
			.andExpect(model().attribute(
				"emailsSent", LanguageUtil.formatMessage("x-emails-sent", 0)));
	}

	@Test
	public void testGetHomeWithActiveUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		setUpSubscription();

		this.mockMvc.perform(get("/home"))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/home.jsp"))
			.andExpect(model().attribute("isActive", true))
			.andExpect(model().attribute(
				"nextChargeDate",
				LanguageUtil.formatMessage(
					"next-charge-date", "December 31")))
			.andExpect(model().attribute(
				"emailsSent", LanguageUtil.formatMessage("x-emails-sent", 0)));
	}

	@Test
	public void testGetHomeWithInactiveUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", false,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		setUpSubscription();

		this.mockMvc.perform(get("/home"))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/home.jsp"))
			.andExpect(model().attribute("isActive", false))
			.andExpect(model().attribute(
				"nextChargeDate",
				LanguageUtil.getMessage("inactive-account")))
			.andExpect(model().attribute(
				"emailsSent", LanguageUtil.formatMessage("x-emails-sent", 0)));
	}

	@Test
	public void testGetHomeWithOneEmailSent() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateEmailsSent(_FIRST_USER.getUserId(), 1);

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", false,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		setUpSubscription();

		this.mockMvc.perform(get("/home"))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/home.jsp"))
			.andExpect(model().attribute("isActive", false))
			.andExpect(model().attribute(
				"nextChargeDate",
				LanguageUtil.getMessage("inactive-account")))
			.andExpect(model().attribute(
				"emailsSent", LanguageUtil.getMessage("one-email-sent")));
	}

	@Test
	public void testGetHomeWithUnauthenticatedUser() throws Exception {
		setUpSecurityUtilsSubject(false);

		this.mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/home.jsp"));

		this.mockMvc.perform(get("/home"))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/home.jsp"));
	}

	@Test
	public void testGetLogInWithAuthenticatedUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		setUpSubscription();

		this.mockMvc.perform(get("/log_in"))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/home.jsp"));
	}

	@Test
	public void testGetLogInWithUnauthenticatedUser() throws Exception {
		setUpSecurityUtilsSession(false, _INVALID_USER_ID, 0);

		this.mockMvc.perform(get("/log_in"))
			.andExpect(status().isOk())
			.andExpect(view().name("log_in"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/log_in.jsp"));
	}

	@Test
	public void testGetLogInExceedingLoginAttemptLimit() throws Exception {
		setUpSecurityUtilsSession(
			false, _INVALID_USER_ID, PropertiesValues.LOGIN_ATTEMPT_LIMIT + 1);

		this.mockMvc.perform(get("/log_in"))
			.andExpect(status().isOk())
			.andExpect(view().name("log_in"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/log_in.jsp"))
			.andExpect(model().attribute(
				"recaptchaSiteKey", PropertiesValues.RECAPTCHA_SITE_KEY));
	}

	@Test
	public void testGetLogOut() throws Exception {
		setUpSecurityUtilsSubject(true);

		Subject currentUser = SecurityUtils.getSubject();

		Assert.assertTrue(currentUser.isAuthenticated());

		this.mockMvc.perform(get("/log_out"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:home"))
			.andExpect(redirectedUrl("home"));

		currentUser = SecurityUtils.getSubject();

		Assert.assertFalse(currentUser.isAuthenticated());
	}

	@Test
	public void testGetResetPassword() throws Exception {
		this.mockMvc.perform(get("/reset_password"))
			.andExpect(status().isOk())
			.andExpect(view().name("reset_password"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/reset_password.jsp"));
	}

	@Test
	public void testPostContact() throws Exception {
		setUpTransport();

		setUpSecurityUtilsSubject(false);

		MockHttpServletRequestBuilder request = post("/contact");

		request.param("emailAddress", "user@test.com");
		request.param("message", "Sample contact message");

		this.mockMvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(view().name("contact"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/contact.jsp"))
			.andExpect(model().attributeExists("success"))
			.andExpect(
				model().attribute(
					"success", LanguageUtil.getMessage("message-send-success")));

		assertTransportCalled(1);
	}

	@Test
	public void testPostContactWithException() throws Exception {
		setUpTransport();

		setUpSecurityUtilsSubject(false);

		this.mockMvc.perform(post("/contact"))
			.andExpect(status().isOk())
			.andExpect(view().name("contact"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/contact.jsp"))
			.andExpect(model().attributeExists("error"))
			.andExpect(
				model().attribute(
					"error", LanguageUtil.getMessage("message-send-fail")));

		assertTransportCalled(0);
	}

	@Test
	public void testPostForgotPassword() throws Exception {
		setUpRecaptchaUtil(true);
		setUpTransport();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		MockHttpServletRequestBuilder request = post("/forgot_password");

		request.param("emailAddress", "test@test.com");
		request.param("g-recaptcha-response", "recaptchaResponse");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:forgot_password"))
			.andExpect(redirectedUrl("forgot_password"))
			.andExpect(flash().attribute(
				"success", LanguageUtil.getMessage("forgot-password-success")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotNull(user.getPasswordResetToken());

		assertTransportCalled(1);
	}

	@Test
	public void testPostForgotPasswordWithInvalidRecaptcha() throws Exception {
		setUpRecaptchaUtil(false);

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		MockHttpServletRequestBuilder request = post("/forgot_password");

		request.param("emailAddress", "test@test.com");
		request.param("g-recaptcha-response", "recaptchaResponse");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:forgot_password"))
			.andExpect(redirectedUrl("forgot_password"))
			.andExpect(flash().attribute(
				"success", LanguageUtil.getMessage("forgot-password-success")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNull(user.getPasswordResetToken());
	}

	@Test
	public void testPostLogInWithAuthenticatedUser() throws Exception {
		setUpSecurityUtilsSubject(true);

		this.mockMvc.perform(post("/log_in"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:home"))
			.andExpect(redirectedUrl("home"));
	}

	@Test
	public void testPostLogInWithAuthenticatedUserAndRedirect()
		throws Exception {

		setUpSecurityUtilsSubject(true);

		MockHttpServletRequestBuilder request = post("/log_in");

		request.param("redirect", "test_redirect");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:test_redirect"))
			.andExpect(redirectedUrl("test_redirect"));
	}

	@Test
	public void testPostLogInWithUnauthenticatedUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(false, _FIRST_USER.getUserId(), 0);

		MockHttpServletRequestBuilder request = post("/log_in");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:home"))
			.andExpect(redirectedUrl("home"));
	}

	@Test
	public void testPostLogInWithUnknownAccountException() throws Exception {
		setUpSecurityUtilsSessionWithException(
			false, _INVALID_USER_ID, 0, new UnknownAccountException());

		MockHttpServletRequestBuilder request = post("/log_in");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:log_in"))
			.andExpect(redirectedUrl("log_in"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error", LanguageUtil.getMessage("log-in-failure")));
	}

	@Test
	public void testPostLogInWithIncorrectCredentialsException()
		throws Exception {

		setUpSecurityUtilsSessionWithException(
			false, _INVALID_USER_ID, 0, new IncorrectCredentialsException());

		MockHttpServletRequestBuilder request = post("/log_in");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:log_in"))
			.andExpect(redirectedUrl("log_in"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error", LanguageUtil.getMessage("log-in-failure")));
	}

	@Test
	public void testPostLogInWithUnknownError()
		throws Exception {

		setUpSecurityUtilsSessionWithException(
			false, _INVALID_USER_ID, 0, new AccountException());

		MockHttpServletRequestBuilder request = post("/log_in");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:log_in"))
			.andExpect(redirectedUrl("log_in"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error", LanguageUtil.getMessage("log-in-failure")));
	}

	@Test
	public void testPostLogInExceedingLoginLimitAndFailingRecaptcha()
		throws Exception {

		setUpRecaptchaUtil(false);

		setUpSecurityUtilsSession(
			false, _INVALID_USER_ID, PropertiesValues.LOGIN_ATTEMPT_LIMIT + 1);

		MockHttpServletRequestBuilder request = post("/log_in");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:log_in"))
			.andExpect(redirectedUrl("log_in"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error", LanguageUtil.getMessage("recaptcha-failure")));
	}

	@Test
	public void testPostLogInExceedingLoginLimitAndPassingRecaptcha()
		throws Exception {

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpRecaptchaUtil(true);

		setUpSecurityUtilsSession(
			false, _FIRST_USER.getUserId(),
			PropertiesValues.LOGIN_ATTEMPT_LIMIT + 1);

		MockHttpServletRequestBuilder request = post("/log_in");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");
		request.param("g-recaptcha-response", "recaptchaResponse");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:home"))
			.andExpect(redirectedUrl("home"));
	}

	@Test
	public void testPostResetPassword() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSubject(true);

		Subject currentUser = SecurityUtils.getSubject();

		Assert.assertTrue(currentUser.isAuthenticated());

		UserUtil.updatePasswordResetToken(_FIRST_USER.getUserId());

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		String password = user.getPassword();
		String salt = user.getSalt();

		MockHttpServletRequestBuilder request = post("/reset_password");

		request.param("emailAddress", user.getEmailAddress());
		request.param("password", "updatedPassword");
		request.param("passwordResetToken", user.getPasswordResetToken());

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:log_in"));
		resultActions.andExpect(redirectedUrl("log_in"));
		resultActions.andExpect(flash().attributeExists("success"));
		resultActions.andExpect(flash().attribute(
			"success", LanguageUtil.getMessage("password-reset-success")));

		user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertNotEquals(password, user.getPassword());
		Assert.assertNotEquals(salt, user.getSalt());

		currentUser = SecurityUtils.getSubject();

		Assert.assertFalse(currentUser.isAuthenticated());
	}

	@Test
	public void testPostResetPasswordWithInvalidResetToken() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updatePasswordResetToken(_FIRST_USER.getUserId());

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		String password = user.getPassword();
		String salt = user.getSalt();

		MockHttpServletRequestBuilder request = post("/reset_password");

		request.param("emailAddress", user.getEmailAddress());
		request.param("password", "updatedPassword");
		request.param("passwordResetToken", "invalidPasswordResetToken");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:reset_password"));
		resultActions.andExpect(redirectedUrl("reset_password"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("password-reset-fail")));

		user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals(password, user.getPassword());
		Assert.assertEquals(salt, user.getSalt());
	}

	@Test
	public void testPostResetPasswordWithNullUser() throws Exception {
		MockHttpServletRequestBuilder request = post("/reset_password");

		request.param("emailAddress", "invalid@test.com");
		request.param("password", "updatedPassword");
		request.param("passwordResetToken", "invalidPasswordResetToken");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:reset_password"));
		resultActions.andExpect(redirectedUrl("reset_password"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("password-reset-fail")));
	}

	@Test
	public void testResubscribeWithActiveUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error",
				LanguageUtil.getMessage("subscription-resubscribe-failure")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testResubscribeWithInactiveUser() throws Exception {
		setUpCustomer();
		setUpSubscription();
		setUpTransport();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", false,
			true);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("success"))
			.andExpect(flash().attribute(
				"success",
				LanguageUtil.getMessage("subscription-updated")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());

		assertTransportCalled(1);
	}

	@Test
	public void testResubscribeWithNullSubscriptionId() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "", true, false);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error",
				LanguageUtil.getMessage("subscription-resubscribe-failure")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testResubscribeWithPendingCancellationUser() throws Exception {
		setUpCustomer();
		setUpSubscription();
		setUpTransport();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true, true);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("success"))
			.andExpect(flash().attribute(
				"success",
				LanguageUtil.getMessage("subscription-updated")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());

		assertTransportCalled(1);
	}

	@Test
	public void testResubscribeWithResubscribeException() throws Exception {
		setUpInvalidSubscription();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", false,
			true);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error",
				LanguageUtil.getMessage("subscription-resubscribe-failure")));

		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertFalse(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());
	}

	@Test
	public void testStripeWebhookEndpoint() throws Exception {
		_setUpStripeUtil();

		MockHttpServletRequestBuilder request = post(
			"/stripe");

		request.contentType(MediaType.APPLICATION_FORM_URLENCODED);

		request.param("stripeJsonEvent", "stripeJsonEvent");

		request.header("Stripe-Signature", "stripeSignature");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
	}

	@Test
	public void testUpdateMyAccount() throws Exception {
		setUpCustomer();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("success"));
		resultActions.andExpect(flash().attribute(
			"success", LanguageUtil.getMessage("account-update-success")));

		_assertUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithDuplicateEmailAddress()
		throws Exception {

		setUpCustomer();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		_SECOND_USER = UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("duplicate-email-address")));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidEmailAddress() throws Exception {
		setUpCustomer();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		request.param("emailAddress", "test");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("invalid-email-address")));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithIncorrectPassword() throws Exception {
		setUpCustomer();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = post(
			"/my_account");

		request.param("userId", String.valueOf(_FIRST_USER.getUserId()));
		request.param("emailAddress", "test2@test.com");
		request.param("emailNotification", "false");
		request.param("currentPassword", "incorrectPassword");
		request.param("newPassword", "short");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("incorrect-password")));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidPassword() throws Exception {
		setUpCustomer();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		MockHttpServletRequestBuilder request = post(
			"/my_account");

		request.param("userId", String.valueOf(_FIRST_USER.getUserId()));
		request.param("emailAddress", "test2@test.com");
		request.param("preferredDomain", "http://www.ebay.ca/itm/");
		request.param("emailNotification", "false");
		request.param("currentPassword", "password");
		request.param("newPassword", "short");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("invalid-password-length")));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidUserId() throws Exception {
		setUpCustomer();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_INVALID_USER_ID);

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("account-update-fail")));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateSubscription() throws Exception {
		setUpCustomer();
		setUpTransport();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		MockHttpServletRequestBuilder request = post("/update_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("success"));
		resultActions.andExpect(flash().attribute(
			"success",
			LanguageUtil.getMessage("subscription-updated")));

		assertTransportCalled(1);
	}

	@Test
	public void testUpdateSubscriptionWithStripeException() throws Exception {
		setUpInvalidCustomer();

		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		MockHttpServletRequestBuilder request = post("/update_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error",
			LanguageUtil.getMessage("incorrect-payment-information")));
	}

	@Test
	public void testViewMyAccountAsActiveUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		UserUtil.updateUserSubscription(
			_FIRST_USER.getUserId(), "customerId", "subscriptionId", true,
			false);

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		this.mockMvc.perform(get("/my_account")
			.sessionAttr("info", "Info Message")
			.param("error", "Error Message")
			.param("success", "Success Message"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attributeExists("user"))
			.andExpect(model().attributeExists("preferredDomains"))
			.andExpect(model().attributeExists("stripePublishableKey"))
			.andExpect(model().attributeExists("error"))
			.andExpect(model().attributeExists("info"))
			.andExpect(model().attributeExists("success"))
			.andExpect(model().attribute("isActive", true))
			.andExpect(model().attribute("info", "Info Message"))
			.andExpect(model().attribute("error", "Error Message"))
			.andExpect(model().attribute("success", "Success Message"));
	}

	@Test
	public void testViewMyAccountAsInactiveUser() throws Exception {
		_FIRST_USER = UserUtil.addUser("test@test.com", "password");

		setUpSecurityUtilsSession(_FIRST_USER.getUserId());

		this.mockMvc.perform(get("/my_account")
			.sessionAttr("info", "Info Message")
			.param("error", "Error Message")
			.param("success", "Success Message"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attributeExists("user"))
			.andExpect(model().attributeExists("preferredDomains"))
			.andExpect(model().attributeExists("stripePublishableKey"))
			.andExpect(model().attributeExists("error"))
			.andExpect(model().attributeExists("info"))
			.andExpect(model().attributeExists("success"))
			.andExpect(model().attribute("isActive", false))
			.andExpect(model().attribute("info", "Info Message"))
			.andExpect(model().attribute("error", "Error Message"))
			.andExpect(model().attribute("success", "Success Message"));
	}

	protected static void setUpCustomer() throws Exception {
		Customer customer = Mockito.mock(Customer.class);

		CustomerSubscriptionCollection customerSubscriptionCollection =
			new CustomerSubscriptionCollection();

		List<Subscription> subscriptions = new ArrayList<>();

		Subscription subscription = new Subscription();

		subscription.setId("testSubscriptionId");

		subscriptions.add(subscription);

		customerSubscriptionCollection.setData(subscriptions);

		PowerMockito.spy(Customer.class);

		PowerMockito.doReturn(
			customer
		).when(
			Customer.class, "retrieve", Mockito.anyString()
		);

		PowerMockito.doReturn(
			customer
		).when(
			Customer.class, "create", Mockito.anyMap()
		);

		Mockito.when(
			customer.update(Mockito.anyMap())
		).thenReturn(
			customer
		);

		Mockito.when(
			customer.getSubscriptions()
		).thenReturn(
			customerSubscriptionCollection
		);

		Mockito.when(
			customer.getId()
		).thenReturn(
			"testCustomerId"
		);

		DeletedCustomer deletedCustomer = new DeletedCustomer();

		Mockito.when(
			customer.delete()
		).thenReturn(
			deletedCustomer
		);
	}

	protected static void setUpInvalidCustomer() throws Exception {
		PowerMockito.spy(Customer.class);

		PowerMockito.doThrow(
			new APIConnectionException("")
		).when(
			Customer.class, "create", Mockito.anyMap()
		);
	}

	protected static void setUpRecaptchaUtil(boolean isValid) throws Exception {
		URL url = PowerMockito.mock(URL.class);

		PowerMockito.whenNew(
			URL.class
		).withAnyArguments().thenReturn(
			url
		);

		HttpsURLConnection httpsURLConnection = Mockito.mock(
			HttpsURLConnection.class);

		InputStream inputStream = null;

		if (isValid) {
			inputStream = new ByteArrayInputStream(
				"{\"success\": true}".getBytes());
		}
		else {
			inputStream = new ByteArrayInputStream(
				"{\"success\": \"false\"}".getBytes());
		}

		Mockito.when(
			url.openConnection()
		).thenReturn(
			httpsURLConnection
		);

		Mockito.when(
			httpsURLConnection.getInputStream()
		).thenReturn(
			inputStream
		);
	}

	private static void _setUpStripeUtil() throws Exception {
		PowerMockito.spy(Webhook.class);

		Event event = new Event();

		EventData eventData = new EventData();

		event.setData(eventData);
		event.setType("Invalid Type");

		PowerMockito.doReturn(
			event
		).when(
			Webhook.class, "constructEvent", Mockito.anyString(),
			Mockito.anyString(), Mockito.anyString()
		);
	}

	protected static void setUpInvalidSubscription() throws Exception {
		PowerMockito.spy(Subscription.class);

		PowerMockito.doThrow(
			new APIConnectionException("")
		).when(
			Subscription.class, "retrieve", Mockito.anyString()
		);
	}

	protected static void setUpSubscription() throws Exception {
		Subscription subscription = Mockito.mock(Subscription.class);

		subscription.setCurrentPeriodEnd(0L);

		PowerMockito.spy(Subscription.class);

		PowerMockito.doReturn(
			subscription
		).when(
			Subscription.class, "retrieve", Mockito.anyString()
		);

		Mockito.when(
			subscription.update(Mockito.anyMap())
		).thenReturn(
			subscription
		);

		Mockito.when(
			subscription.cancel(Mockito.anyMap())
		).thenReturn(
			subscription
		);
	}

	private void _assertNotUpdatedUser() throws Exception {
		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals("test@test.com", user.getEmailAddress());
		Assert.assertEquals(
			ConstantsUtil.DEFAULT_PREFERRED_DOMAIN, user.getPreferredDomain());
		Assert.assertTrue(user.isEmailNotification());
	}

	private void _assertUpdatedUser() throws Exception {
		User user = UserUtil.getUserByUserId(_FIRST_USER.getUserId());

		Assert.assertEquals("test2@test.com", user.getEmailAddress());
		Assert.assertEquals(
			"http://www.ebay.ca/itm/", user.getPreferredDomain());
		Assert.assertFalse(user.isEmailNotification());
	}

	private MockHttpServletRequestBuilder _buildUpdateMyAccountRequest() {
		MockHttpServletRequestBuilder request = post(
			"/my_account");

		request.param("userId", String.valueOf(_FIRST_USER.getUserId()));
		request.param("emailAddress", "test2@test.com");
		request.param("preferredDomain", "http://www.ebay.ca/itm/");
		request.param("emailNotification", "false");
		request.param("currentPassword", "password");
		request.param("newPassword", "updatedPassword");

		return request;
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	private static User _FIRST_USER;
	private static User _SECOND_USER;
	private static User _THIRD_USER;

}