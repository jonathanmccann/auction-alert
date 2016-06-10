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
import com.app.model.User;
import com.app.util.StripeCustomerUtil;
import com.app.util.PropertiesValues;
import com.app.util.UserUtil;

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
				"duplicateEmailAddressException",
				"This email address already exists. Please try again.");

			return "create_account";
		}
		catch (InvalidEmailAddressException ieae) {
			model.put(
				"invalidEmailAddressException",
				"This email address is invalid. Please try again.");

			return "create_account";
		}

		return logIn(emailAddress, password, model);
	}

	@RequestMapping(value = "/create_subscription", method = RequestMethod.POST)
	public String createSubscription(
			String stripeToken, String stripeEmail, Map<String, Object> model)
		throws Exception {

		User currentUser = UserUtil.getUserByUserId(UserUtil.getCurrentUserId());

		if (!currentUser.getEmailAddress().equalsIgnoreCase(stripeEmail)) {
			model.put(
				"invalidEmailAddressException",
				"Please confirm your email address and the one submitted to " +
					"Stripe are the same."
			);

			return viewMyAccount(model);
		}

		if (currentUser.isActive()) {
			model.put(
				"userActiveException", "You are already an active user.");

			return viewMyAccount(model);
		}

		Map<String, Object> customerParams = new HashMap<>();

		customerParams.put("email", stripeEmail);
		customerParams.put("plan", PropertiesValues.STRIPE_SUBSCRIPTION_PLAN_ID);
		customerParams.put("source", stripeToken);

		Customer customer = null;

		try {
			customer = Customer.create(customerParams);
		}
		catch (Exception e) {
			_log.error(e.getMessage());

			model.put(
				"paymentException",
				"Please check your payment information and try again. If the " +
					"issue persists, please contact the administrator.");
		}

		if (customer != null) {
			Subscription subscription =
				customer.getSubscriptions().getData().get(0);

			StripeCustomerUtil.addCustomer(
				currentUser.getUserId(), customer.getId(), subscription.getId());

			currentUser.setActive(true);

			UserUtil.updateUser(currentUser);
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
					"authenticationError",
					"Authentication failed. Please try again.");

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

		if (user.getUserId() != UserUtil.getCurrentUserId()) {
			return viewMyAccount(model);
		}

		try {
			UserUtil.updateUser(user);
		}
		catch (DuplicateEmailAddressException deae) {
			model.put(
				"duplicateEmailAddressException",
				"This email address already exists. Please try again.");
		}
		catch (InvalidEmailAddressException ieae) {
			model.put(
				"invalidEmailAddressException",
				"This email address is invalid. Please try again.");
		}

		model.put("user", user);

		return "my_account";
	}

	@RequestMapping(value = "/my_account", method = RequestMethod.GET)
	public String viewMyAccount(Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		model.put("user", UserUtil.getUserByUserId(UserUtil.getCurrentUserId()));
		model.put(
			"stripePublishableKey", PropertiesValues.STRIPE_PUBLISHABLE_KEY);

		return "my_account";
	}

	private static final Logger _log = LoggerFactory.getLogger(
		UserController.class);

}