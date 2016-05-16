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

import com.app.model.NotificationPreferences;
import com.app.model.User;
import com.app.test.BaseTestCase;
import com.app.util.NotificationPreferencesUtil;

import com.app.util.UserUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.modules.junit4.rule.PowerMockRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author Jonathan McCann
 */
@ContextConfiguration("/test-dispatcher-servlet.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class UserControllerTest extends BaseTestCase {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		setUpDatabase();

		_USER = UserUtil.addUser("test@test.com", "password");

		NotificationPreferencesUtil.addNotificationPreferences(
			new NotificationPreferences(_USER.getUserId()));
	}

	@Test
	public void testUpdateMyAccount() throws Exception {
		setUpUserUtil();

		MockHttpServletRequestBuilder request = buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("userDetails"));
		resultActions.andExpect(model().attributeExists("hours"));
		resultActions.andExpect(
			model().attributeExists("mobileCarrierSuffixes"));
		resultActions.andExpect(
			model().attributeExists("mobileOperatingSystems"));
		resultActions.andExpect(model().attributeExists("timeZones"));
		resultActions.andExpect(
			model().attributeDoesNotExist("duplicateEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidPhoneNumberException"));

		assertUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithDuplicateEmailAddress()
		throws Exception {

		setUpUserUtil();

		UserUtil.addUser("test2@test.com", "password");

		MockHttpServletRequestBuilder request = buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("userDetails"));
		resultActions.andExpect(model().attributeExists("hours"));
		resultActions.andExpect(
			model().attributeExists("mobileCarrierSuffixes"));
		resultActions.andExpect(
			model().attributeExists("mobileOperatingSystems"));
		resultActions.andExpect(model().attributeExists("timeZones"));
		resultActions.andExpect(
			model().attributeExists("duplicateEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidPhoneNumberException"));

		assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidEmailAddress()
		throws Exception {

		setUpUserUtil();

		MockHttpServletRequestBuilder request = buildUpdateMyAccountRequest();

		request.param("user.emailAddress", "test");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("userDetails"));
		resultActions.andExpect(model().attributeExists("hours"));
		resultActions.andExpect(
			model().attributeExists("mobileCarrierSuffixes"));
		resultActions.andExpect(
			model().attributeExists("mobileOperatingSystems"));
		resultActions.andExpect(model().attributeExists("timeZones"));
		resultActions.andExpect(
			model().attributeDoesNotExist("duplicateEmailAddressException"));
		resultActions.andExpect(
			model().attributeExists("invalidEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidPhoneNumberException"));

		assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidPhoneNumber()
		throws Exception {

		setUpUserUtil();

		MockHttpServletRequestBuilder request = buildUpdateMyAccountRequest();

		request.param("user.phoneNumber", "1");

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("userDetails"));
		resultActions.andExpect(model().attributeExists("hours"));
		resultActions.andExpect(
			model().attributeExists("mobileCarrierSuffixes"));
		resultActions.andExpect(
			model().attributeExists("mobileOperatingSystems"));
		resultActions.andExpect(model().attributeExists("timeZones"));
		resultActions.andExpect(
			model().attributeDoesNotExist("duplicateEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));
		resultActions.andExpect(
			model().attributeExists("invalidPhoneNumberException"));

		assertNotUpdatedUser();
	}

	@Test
	public void testUpdateMyAccountWithInvalidUserId() throws Exception {
		setUpInvalidUserUtil();

		UserUtil.addUser("test2@test.com", "password");

		NotificationPreferencesUtil.addNotificationPreferences(
			new NotificationPreferences(_INVALID_USER_ID));

		MockHttpServletRequestBuilder request = buildUpdateMyAccountRequest();

		ResultActions resultActions = this.mockMvc.perform(request);

		resultActions.andExpect(status().isOk());
		resultActions.andExpect(view().name("my_account"));
		resultActions.andExpect(model().attributeExists("userDetails"));
		resultActions.andExpect(model().attributeExists("hours"));
		resultActions.andExpect(
			model().attributeExists("mobileCarrierSuffixes"));
		resultActions.andExpect(
			model().attributeExists("mobileOperatingSystems"));
		resultActions.andExpect(model().attributeExists("timeZones"));
		resultActions.andExpect(
			model().attributeDoesNotExist("duplicateEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidEmailAddressException"));
		resultActions.andExpect(
			model().attributeDoesNotExist("invalidPhoneNumberException"));

		assertNotUpdatedUser();
	}

	@Test
	public void testViewMyAccount() throws Exception {
		setUpUserUtil();

		this.mockMvc.perform(get("/my_account"))
			.andExpect(status().isOk())
			.andExpect(view().name("my_account"))
			.andExpect(forwardedUrl("/WEB-INF/jsp/my_account.jsp"))
			.andExpect(model().attributeExists("userDetails"))
			.andExpect(model().attributeExists("hours"))
			.andExpect(model().attributeExists("mobileCarrierSuffixes"))
			.andExpect(model().attributeExists("mobileOperatingSystems"))
			.andExpect(model().attributeExists("timeZones"));
	}

	private void assertUpdatedUser() throws Exception {
		User user = UserUtil.getUserByUserId(_USER.getUserId());

		Assert.assertEquals("test2@test.com", user.getEmailAddress());
		Assert.assertEquals("2345678901", user.getPhoneNumber());

		NotificationPreferences notificationPreferences =
			NotificationPreferencesUtil.getNotificationPreferencesByUserId(
				_USER.getUserId());

		Assert.assertEquals(
			_USER.getUserId(), notificationPreferences.getUserId());
		Assert.assertTrue(notificationPreferences.isEmailNotification());
		Assert.assertTrue(notificationPreferences.isTextNotification());
		Assert.assertTrue(notificationPreferences.isBasedOnTime());
		Assert.assertEquals(1, notificationPreferences.getStartOfDay());
		Assert.assertEquals(2, notificationPreferences.getEndOfDay());
		Assert.assertEquals("PST", notificationPreferences.getTimeZone());
		Assert.assertTrue(
			notificationPreferences.isWeekdayDayEmailNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekdayDayTextNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekdayNightEmailNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekdayNightTextNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekendDayEmailNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekendDayTextNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekendNightEmailNotification());
		Assert.assertTrue(
			notificationPreferences.isWeekendNightTextNotification());
	}

	private void assertNotUpdatedUser() throws Exception {
		User user = UserUtil.getUserByUserId(_USER.getUserId());

		Assert.assertEquals("test@test.com", user.getEmailAddress());
		Assert.assertNull(user.getPhoneNumber());

		NotificationPreferences notificationPreferences =
			NotificationPreferencesUtil.getNotificationPreferencesByUserId(
				_USER.getUserId());

		Assert.assertEquals(
			_USER.getUserId(), notificationPreferences.getUserId());
		Assert.assertFalse(notificationPreferences.isEmailNotification());
		Assert.assertFalse(notificationPreferences.isTextNotification());
		Assert.assertFalse(notificationPreferences.isBasedOnTime());
		Assert.assertEquals(0, notificationPreferences.getStartOfDay());
		Assert.assertEquals(0, notificationPreferences.getEndOfDay());
		Assert.assertNull(notificationPreferences.getTimeZone());
		Assert.assertFalse(
			notificationPreferences.isWeekdayDayEmailNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekdayDayTextNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekdayNightEmailNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekdayNightTextNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekendDayEmailNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekendDayTextNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekendNightEmailNotification());
		Assert.assertFalse(
			notificationPreferences.isWeekendNightTextNotification());
	}

	private MockHttpServletRequestBuilder buildUpdateMyAccountRequest() {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
			"/my_account");

		request.param("user.userId", String.valueOf(_USER.getUserId()));
		request.param("user.emailAddress", "test2@test.com");
		request.param("user.phoneNumber", "2345678901");

		request.param(
			"notificationPreferences.userId",
			String.valueOf(_USER.getUserId()));
		request.param(
			"notificationPreferences.emailNotification", "true");
		request.param(
			"notificationPreferences.textNotification", "true");
		request.param(
			"notificationPreferences.basedOnTime", "true");
		request.param(
			"notificationPreferences.startOfDay", "1");
		request.param(
			"notificationPreferences.endOfDay", "2");
		request.param(
			"notificationPreferences.timeZone", "PST");
		request.param(
			"notificationPreferences.weekdayDayEmailNotification", "true");
		request.param(
			"notificationPreferences.weekdayDayTextNotification", "true");
		request.param(
			"notificationPreferences.weekdayNightEmailNotification", "true");
		request.param(
			"notificationPreferences.weekdayNightTextNotification", "true");
		request.param(
			"notificationPreferences.weekendDayEmailNotification", "true");
		request.param(
			"notificationPreferences.weekendDayTextNotification", "true");
		request.param(
			"notificationPreferences.weekendNightEmailNotification", "true");
		request.param(
			"notificationPreferences.weekendNightTextNotification", "true");

		return request;
	}

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	private static User _USER;

}