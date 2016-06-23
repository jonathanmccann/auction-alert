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

package com.app.controller;

import com.app.exception.DatabaseConnectionException;
import com.app.exception.DuplicateEmailAddressException;
import com.app.exception.InvalidEmailAddressException;
import com.app.language.LanguageUtil;
import com.app.model.User;
import com.app.util.PropertiesValues;
import com.app.util.UserUtil;
import com.app.util.ValidatorUtil;

import com.stripe.model.Customer;
import com.stripe.model.Subscription;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jonathan McCann
 */
@Controller
public class UserController {

	@RequestMapping(value = "/create_account", method = RequestMethod.GET)
	public String createAccount() throws Exception {
		return "create_account";
	}

	@RequestMapping(value = "/create_account", method = RequestMethod.POST)
	public String createAccount(
			String emailAddress, String password, Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		User user = null;

		try {
			user = UserUtil.addUser(emailAddress, password);
		}
		catch (DuplicateEmailAddressException deae) {
			model.put(
				"error", LanguageUtil.getMessage("duplicate-email-address"));

			return "create_account";
		}
		catch (InvalidEmailAddressException ieae) {
			model.put(
				"error", LanguageUtil.getMessage("invalid-email-address"));

			return "create_account";
		}

		return logIn(emailAddress, password, model);
	}

	@RequestMapping(value = "/create_subscription", method = RequestMethod.POST)
	public String createSubscription(
			String stripeToken, String stripeEmail, Map<String, Object> model)
		throws Exception {

		int userId = UserUtil.getCurrentUserId();

		User currentUser = UserUtil.getUserByUserId(userId);

		if (!currentUser.getEmailAddress().equalsIgnoreCase(stripeEmail)) {
			model.put(
				"error",
				LanguageUtil.getMessage("invalid-stripe-email-address"));

			return viewMyAccount(model);
		}

		if (currentUser.isActive()) {
			model.put(
				"error", LanguageUtil.getMessage("user-already-active"));

			return viewMyAccount(model);
		}

		if (ValidatorUtil.isNotNull(currentUser.getCustomerId())) {
			model.put(
				"error", LanguageUtil.getMessage("existing-subscription"));

			return viewMyAccount(model);
		}

		Map<String, Object> customerParams = new HashMap<>();

		customerParams.put("email", stripeEmail);
		customerParams.put("plan", PropertiesValues.STRIPE_SUBSCRIPTION_PLAN_ID);
		customerParams.put("source", stripeToken);

		Customer customer = null;

		try {
			customer = Customer.create(customerParams);

			Subscription subscription =
				customer.getSubscriptions().getData().get(0);

			UserUtil.updateUserSubscription(
				customer.getId(), subscription.getId(), true, false);
		}
		catch (Exception e) {
			_log.error(e.getMessage());

			model.put(
				"error",
				LanguageUtil.getMessage("incorrect-payment-information"));
		}

		return viewMyAccount(model);
	}

	@RequestMapping(value = "/delete_subscription", method = RequestMethod.POST)
	public String deleteSubscription(Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		User currentUser = UserUtil.getCurrentUser();

		String subscriptionId = currentUser.getSubscriptionId();

		if (ValidatorUtil.isNotNull(subscriptionId) &&
			currentUser.isActive() && !currentUser.isPendingCancellation()) {

			Subscription subscription = null;

			try {
				subscription = Subscription.retrieve(subscriptionId);

				Map<String, Object> parameters = new HashMap<>();

				parameters.put("at_period_end", true);

				subscription.cancel(parameters);

				UserUtil.updateUserSubscription(
					currentUser.getCustomerId(),
					currentUser.getSubscriptionId(), true, true);
			}
			catch (Exception e) {
				_log.error(e.getMessage());

				model.put(
					"error",
					LanguageUtil.getMessage("subscription-cancellation"));
			}
		}
		else {
			model.put(
				"error",
				LanguageUtil.getMessage("subscription-already-cancelled"));
		}

		return viewMyAccount(model);
	}

	@RequestMapping(value = "/log_in", method = RequestMethod.POST)
	public String logIn(
		String emailAddress, String password, Map<String, Object> model) {

		Subject currentUser = SecurityUtils.getSubject();

		if (!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(
				emailAddress, password);

			try {
				currentUser.login(token);

				_log.debug(
					"User with email address {} logged in successfully",
					emailAddress);

				User user = UserUtil.getUserByEmailAddress(emailAddress);

				currentUser.getSession().setAttribute(
					"userId", user.getUserId());
			}
			catch (Exception e) {
				model.put(
					"error", LanguageUtil.getMessage("authentication-failure"));

				if (e instanceof UnknownAccountException) {
					_log.error(
						"There is no user with emailAddress {}",
						token.getPrincipal());
				}
				else if (e instanceof IncorrectCredentialsException) {
					_log.error(
						"Password for emailAddress {} is incorrect",
						token.getPrincipal());
				}
				else if (e instanceof LockedAccountException) {
					_log.error(
						"The account associated with emailAddress {} is locked",
						token.getPrincipal());
				}
			}
		}

		return "home";
	}

	@RequestMapping(value = "/log_out", method = RequestMethod.GET)
	public String logOut() {
		Subject currentUser = SecurityUtils.getSubject();

		currentUser.logout();

		return "home";
	}

	@RequestMapping(value = "/my_account", method = RequestMethod.POST)
	public String updateMyAccount(
			@ModelAttribute("user")User user, Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		try {
			UserUtil.updateUserDetails(
				user.getEmailAddress(), user.isEmailNotification());
		}
		catch (DuplicateEmailAddressException deae) {
			model.put(
				"error", LanguageUtil.getMessage("duplicate-email-address"));
		}
		catch (InvalidEmailAddressException ieae) {
			model.put(
				"error", LanguageUtil.getMessage("invalid-email-address"));
		}

		model.put("user", user);

		return "my_account";
	}

	@RequestMapping(value = "/my_account", method = RequestMethod.GET)
	public String viewMyAccount(Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		model.put("user", UserUtil.getCurrentUser());
		model.put(
			"stripePublishableKey", PropertiesValues.STRIPE_PUBLISHABLE_KEY);

		return "my_account";
	}

	private static final Logger _log = LoggerFactory.getLogger(
		UserController.class);

}