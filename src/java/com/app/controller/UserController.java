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
import com.app.mail.MailSender;
import com.app.mail.MailSenderFactory;
import com.app.model.SearchQuery;
import com.app.model.User;
import com.app.util.PropertiesValues;
import com.app.util.RecaptchaUtil;
import com.app.util.SearchQueryPreviousResultUtil;
import com.app.util.SearchQueryUtil;
import com.app.util.SearchResultUtil;
import com.app.util.StripeUtil;
import com.app.util.UserUtil;
import com.app.util.ValidatorUtil;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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

	@RequestMapping(value ="/contact", method = RequestMethod.GET)
	public String contact(Map<String, Object> model) throws Exception {
		Subject currentUser = SecurityUtils.getSubject();

		if (currentUser.isAuthenticated()) {
			User user = UserUtil.getCurrentUser();

			model.put("emailAddress", user.getEmailAddress());
		}

		model.put("isActive", UserUtil.isCurrentUserActive());

		return "contact";
	}

	@RequestMapping(value ="/contact", method = RequestMethod.POST)
	public String contact(
			String emailAddress, String message, Map<String, Object> model)
		throws Exception {

		MailSender mailSender = MailSenderFactory.getInstance();

		try {
			mailSender.sendContactMessage(emailAddress, message);

			model.put("success", LanguageUtil.getMessage("message-send-success"));
		}
		catch (Exception e) {
			_log.error("Unable to deliver contact message");

			model.put("error", LanguageUtil.getMessage("message-send-fail"));
		}

		return contact(model);
	}

	@RequestMapping(value = "/create_account", method = RequestMethod.GET)
	public String createAccount(
			@ModelAttribute("error")String error, Map<String, Object> model)
		throws DatabaseConnectionException, SQLException {

		Subject currentUser = SecurityUtils.getSubject();

		if (currentUser.isAuthenticated()) {
			return "redirect:my_account";
		}
		else {
			model.put("error", error);
			model.put(
				"exceedsMaximumNumberOfUsers",
				UserUtil.exceedsMaximumNumberOfUsers());
			model.put(
				"stripePublishableKey", PropertiesValues.STRIPE_PUBLISHABLE_KEY);

			return "create_account";
		}
	}

	@RequestMapping(value = "/create_account", method = RequestMethod.POST)
	public String createAccount(
			String emailAddress, String password, String stripeToken,
			HttpServletRequest request, RedirectAttributes redirectAttributes)
		throws Exception {

		if (UserUtil.exceedsMaximumNumberOfUsers()) {
			return "redirect:create_account";
		}

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

		MailSender mailSender = MailSenderFactory.getInstance();

		mailSender.sendWelcomeMessage(emailAddress);

		return logIn(
			emailAddress, password, "home", "", request, redirectAttributes);
	}

	@RequestMapping(value = "/delete_user", method = RequestMethod.POST)
	public String deleteUser(
			String emailAddress, String password,
			RedirectAttributes redirectAttributes)
		throws Exception {

		User currentUser = UserUtil.getCurrentUser();

		try {
			UserUtil.deleteUser(password, currentUser);

			int userId = currentUser.getUserId();

			List<SearchQuery> searchQueries = SearchQueryUtil.getSearchQueries(
				userId);

			for (SearchQuery searchQuery : searchQueries) {
				int searchQueryId = searchQuery.getSearchQueryId();

				SearchQueryPreviousResultUtil.deleteSearchQueryPreviousResults(
					searchQueryId);

				SearchResultUtil.deleteSearchQueryResults(searchQueryId);

				SearchQueryUtil.deleteSearchQuery(userId, searchQueryId);
			}

			StripeUtil.deleteCustomer(currentUser.getCustomerId());
		}
		catch (Exception e) {
			_log.error(
				"Unable to delete user with email address: {}. The current " +
					"user's email address is: {}",
				emailAddress, currentUser.getEmailAddress());

			redirectAttributes.addFlashAttribute(
				"error",
				LanguageUtil.getMessage("user-deletion-failure"));

			return "redirect:my_account";
		}

		MailSender mailSender = MailSenderFactory.getInstance();

		mailSender.sendAccountDeletionMessage(emailAddress);

		return "redirect:log_out";
	}

	@RequestMapping(value = "/delete_subscription", method = RequestMethod.POST)
	public String deleteSubscription(RedirectAttributes redirectAttributes)
		throws Exception {

		User currentUser = UserUtil.getCurrentUser();

		String subscriptionId = currentUser.getSubscriptionId();

		if (ValidatorUtil.isNotNull(subscriptionId) &&
			currentUser.isActive() && !currentUser.isPendingCancellation()) {

			try {
				StripeUtil.deleteSubscription(subscriptionId);

				redirectAttributes.addFlashAttribute(
					"success", LanguageUtil.getMessage("subscription-updated"));

				MailSender mailSender = MailSenderFactory.getInstance();

				mailSender.sendCancellationMessage(
					currentUser.getEmailAddress());
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

	@RequestMapping(value ="/faq", method = RequestMethod.GET)
	public String faq(Map<String, Object> model) throws Exception {
		model.put("isActive", UserUtil.isCurrentUserActive());

		return "faq";
	}

	@RequestMapping(value ="/forgot_password", method = RequestMethod.GET)
	public String forgotPassword(
		@ModelAttribute("success")String success, Map<String, Object> model) {

		model.put("recaptchaSiteKey", PropertiesValues.RECAPTCHA_SITE_KEY);
		model.put("success", success);

		return "forgot_password";
	}

	@RequestMapping(value ="/forgot_password", method = RequestMethod.POST)
	public String forgotPassword(
			String emailAddress,
			@RequestParam(value = "g-recaptcha-response", required = false)
				String recaptchaResponse,
			RedirectAttributes redirectAttributes)
		throws Exception {

		boolean valid = RecaptchaUtil.verifyRecaptchaResponse(
			recaptchaResponse);

		if (valid) {
			User user = UserUtil.getUserByEmailAddress(emailAddress);

			String passwordResetToken = UserUtil.updatePasswordResetToken(
				user.getUserId());

			MailSender mailSender = MailSenderFactory.getInstance();

			mailSender.sendPasswordResetToken(emailAddress, passwordResetToken);
		}

		redirectAttributes.addFlashAttribute(
			"success", LanguageUtil.getMessage("forgot-password-success"));

		return "redirect:forgot_password";
	}

	@RequestMapping(
		value = { "", "/", "/home" },
		method = {RequestMethod.GET, RequestMethod.HEAD}
	)
	public String home(Map<String, Object> model) throws Exception {
		Subject currentUser = SecurityUtils.getSubject();

		if (currentUser.isAuthenticated()) {
			User user = UserUtil.getCurrentUser();

			if (user.isPendingCancellation()) {
				model.put("isActive", true);
				model.put(
					"nextChargeDate",
					LanguageUtil.formatMessage(
						"subscription-will-end-on",
						StripeUtil.getNextChargeDate()));
			}
			else if (user.isActive()) {
				model.put("isActive", true);
				model.put(
					"nextChargeDate",
					LanguageUtil.formatMessage(
						"next-charge-date", StripeUtil.getNextChargeDate()));
			}
			else {
				model.put("isActive", false);
				model.put(
					"nextChargeDate",
					LanguageUtil.getMessage("inactive-account"));
			}

			int emailsSent = user.getEmailsSent();

			if (emailsSent == 1) {
				model.put(
					"emailsSent", LanguageUtil.getMessage("one-email-sent"));
			}
			else {
				model.put(
					"emailsSent",
					LanguageUtil.formatMessage(
						"x-emails-sent", user.getEmailsSent()));
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

	@RequestMapping(value = "/reset_password", method = RequestMethod.GET)
	public String resetPassword(
		@ModelAttribute("error")String error,
		@ModelAttribute("success")String success, Map<String, Object> model) {

		model.put("error", error);
		model.put("success", success);

		return "reset_password";
	}

	@RequestMapping(value = "/reset_password", method = RequestMethod.POST)
	public String resetPassword(
		String emailAddress, String password, String passwordResetToken,
		RedirectAttributes redirectAttributes) {

		try {
			UserUtil.resetPassword(emailAddress, password, passwordResetToken);

			redirectAttributes.addFlashAttribute(
				"success", LanguageUtil.getMessage("password-reset-success"));

			Subject currentUser = SecurityUtils.getSubject();

			currentUser.logout();
		}
		catch (Exception e) {
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("password-reset-fail"));
		}

		return "redirect:reset_password";
	}

	@RequestMapping(value = "/resubscribe", method = RequestMethod.POST)
	public String resubscribe(
			String stripeToken, RedirectAttributes redirectAttributes)
		throws DatabaseConnectionException, SQLException {

		User currentUser = UserUtil.getCurrentUser();

		String subscriptionId = currentUser.getSubscriptionId();

		if (ValidatorUtil.isNotNull(subscriptionId) &&
			(!currentUser.isActive() || currentUser.isPendingCancellation())) {

			try {
				StripeUtil.resubscribe(
					currentUser.getCustomerId(), subscriptionId, stripeToken);

				redirectAttributes.addFlashAttribute(
					"success", LanguageUtil.getMessage("subscription-updated"));

				MailSender mailSender = MailSenderFactory.getInstance();

				mailSender.sendResubscribeMessage(
					currentUser.getEmailAddress());
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

	@RequestMapping(value="/stripe", method=RequestMethod.POST)
	public void stripeWebhookEndpoint(
			@RequestBody String stripeJsonEvent, HttpServletResponse response)
		throws Exception {

		StripeUtil.handleStripeEvent(stripeJsonEvent);

		response.setStatus(HttpStatus.SC_OK);
	}

	@RequestMapping(value = "/email_unsubscribe", method = RequestMethod.GET)
	public void unsubscribeFromEmailNotifications(
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

		return;
	}

	@RequestMapping(value = "/update_subscription", method = RequestMethod.POST)
	public String updateSubscription(
		String stripeToken, RedirectAttributes redirectAttributes) {

		try {
			User currentUser = UserUtil.getCurrentUser();

			StripeUtil.updateSubscription(
				stripeToken, currentUser.getCustomerId());

			redirectAttributes.addFlashAttribute(
				"success", LanguageUtil.getMessage("subscription-updated"));

			MailSender mailSender = MailSenderFactory.getInstance();

			mailSender.sendCardDetailsMessage(currentUser.getEmailAddress());
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
				user.getEmailAddress(), user.getCurrentPassword(),
				user.getNewPassword(), user.getPreferredDomain(),
				user.isEmailNotification());

			StripeUtil.updateCustomerEmailAddress();

			redirectAttributes.addFlashAttribute(
				"success", LanguageUtil.getMessage("account-update-success"));
		}
		catch (CredentialsException ce) {
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("incorrect-password"));
		}
		catch (DuplicateEmailAddressException deae) {
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("duplicate-email-address"));
		}
		catch (InvalidEmailAddressException ieae) {
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("invalid-email-address"));
		}
		catch (PasswordLengthException ple) {
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("invalid-password-length"));
		}
		catch (Exception e) {
			redirectAttributes.addFlashAttribute(
				"error", LanguageUtil.getMessage("account-update-fail"));
		}

		return "redirect:my_account";
	}

	@RequestMapping(value = "/my_account", method = RequestMethod.GET)
	public String viewMyAccount(
			@ModelAttribute("error")String error,
			@ModelAttribute("success")String success, Map<String, Object> model,
			HttpServletRequest request)
		throws DatabaseConnectionException, SQLException {

		model.put("error", error);
		model.put(
			"info", WebUtils.getSessionAttribute(request, "info"));
		model.put("isActive", UserUtil.isCurrentUserActive());
		model.put("success", success);
		model.put("user", UserUtil.getCurrentUser());
		model.put("preferredDomains", UserUtil.getPreferredDomains());
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