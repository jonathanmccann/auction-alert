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
import com.app.exception.PasswordLengthException;
import com.app.exception.RecaptchaException;
import com.app.language.LanguageUtil;
import com.app.model.User;
import com.app.util.PropertiesValues;
import com.app.util.RecaptchaUtil;
import com.app.util.StripeUtil;
import com.app.util.UserUtil;
import com.app.util.ValidatorUtil;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

/**
 * @author Jonathan McCann
 */
@Controller
public class UserController {

	@RequestMapping(value = "/create_account", method = RequestMethod.GET)
	public String createAccount(
		@ModelAttribute("error")String error, Map<String, Object> model) {

		model.put("error", error);
		model.put(
			"stripePublishableKey", PropertiesValues.STRIPE_PUBLISHABLE_KEY);

		return "create_account";
	}

	@RequestMapping(value = "/create_account", method = RequestMethod.POST)
	public String createAccount(
			String emailAddress, String password, String stripeToken,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
		throws DatabaseConnectionException, SQLException {

		User user = null;

		try {
			user = UserUtil.addUser(emailAddress, password);
		}
		catch (DuplicateEmailAddressException deae) {
			_log.error(deae.getMessage());

			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("duplicate-email-address"));

			return "redirect:create_account";
		}
		catch (InvalidEmailAddressException ieae) {
			_log.error(ieae.getMessage());

			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("invalid-email-address"));

			return "redirect:create_account";
		}
		catch (PasswordLengthException ple) {
			_log.error(ple.getMessage());

			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("invalid-password-length"));

