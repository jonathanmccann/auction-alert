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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.UserUtil;

import com.stripe.model.Customer;
import com.stripe.model.CustomerSubscriptionCollection;
import com.stripe.model.Subscription;

import java.util.ArrayList;
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
	public void testCreateSubscription() throws Exception {
		setUpCustomer();
		setUpProperties();
		setUpUserUtil();

		MockHttpServletRequestBuilder request = post("/create_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeDoesNotExist("error"));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testCreateSubscriptionWithActiveUser() throws Exception {

		setUpUserUtil();

		UserUtil.updateUserSubscription(
			_USER.getCustomerId(), _USER.getSubscriptionId(), true,
			_USER.isPendingCancellation());

		MockHttpServletRequestBuilder request = post("/create_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));
	}

	@Test
	public void testCreateSubscriptionWithExistingSubscription()
		throws Exception {

		setUpUserUtil();

		UserUtil.updateUserSubscription(
			"customerId", _USER.getSubscriptionId(), false,
			_USER.isPendingCancellation());

		MockHttpServletRequestBuilder request = post("/create_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));
	}

	@Test
	public void testCreateSubscriptionWithInvalidEmailAddress()
		throws Exception {

		setUpUserUtil();

		MockHttpServletRequestBuilder request = post("/create_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test2@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));
	}

	@Test
	public void testCreateSubscriptionWithInvalidStripeToken()
		throws Exception {

		setUpUserUtil();

		MockHttpServletRequestBuilder request = post("/create_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));
	}

	@Test
	public void testDeleteSubscription() throws Exception {
		setUpUserUtil();
		setUpSubscription();

		UserUtil.updateUserSubscription(
			"customerId", "subscriptionId", true, false);

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeDoesNotExist("error"));

		User user = UserUtil.getCurrentUser();

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
			"customerId", _USER.getSubscriptionId(), true, false);

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));

		User user = UserUtil.getCurrentUser();

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertNull(user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testDeleteSubscriptionWithInactiveUser() throws Exception {
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			"customerId", "subscriptionId", false, false);

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));

		User user = UserUtil.getCurrentUser();

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
			"customerId", "subscriptionId", true, true);

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));

		User user = UserUtil.getCurrentUser();

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
			"customerId", "subscriptionId", true, false);

		MockHttpServletRequestBuilder request = post("/delete_subscription");

		request.param("stripeToken", "test");
		request.param("stripeEmail", "test@test.com");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));

		User user = UserUtil.getCurrentUser();

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testGetCreateAccount() throws Exception {
		this.mockMvc.perform(get("/create_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("create_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/create_account.jsp"));
	}

	@Test
	public void testResubscribeWithActiveUser() throws Exception {
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			"customerId", "subscriptionId", true, false);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("user"))
			.andExpect(model().attributeExists("error"));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testResubscribeWithNullSubscription() throws Exception {
		setUpNullSubscription();
		setUpProperties();
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			"customerId", "subscriptionId", false, true);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("user"))
			.andExpect(model().attributeDoesNotExist("error"));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("updatedSubscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testResubscribeWithPreviousSubscription() throws Exception {
		setUpProperties();
		setUpSubscription();
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			"customerId", "subscriptionId", false, true);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("user"))
			.andExpect(model().attributeDoesNotExist("error"));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertTrue(user.isActive());
		Assert.assertFalse(user.isPendingCancellation());
	}

	@Test
	public void testResubscribeWithResubscribeException() throws Exception {
		setUpUserUtil();

		UserUtil.updateUserSubscription(
			"customerId", "subscriptionId", false, true);

		this.mockMvc.perform(post("/resubscribe"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("user"))
			.andExpect(model().attributeExists("error"));

		User user = UserUtil.getUserByUserId(_USER_ID);

		Assert.assertEquals("customerId", user.getCustomerId());
		Assert.assertEquals("subscriptionId", user.getSubscriptionId());
		Assert.assertFalse(user.isActive());
		Assert.assertTrue(user.isPendingCancellation());
	}

	@Test
	public void testUpdateMyAccount() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeDoesNotExist("error"));

		_assertUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithDuplicateEmailAddress()
		throws Exception {

		setUpUserUtil();

		UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidEmailAddress() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		request.param("emailAddress", "test");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeExists("error"));

		_assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidUserId() throws Exception {
		setUpInvalidUserUtil();

		UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = _buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("user"));
		resultActions.andExpect(model().attributeDoesNotExist("error"));

		_assertNotUpdatedUser();
	}

	@Test
	public void testViewMyAccount() throws Exception {
		setUpUserUtil();

		this.mockMvc.perform(get("/my_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("user"));
	}

	protected static void setUpCustomer() throws Exception {
		Customer customer = new Customer();

		customer.setId("customerId");

		CustomerSubscriptionCollection customerSubscriptionCollection =
			new CustomerSubscriptionCollection();

		List<Subscription> subscriptions = new ArrayList<>();

		Subscription subscription = new Subscription();

		subscription.setId("subscriptionId");

		subscriptions.add(subscription);

		customerSubscriptionCollection.setData(subscriptions);

		customer.setSubscriptions(customerSubscriptionCollection);

		PowerMockito.spy(Customer.class);

		PowerMockito.doReturn(
			customer
		).when(
			Customer.class, "create", Mockito.anyMap()
		);
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

		return request;
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	private static User _USER;

}