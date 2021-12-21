<%--
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
--%>

<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<title>My Account</title>

		<script src="/resources/js/jquery-2.1.3.min.js" type="text/javascript"></script>

		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>

		<noscript>
			<link rel="stylesheet" href="/resources/css/skel.css" />
			<link rel="stylesheet" href="/resources/css/style.css" />
			<link rel="stylesheet" href="/resources/css/style-xlarge.css" />
		</noscript>
	</head>
	<body>
		<header class="skel-layers-fixed" id="header">
			<h1><a href="/home">Auction Alert</a></h1>
			<nav id="nav">
				<ul>
					<shiro:guest>
						<li><a href="log_in" id="loginLink">Log In</a></li>
						<li><a href="create_account" class="button special">Sign Up</a></li>
					</shiro:guest>
					<shiro:user>
						<c:if test="${isActive}">
							<li><a href="add_search_query">Add Search Query</a></li>
							<li><a href="view_search_queries">Search Queries and Results</a></li>
						</c:if>

						<li><a href="my_account">My Account</a></li>
						<li><a href="log_out" class="button special">Log Out</a></li>
					</shiro:user>
				</ul>
			</nav>
		</header>

		<section class="minor">
			<h1>My Account</h1>

			<div class="sub-heading-indent">
				<h5><a href="/faq">FAQ Home</a></h5>
			</div>
		</section>

		<section class="wrapper style1">
			<div class="container small">
				<div>
					<span class="anchor" id="credit_card"></span>
					<h2>How do I change my credit card details?</h2>
					<p class="faq-paragraph">
						To change your credit card, please go to <a href="/my_account" target="_blank">your account</a> and then click on the <b>Update Billing Details</b> button.
						You will then be presented with the Stripe dialog box and you can reset your card number, expiration date, CCV, and zip code.
						Please note that all of the information must be updated at once, so you are unable to simply change the zip code.
					</p>

					<span class="anchor" id="cancel"></span>
					<h2>How do I cancel my subscription?</h2>
					<p class="faq-paragraph">
						To cancel your subscription, please go to <a href="/my_account" target="_blank">your account</a> and then click on the <b>Cancel Subscription</b> button.
						Your subscription will be immediately cancelled, you will not be charged again, and you will lose access to the application once the current month's period has ended.<br><br>
						To resubscribe, you can simply click on the <b>Resubscribe</b> button within your account to re-add your payment details and regain access to the site.
						When resubscribing, you will not be charged immediately, rather it will follow the previous billing cycle.
						If you subscribed on the 5th of February, cancelled your subscription on the 10th of February, and resubscribed on the 15th of February, you will be charged only on the 5th of February and the 5th of March and you will not see any lapse in your ability to use the application.
					</p>

					<span class="anchor" id="account"></span>
					<h2>How do I change my account details?</h2>
					<p class="faq-paragraph">
						You are able to change these details by clicking the <b>Update User</b> button on <a href="/my_account" target="_blank">your account</a> page.
						Updating your password requires entering your current password and the new password you wish to use.
					</p>

					<span class="anchor" id="notifications"></span>
					<h2>How do I turn off email notifications?</h2>
					<p class="faq-paragraph">
						You can uncheck the option <b>Send Email Notifications</b> if you wish to no longer receive the results via email.
						The queries will still be searched and the results can be viewing on <a href="/view_search_queries" target="_blank">search queries</a> page.
					</p>

					<span class="anchor" id="safety"></span>
					<h2>How safe is my credit card and password?</h2>
					<p class="faq-paragraph">
						When submitting your credit card information, it is transmitted securely to Stripe. Your details are never seen by the application nor are they stored locally.
						Stripe then returns a unique token for the application to set up your account. For more information about how Stripe stores your information, please see <a href="https://stripe.com/docs/security/stripe" target="_blank">Stripe's documentation</a>.<br><br>
						Your password is both encrypted and salted so that even if an attacker gains access to the database, your password will not be revealed.
					</p>

					<span class="anchor" id="domain"></span>
					<h2>What is the preferred domain?</h2>
					<p class="faq-paragraph">
						The preferred domain is the domain that you wish to view your results on. If you are from the United States, then you would choose the <b>.com</b> domain.
						Likewise, if you are from the United Kingdom, then you would choose the <b>.co.uk</b> domain.
						All results will use this domain so that you are always directed to your preferred location for purchasing.
					</p>

					<span class="anchor" id="currency"></span>
					<h2>What currency are the prices in?</h2>
					<p class="faq-paragraph">
						The currency is determined by your preferred domain. If you have chosen <b>.com</b> for your preferred domain, then all prices will be in <b>USD</b>.
						If you are searching for items from a location other than your preferred location, the currencies will be converted to your local currency.
					</p>
				</div>
			</div>
		</section>

		<%@ include file="footer.jspf" %>
	</body>
</html>