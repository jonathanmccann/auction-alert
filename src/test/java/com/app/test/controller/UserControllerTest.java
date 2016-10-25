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
import com.app.util.PropertiesValues;
import com.app.util.RecaptchaUtil;
import com.app.util.SearchQueryPreviousResultUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;
import com.app.util.UserUtil;

import com.stripe.model.Customer;
import com.stripe.model.CustomerSubscriptionCollection;
import com.stripe.model.DeletedCustomer;
import com.stripe.model.Event;
import com.stripe.model.Subscription;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@PrepareForTest({Customer.class, RecaptchaUtil.class, Subscription.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class UserControllerTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		setUpProperties();
	}

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		setUpDatabase();

		_USER = UserUtil.addUser("test@test.com", "password");
	}

	@Test
	public void testCreateAccount() throws Exception {
		setUpCustomer();
		setUpMailSender();
		setUpSecurityUtils(true);
		setUpUserUtil();

		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("emailAddress", "test2@test.com");
		request.param("password", "password");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:home"));
		resultActions.andExpect(redirectedUrl("home"));

		User user = UserUtil.getUserByEmailAddress("test2@test.com");

		Assert.assertEquals("testCustomerId", user.getCustomerId());
		Assert.assertEquals("testSubscriptionId", user.getSubscriptionId());
	}

	@Test
	public void testCreateAccountExceedingMaximumNumberOfUsers()
		throws Exception {

		UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("emailAddress", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:create_account"));
		resultActions.andExpect(redirectedUrl("create_account"));
	}

	@Test
	public void testCreateAccountWithDuplicateEmailAddress() throws Exception {
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

		request.param("emailAddress", "test2@test.com");
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

		setUpUserUtil();

		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("emailAddress", "test2@test.com");
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
		setUpMailSender();
		setUpUserUtil();
		setUpSubscription();

		UserUtil.updateUserSubscription(
			_USER_ID, "unsubscribeToken", "customerId", "subscriptionId", true,
			false);

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

		User user = UserUtil.getCurrentUser();

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());
	}

	@Test
	public void testDeleteSubscriptionWithNullSubscriptionId()
		throws Exception {

		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "unsubscribeToken", "customerId",
			_USER.getSubscriptionId(), true, false);

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

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertNull(user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testDeleteSubscriptionWithInactiveUser() throws Exception {
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID,  "unsubscribeToken", "customerId", "subscriptionId",
			false, false);

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

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertFalse(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testDeleteSubscriptionWithPendingCancellation()
		throws Exception {

		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID,  "unsubscribeToken", "customerId", "subscriptionId", true,
			true);

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

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());
	}

	@Test
	public void testDeleteSubscriptionWithCancellationException()
		throws Exception {

		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID,  "unsubscribeToken", "customerId", "subscriptionId", true,
			false);

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

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testDeleteUser() throws Exception {
		setUpCustomer();
		setUpUserUtil();

		SearchQuery searchQuery = new SearchQuery();

		searchQuery.setUserId(_USER_ID);
		searchQuery.setKeywords("Test keywords");
		searchQuery.setActive(true);

		int searchQueryId = SearchQueryUtil.addSearchQuery(searchQuery);

		SearchQueryPreviousResultUtil.addSearchQueryPreviousResult(
			searchQueryId, "1234");

		SearchResult searchResult = new SearchResult(
			searchQueryId, "1234", "First Item", 10.00, 14.99,
			"http://www.ebay.com/itm/1234", "http://www.ebay.com/123.jpg");

		SearchResultUtil.addSearchResult(searchResult);

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user);

		MockHttpServletRequestBuilder request = post("/delete_user");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:log_out"));
		resultActions.andExpect(redirectedUrl("log_out"));

		user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNull(user);

		try {
			SearchQueryUtil.getSearchQuery(searchQueryId);
		}
		catch (SQLException sqle) {
			Assert.assertEquals(SQLException.class, sqle.getClass());
		}

		int searchQueryPreviousResultCount =
			SearchQueryPreviousResultUtil.getSearchQueryPreviousResultsCount(
				searchQueryId);

		Assert.assertEquals(0, searchQueryPreviousResultCount);

		List<SearchResult> searchResults =
			SearchResultUtil.getSearchQueryResults(searchQueryId);

		Assert.assertEquals(0, searchResults.size());
	}

	@Test
	public void testDeleteUserWithIncorrectPassword() throws Exception {
		setUpUserUtil();

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user);

		MockHttpServletRequestBuilder request = post("/delete_user");

		request.param("emailAddress", "test@test.com");
		request.param("password", "incorrectPassword");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));
		resultActions.andExpect(flash().attribute(
			"error", LanguageUtil.getMessage("user-deletion-failure")));

		user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user);
	}

	@Test
	public void testGetContactWithAuthenticatedUser() throws Exception {
		setUpSecurityUtils(true);
		setUpUserUtil();

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
	public void testGetContactWithUnauthenticatedUser() throws Exception {
		setUpSecurityUtils(false);
		setUpUserUtil();

		this.mockMvc.perform(get("/contact"))
			.andExpect(status().isOk())
			.andExpect(view().name("contact"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/contact.jsp"))
			.andExpect(model().attributeDoesNotExist("emailAddress"))
			.andExpect(model().attributeExists("isActive"))
			.andExpect(model().attribute("isActive", true));
	}

	@Test
	public void testGetCreateAccount() throws Exception {
		setUpSecurityUtils(false);

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
		setUpSecurityUtils(true);
		setUpUserUtil();

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
	public void testGetFaq() throws Exception {
		setUpUserUtil();

		this.mockMvc.perform(get("/faq"))
			.andExpect(status().isOk())
			.andExpect(view().name("faq"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/faq.jsp"))
			.andExpect(model().attribute("isActive", true));
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
		setUpSecurityUtils(true);
		setUpSubscription();
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "", "", "testSubscriptionId", true, true);

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
		setUpSecurityUtils(true);
		setUpSubscription();
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "", "", "testSubscriptionId", true, false);

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
		setUpSecurityUtils(true);
		setUpSubscription();
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "", "", "testSubscriptionId", false, false);

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
		setUpSecurityUtils(true);
		setUpSubscription();
		setUpUserUtil();

		UserUtil.updateEmailsSent(_USER_ID, 1);

		UserUtil.updateUserSubscription(
			_USER_ID, "", "", "testSubscriptionId", false, false);

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
		setUpSecurityUtils(false);

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
		setUpSecurityUtils(true);
		setUpSubscription();
		setUpUserUtil();

		this.mockMvc.perform(get("/log_in"))
			.andExpect(status().isOk())
			.andExpect(view().name("home"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/home.jsp"));
	}

	@Test
	public void testGetLogInWithUnauthenticatedUser() throws Exception {
		PowerMockito.spy(SecurityUtils.class);

		Session session = new SimpleSession();

		session.setAttribute("loginAttempts", 0);

		Subject mockSubject = Mockito.mock(Subject.class);

		PowerMockito.doReturn(
			mockSubject
		).when(
			SecurityUtils.class, "getSubject"
		);

		PowerMockito.doReturn(
			session
		).when(
			mockSubject
		).getSession();

		this.mockMvc.perform(get("/log_in"))
			.andExpect(status().isOk())
			.andExpect(view().name("log_in"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/log_in.jsp"));
	}

	@Test
	public void testGetLogInExceedingLoginAttemptLimit() throws Exception {
		PowerMockito.spy(SecurityUtils.class);

		Session session = new SimpleSession();

		session.setAttribute(
			"loginAttempts", PropertiesValues.LOGIN_ATTEMPT_LIMIT + 1);

		Subject mockSubject = Mockito.mock(Subject.class);

		PowerMockito.doReturn(
			mockSubject
		).when(
			SecurityUtils.class, "getSubject"
		);

		PowerMockito.doReturn(
			session
		).when(
			mockSubject
		).getSession();

		this.mockMvc.perform(get("/log_in"))
			.andExpect(status().isOk())
			.andExpect(view().name("log_in"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/log_in.jsp"))
			.andExpect(model().attribute(
				"recaptchaSiteKey", PropertiesValues.RECAPTCHA_SITE_KEY));
	}

	@Test
	public void testGetLogOut() throws Exception {
		setUpSecurityUtils(true);

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
		setUpMailSender();
		setUpSecurityUtils(false);

		this.mockMvc.perform(post("/contact"))
			.andExpect(status().isOk())
			.andExpect(view().name("contact"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/contact.jsp"))
			.andExpect(model().attributeExists("success"))
			.andExpect(
				model().attribute(
					"success", LanguageUtil.getMessage("message-send-success")));
	}

	@Test
	public void testPostContactWithException() throws Exception {
		setUpSecurityUtils(false);

		this.mockMvc.perform(post("/contact"))
			.andExpect(status().isOk())
			.andExpect(view().name("contact"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/contact.jsp"))
			.andExpect(model().attributeExists("error"))
			.andExpect(
				model().attribute(
					"error", LanguageUtil.getMessage("message-send-fail")));
	}

	@Test
	public void testPostForgotPassword() throws Exception {
		setUpMailSender();
		setUpRecaptchaUtil(true);

		MockHttpServletRequestBuilder request = post("/forgot_password");

		request.param("emailAddress", "test@test.com");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:forgot_password"))
			.andExpect(redirectedUrl("forgot_password"))
			.andExpect(flash().attribute(
				"success", LanguageUtil.getMessage("forgot-password-success")));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user.getPasswordResetToken());
	}

	@Test
	public void testPostForgotPasswordWithInvalidRecaptcha() throws Exception {
		setUpRecaptchaUtil(false);

		MockHttpServletRequestBuilder request = post("/forgot_password");

		request.param("emailAddress", "test@test.com");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:forgot_password"))
			.andExpect(redirectedUrl("forgot_password"))
			.andExpect(flash().attribute(
				"success", LanguageUtil.getMessage("forgot-password-success")));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNull(user.getPasswordResetToken());
	}

	@Test
	public void testPostLogInWithAuthenticatedUser() throws Exception {
		setUpSecurityUtils(true);

		this.mockMvc.perform(post("/log_in"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:home"))
			.andExpect(redirectedUrl("home"));
	}

	@Test
	public void testPostLogInWithAuthenticatedUserAndRedirect()
		throws Exception {

		setUpSecurityUtils(true);

		MockHttpServletRequestBuilder request = post("/log_in");

		request.param("redirect", "test_redirect");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:test_redirect"))
			.andExpect(redirectedUrl("test_redirect"));
	}

	@Test
	public void testPostLogInWithUnauthenticatedUser() throws Exception {
		PowerMockito.spy(SecurityUtils.class);

		Session session = new SimpleSession();

		Subject mockSubject = Mockito.mock(Subject.class);

		PowerMockito.doReturn(
			mockSubject
		).when(
			SecurityUtils.class, "getSubject"
		);

		PowerMockito.doReturn(
			session
		).when(
			mockSubject
		).getSession();

		Mockito.doNothing().when(
			mockSubject
		).login(
			Mockito.any(AuthenticationToken.class)
		);

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
		PowerMockito.spy(SecurityUtils.class);

		Session session = new SimpleSession();

		Subject mockSubject = Mockito.mock(Subject.class);

		PowerMockito.doReturn(
			mockSubject
		).when(
			SecurityUtils.class, "getSubject"
		);

		PowerMockito.doReturn(
			session
		).when(
			mockSubject
		).getSession();

		Mockito.doThrow(new UnknownAccountException()).when(
			mockSubject
		).login(
			Mockito.any(AuthenticationToken.class)
		);

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

		PowerMockito.spy(SecurityUtils.class);

		Session session = new SimpleSession();

		Subject mockSubject = Mockito.mock(Subject.class);

		PowerMockito.doReturn(
			mockSubject
		).when(
			SecurityUtils.class, "getSubject"
		);

		PowerMockito.doReturn(
			session
		).when(
			mockSubject
		).getSession();

		Mockito.doThrow(new IncorrectCredentialsException()).when(
			mockSubject
		).login(
			Mockito.any(AuthenticationToken.class)
		);

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

		PowerMockito.spy(SecurityUtils.class);

		Session session = new SimpleSession();

		session.setAttribute(
			"loginAttempts", PropertiesValues.LOGIN_ATTEMPT_LIMIT + 1);

		Subject mockSubject = Mockito.mock(Subject.class);

		PowerMockito.doReturn(
			mockSubject
		).when(
			SecurityUtils.class, "getSubject"
		);

		PowerMockito.doReturn(
			session
		).when(
			mockSubject
		).getSession();

		Mockito.doNothing().when(
			mockSubject
		).login(
			Mockito.any(AuthenticationToken.class)
		);

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

		setUpRecaptchaUtil(true);

		PowerMockito.spy(SecurityUtils.class);

		Session session = new SimpleSession();

		session.setAttribute(
			"loginAttempts", PropertiesValues.LOGIN_ATTEMPT_LIMIT + 1);

		Subject mockSubject = Mockito.mock(Subject.class);

		PowerMockito.doReturn(
			mockSubject
		).when(
			SecurityUtils.class, "getSubject"
		);

		PowerMockito.doReturn(
			session
		).when(
			mockSubject
		).getSession();

		Mockito.doNothing().when(
			mockSubject
		).login(
			Mockito.any(AuthenticationToken.class)
		);

		MockHttpServletRequestBuilder request = post("/log_in");

		request.param("emailAddress", "test@test.com");
		request.param("password", "password");

		this.mockMvc.perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:home"))
			.andExpect(redirectedUrl("home"));
	}

	@Test
	public void testPostResetPassword() throws Exception {
		setUpSecurityUtils(true);
		setUpUserUtil();

		Subject currentUser = SecurityUtils.getSubject();

		Assert.assertTrue(currentUser.isAuthenticated());

		UserUtil.updatePasswordResetToken(_USER_ID);

		User user = UserUtil.getUserByUserId(_USER_ID);

		String password = user.getPassword();
		String salt = user.getSalt();

		MockHttpServletRequestBuilder request = post("/reset_password");

		request.param("emailAddress", user.getEmailAddress());
		request.param("password", "updatedPassword");
		request.param("passwordResetToken", user.getPasswordResetToken());

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:reset_password"));
		resultActions.andExpect(redirectedUrl("reset_password"));
		resultActions.andExpect(flash().attributeExists("success"));
		resultActions.andExpect(flash().attribute(
			"success", LanguageUtil.getMessage("password-reset-success")));

		user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotEquals(password, user.getPassword());
		Assert.assertNotEquals(salt, user.getSalt());

		currentUser = SecurityUtils.getSubject();

		Assert.assertFalse(currentUser.isAuthenticated());
	}

	@Test
	public void testPostResetPasswordWithInvalidResetToken() throws Exception {
		setUpUserUtil();

		UserUtil.updatePasswordResetToken(_USER_ID);

		User user = UserUtil.getUserByUserId(_USER_ID);

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

		user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertEquals(password, user.getPassword());
		Assert.assertEquals(salt, user.getSalt());
	}

	@Test
	public void testPostResetPasswordWithNullUser() throws Exception {
		setUpUserUtil();

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
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "unsubscribeToken", "customerId", "subscriptionId", true,
			false);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error",
				LanguageUtil.getMessage("subscription-resubscribe-failure")));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testResubscribeWithInactiveUser() throws Exception {
		setUpCustomer();
		setUpMailSender();
		setUpSubscription();
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "unsubscribeToken", "customerId", "subscriptionId", false,
			true);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("success"))
			.andExpect(flash().attribute(
				"success",
				LanguageUtil.getMessage("subscription-updated")));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testResubscribeWithNullSubscriptionId() throws Exception {
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "unsubscribeToken", "customerId", "", true, false);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error",
				LanguageUtil.getMessage("subscription-resubscribe-failure")));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testResubscribeWithPendingCancellationUser() throws Exception {
		setUpCustomer();
		setUpMailSender();
		setUpSubscription();
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "unsubscribeToken", "customerId", "subscriptionId", true,
			true);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("success"))
			.andExpect(flash().attribute(
				"success",
				LanguageUtil.getMessage("subscription-updated")));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testResubscribeWithResubscribeException() throws Exception {
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "unsubscribeToken", "customerId", "subscriptionId", false,
			true);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:my_account"))
			.andExpect(redirectedUrl("my_account"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(flash().attribute(
				"error",
				LanguageUtil.getMessage("subscription-resubscribe-failure")));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertFalse(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());
	}

	@Test
	public void testUnsubscribeFromEmailNotifications() throws Exception {
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, "unsubscribeToken", _USER.getCustomerId(),
			_USER.getSubscriptionId(), _USER.isActive(), true);

		MockHttpServletRequestBuilder request = get("/email_unsubscribe");

		request.param("emailAddress", _USER.getEmailAddress());
		request.param("unsubscribeToken", "invalidUnsubscribeToken");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("email_unsubscribe"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/email_unsubscribe.jsp"));
		resultActions.andExpect(model().attributeExists("unsubscribeMessage"));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertTrue(user.isEmailNotification());

		request = get("/email_unsubscribe");

		request.param("emailAddress", _USER.getEmailAddress());
		request.param("unsubscribeToken", "unsubscribeToken");

		resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("email_unsubscribe"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/email_unsubscribe.jsp"));
		resultActions.andExpect(model().attributeExists("unsubscribeMessage"));

		user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertFalse(user.isEmailNotification());
	}

	@Test
	public void testUpdateMyAccount() throws Exception {
		setUpCustomer();
		setUpUserUtil();

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
		setUpUserUtil();

		UserUtil.addUser("test2@test.com", "password");

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
		setUpUserUtil();

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
		setUpUserUtil();

		MockHttpServletRequestBuilder request = post(
			"/my_account");

		request.param("userId", String.valueOf(_USER.getUserId()));
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
		setUpUserUtil();

		MockHttpServletRequestBuilder request = post(
			"/my_account");

		request.param("userId", String.valueOf(_USER.getUserId()));
		request.param("emailAddress", "test2@test.com");
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
		setUpInvalidUserUtil();

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
		setUpMailSender();
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, _USER.getUnsubscribeToken(), "customerId",
			"subscriptionId", true, false);

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
	}

	@Test
	public void testUpdateSubscriptionWithStripeException() throws Exception {
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER_ID, _USER.getUnsubscribeToken(), "customerId",
			"subscriptionId", true, false);

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
	public void testUpdateSubscriptionWithoutCurrentSubscription()
		throws Exception {

		setUpUserUtil();

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
	public void testViewMyAccount() throws Exception {
		setUpUserUtil();

		this.mockMvc.perform(get("/my_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("user"))
			.andExpect(model().attributeExists("preferredDomains"))
			.andExpect(model().attributeExists("stripePublishableKey"));
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

	protected static void setUpRecaptchaUtil(boolean valid) throws Exception {
		PowerMockito.spy(RecaptchaUtil.class);

		PowerMockito.doReturn(
			valid
		).when(
			RecaptchaUtil.class, "verifyRecaptchaResponse", Mockito.anyString()
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
		User user = UserUtil.getUserByUserId(_USER.getUserId());

		Assert.assertEquals("test@test.com", user.getEmailAddress());
		Assert.assertTrue(user.isEmailNotification());
	}

	private void _assertUpdatedUser() throws Exception {
		User user = UserUtil.getUserByUserId(_USER.getUserId());

		Assert.assertEquals("test2@test.com", user.getEmailAddress());
		Assert.assertFalse(user.isEmailNotification());
	}

	private MockHttpServletRequestBuilder _buildUpdateMyAccountRequest() {
		MockHttpServletRequestBuilder request = post(
			"/my_account");

		request.param("userId", String.valueOf(_USER.getUserId()));
		request.param("emailAddress", "test2@test.com");
		request.param("emailNotification", "false");
		request.param("currentPassword", "password");
		request.param("newPassword", "updatedPassword");

		return request;
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	private static User _USER;

}