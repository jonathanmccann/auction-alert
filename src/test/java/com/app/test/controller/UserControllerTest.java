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

import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.UserUtil;

import com.stripe.model.Customer;
import com.stripe.model.CustomerSubscriptionCollection;
import com.stripe.model.Subscription;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
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

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@PrepareForTest({Customer.class, Subscription.class})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class UserControllerTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		setUpDatabase();
		setUpProperties();

		_USER = UserUtil.addUser("test@test.com", "password");
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
	}

	@Test
	public void testCreateAccountWithInvalidStripeToken()
		throws Exception {

		setUpUserUtil();

		MockHttpServletRequestBuilder request = post("/create_account");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:create_account"));
		resultActions.andExpect(redirectedUrl("create_account"));
		resultActions.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testDeleteSubscription() throws Exception {
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

		User user = UserUtil.getCurrentUser();

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
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
			.andExpect(flash().attributeExists("error"));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertNotNull(user.getUnsubscribeToken());
		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testGetResetPassword() throws Exception {
		this.mockMvc.perform(get("/reset_password"))
			.andExpect(status().isOk())
			.andExpect(view().name("reset_password"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/reset_password.jsp"));
	}

	@Test
	public void testResetPassword() throws Exception {
		setUpUserUtil();

		UserUtil.updatePasswordResetToken(_USER_ID);

		User user = UserUtil.getUserByUserId(_USER_ID);

		MockHttpServletRequestBuilder request = post("/reset_password");

		request.param("emailAddress", user.getEmailAddress());
		request.param("password", "updatedPassword");
		request.param("passwordResetToken", user.getPasswordResetToken());

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:reset_password"));
		resultActions.andExpect(redirectedUrl("reset_password"));
		resultActions.andExpect(flash().attributeExists("success"));
	}

	@Test
	public void testResetPasswordWithInvalidResetToken() throws Exception {
		setUpUserUtil();

		UserUtil.updatePasswordResetToken(_USER_ID);

		User user = UserUtil.getUserByUserId(_USER_ID);

		MockHttpServletRequestBuilder request = post("/reset_password");

		request.param("emailAddress", user.getEmailAddress());
		request.param("password", "updatedPassword");
		request.param("passwordResetToken", "invalidPasswordResetToken");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:reset_password"));
		resultActions.andExpect(redirectedUrl("reset_password"));
		resultActions.andExpect(flash().attributeExists("error"));
	}

	@Test
	public void testResetPasswordWithNullUser() throws Exception {
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
			.andExpect(flash().attributeExists("error"));

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
		setUpUserUtil();

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("success"));

		_assertUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithDuplicateEmailAddress()
		throws Exception {

		setUpUserUtil();

		UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidEmailAddress() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		request.param("emailAddress", "test");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));
		resultActions.andExpect(flash().attributeExists("error"));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidUserId() throws Exception {
		setUpInvalidUserUtil();

		UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().is3xxRedirection());
		resultActions.andExpect(view().name("redirect:my_account"));
		resultActions.andExpect(redirectedUrl("my_account"));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateSubscriptionWithStripeException() throws Exception {
		setUpProperties();
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
	}

	@Test
	public void testViewMyAccount() throws Exception {
		setUpUserUtil();

		this.mockMvc.perform(get("/my_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("user"))
			.andExpect(model().attributeExists("stripePublishableKey"));
	}

	protected static void setUpNullSubscription() throws Exception {
		Subscription subscription = Mockito.mock(Subscription.class);

		Mockito.when(
			subscription.getId()
		).thenReturn(
			"updatedSubscriptionId"
		);

		PowerMockito.spy(Subscription.class);

		PowerMockito.doReturn(
			null
		).when(
			Subscription.class, "retrieve", Mockito.anyString()
		);

		PowerMockito.doReturn(
			subscription
		).when(
			Subscription.class, "create", Mockito.anyMap()
		);
	}

	protected static void setUpSubscription() throws Exception {
		Subscription subscription = Mockito.mock(Subscription.class);

		PowerMockito.spy(Subscription.class);

		PowerMockito.doReturn(
			subscription
		).when(
			Subscription.class, "retrieve", Mockito.anyString()
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