			return "redirect:create_account";
		}

		try {
			StripeUtil.createSubscription(
				user.getUserId(), emailAddress, stripeToken);
		}
		catch (Exception e) {
			_log.error(e.getMessage());

			redirectAttributes.addFlashAttribute(
				"error",
				LanguageUtil.getMessage("incorrect-payment-information"));

			UserUtil.deleteUserByUserId(user.getUserId());

			return "redirect:create_account";
		}

		return logIn(
			emailAddress, password, "home", "", request, redirectAttributes);
	}

	@RequestMapping(value = "/delete_subscription", method = RequestMethod.POST)
	public String deleteSubscription(RedirectAttributes redirectAttributes)
		throws DatabaseConnectionException, SQLException {

		User currentUser = UserUtil.getCurrentUser();

		String subscriptionId = currentUser.getSubscriptionId();

		if (ValidatorUtil.isNotNull(subscriptionId) &&
			currentUser.isActive() && !currentUser.isPendingCancellation()) {

			try {
				StripeUtil.deleteSubscription(subscriptionId);
			}
			catch (Exception e) {
				_log.error(e.getMessage());

				redirectAttributes.addFlashAttribute(
					"error",
					LanguageUtil.getMessage("subscription-cancellation"));
			}
		}
		else {
			redirectAttributes.addFlashAttribute(
				"error",
				LanguageUtil.getMessage("subscription-already-cancelled"));
		}

		return "redirect:my_account";
	}

	@RequestMapping(value = { "", "/", "/home" }, method = RequestMethod.GET)
	public String home(Map<String, Object> model) throws Exception {
		Subject currentUser = SecurityUtils.getSubject();

		if (currentUser.isAuthenticated()) {
			User user = UserUtil.getCurrentUser();

			if (user.isPendingCancellation()) {
				model.put(
					"nextChargeDate",
					LanguageUtil.formatMessage(
						"subscription-will-end-on",
						StripeUtil.getNextChargeDate()));
			}
			else if (user.isActive()) {
				model.put(
					"nextChargeDate",
					LanguageUtil.formatMessage(
						"next-charge-date", StripeUtil.getNextChargeDate()));
			}
			else {
				model.put(
					"nextChargeDate",
					LanguageUtil.getMessage("inactive-account"));
			}
		}

		return "home";
	}

	@RequestMapping(value = "/log_in", method = RequestMethod.GET)
	public String logIn(
			@ModelAttribute("error")String error, Map<String, Object> model,
			HttpServletRequest request)
		throws Exception {

		Subject currentUser = SecurityUtils.getSubject();

		if (currentUser.isAuthenticated()) {
			return home(model);
		}

		model.put("error", error);
		model.put(
			"redirect", WebUtils.getSessionAttribute(request, "redirect"));

		int loginAttempts = getLoginAttempts();

		if (loginAttempts >= PropertiesValues.LOGIN_ATTEMPT_LIMIT) {
			model.put(
				"recaptchaSiteKey", PropertiesValues.RECAPTCHA_SITE_KEY);
		}

		return "log_in";
	}

	@RequestMapping(value = "/log_in", method = RequestMethod.POST)
	public String logIn(
		String emailAddress, String password, String redirect,
		@RequestParam(value = "g-recaptcha-response", required = false)
			String recaptchaResponse,
		HttpServletRequest request, RedirectAttributes redirectAttributes) {

		Subject currentUser = SecurityUtils.getSubject();

		if (!currentUser.isAuthenticated()) {
			Session session = currentUser.getSession();

			int loginAttempts = getLoginAttempts(session);

			session.setAttribute("loginAttempts", loginAttempts + 1);

			try {
				if (loginAttempts >= PropertiesValues.LOGIN_ATTEMPT_LIMIT) {
					boolean valid = RecaptchaUtil.verifyRecaptchaResponse(
						recaptchaResponse);

					if (!valid) {
						throw new RecaptchaException();
					}
				}

				UsernamePasswordToken token = new UsernamePasswordToken(
					emailAddress, password);

				currentUser.login(token);

				_log.debug(
					"User with email address {} logged in successfully",
					emailAddress);

				User user = UserUtil.getUserByEmailAddress(emailAddress);

				currentUser.getSession().setAttribute(
					"userId", user.getUserId());

				UserUtil.updateUserLoginDetails(
					new Timestamp(System.currentTimeMillis()),
					request.getRemoteAddr());
			}
			catch (Exception e) {
				if (e instanceof RecaptchaException) {
					redirectAttributes.addFlashAttribute(
						"error", LanguageUtil.getMessage("recaptcha-failure"));
				}
				else {
					redirectAttributes.addFlashAttribute(
						"error", LanguageUtil.getMessage("log-in-failure"));

					if (e instanceof UnknownAccountException) {
						_log.error(
							"There is no user with emailAddress {}",
							emailAddress);
					}
					else if (e instanceof IncorrectCredentialsException) {
						_log.error(
							"Password for emailAddress {} is incorrect",
							emailAddress);
					}
					else if (e instanceof LockedAccountException) {
						_log.error(
							"The account associated with emailAddress {} is " +
								"locked",
							emailAddress);
					}
				}

				return "redirect:log_in";
			}
		}

		if (ValidatorUtil.isNull(redirect)) {
			return "redirect:home";
		}
		else {
			return "redirect:" + redirect;
		}
	}

	@RequestMapping(value = "/log_out", method = RequestMethod.GET)
	public String logOut() {
		Subject currentUser = SecurityUtils.getSubject();

		currentUser.logout();

		return "redirect:home";
	}

	@RequestMapping(value = "/resubscribe", method = RequestMethod.POST)
	public String resubscribe(RedirectAttributes redirectAttributes)
		throws DatabaseConnectionException, SQLException {

		User currentUser = UserUtil.getCurrentUser();

		String subscriptionId = currentUser.getSubscriptionId();

		if (ValidatorUtil.isNotNull(subscriptionId) &&
			(!currentUser.isActive() || currentUser.isPendingCancellation())) {

			try {
				StripeUtil.resubscribe(
					currentUser.getCustomerId(), subscriptionId);
			}
			catch (Exception e) {
				_log.error(e.getMessage());

				redirectAttributes.addFlashAttribute(
					"error",
					LanguageUtil.getMessage(
						"subscription-resubscribe-failure"));
			}
		}
		else {
			redirectAttributes.addFlashAttribute(
				"error",
				LanguageUtil.getMessage("subscription-resubscribe-failure"));
		}

		return "redirect:my_account";
	}

	@RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
	public String unsubscribeFromEmailNotifications(
			String emailAddress, String unsubscribeToken,
			Map<String, Object> model)
		throws Exception {

		User user = UserUtil.getUserByEmailAddress(emailAddress);

		String userUnsubscribeToken = user.getUnsubscribeToken();

		if (userUnsubscribeToken.equals(unsubscribeToken)) {
			UserUtil.unsubscribeUserFromEmailNotifications(emailAddress);

			model.put(
				"unsubscribeMessage",
				LanguageUtil.getMessage("unsubscribe-successful"));
		}
		else {
			model.put(
				"unsubscribeMessage",
				LanguageUtil.getMessage("unsubscribe-failure"));
		}

		return "unsubscribe";
	}

	@RequestMapping(value = "/update_subscription", method = RequestMethod.POST)
	public String updateSubscription(
			String stripeToken, RedirectAttributes redirectAttributes)
		throws Exception {

		try {
			StripeUtil.updateSubscription(stripeToken);
		}
		catch (Exception e) {
			_log.error(e.getMessage());

			redirectAttributes.addFlashAttribute(
				"error",
				LanguageUtil.getMessage("incorrect-payment-information"));
		}

		return "redirect:my_account";
	}

	@RequestMapping(value = "/my_account", method = RequestMethod.POST)
	public String updateMyAccount(
			@ModelAttribute("user")User user,
			RedirectAttributes redirectAttributes)
		throws DatabaseConnectionException, SQLException {

		try {
			UserUtil.updateUserDetails(
				user.getEmailAddress(), user.isEmailNotification());
		}
		catch (DuplicateEmailAddressException deae) {
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("duplicate-email-address"));
		}
		catch (InvalidEmailAddressException ieae) {
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("invalid-email-address"));
		}

		return "redirect:my_account";
	}

	@RequestMapping(value = "/my_account", method = RequestMethod.GET)
	public String viewMyAccount(
			@ModelAttribute("error")String error, Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		model.put("error", error);
		model.put("user", UserUtil.getCurrentUser());
		model.put(
			"stripePublishableKey", PropertiesValues.STRIPE_PUBLISHABLE_KEY);

		return "my_account";
	}

	private static int getLoginAttempts() {
		Subject currentUser = SecurityUtils.getSubject();

		Session session = currentUser.getSession();

		return getLoginAttempts(session);
	}

	private static int getLoginAttempts(Session session) {
		try {
			return (int)session.getAttribute("loginAttempts");
		}
		catch (Exception e) {
			return 0;
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		UserController.class);

}