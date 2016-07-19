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
		<link href="<c:url value="/resources/css/tooltipster.css" />" rel="stylesheet">
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-tooltipster-3.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-validate-1.14.0.min.js" />" type="text/javascript"></script>
		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/main.js" />" type="text/javascript"></script>
		<script src="https://checkout.stripe.com/checkout.js" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/subscription.js" />" type="text/javascript"></script>
		<noscript>
			<link rel="stylesheet" href="/resources/css/skel.css" />
			<link rel="stylesheet" href="/resources/css/style.css" />
			<link rel="stylesheet" href="/resources/css/style-xlarge.css" />
		</noscript>
	</head>
	<body>
		<header id="header" class="skel-layers-fixed">
			<h1><a href="/home">eBay Searcher</a></h1>
			<nav id="nav">
				<ul>
					<c:if test="${isActive}">
						<li><a href="add_search_query">Add Search Query</a></li>
						<li><a href="view_search_queries">Search Queries and Results</a></li>
						<li><a href="monitor">Monitor</a></li>
					</c:if>

					<li><a href="my_account">My Account</a></li>
					<li><a href="log_out" class="button special">Log Out</a></li>
				</ul>
			</nav>
		</header>

		<section id="banner" class="minor">
			<div class="inner">
				<h2>My Account</h2>
			</div>
		</section>

		<div id="user-details">
			<form:form action="/my_account" commandName="user" id="updateUserForm" method="post">
				<c:if test="${not empty error}">
					<div id="error">
						<i class="icon fa-times-circle"></i>
						${error}
					</div>
				</c:if>

				<c:if test="${not empty info}">
					<div id="info">
						<i class="icon fa-info-circle"></i>
						${info}
					</div>
				</c:if>

				<c:if test="${not empty success}">
					<div id="success">
						<i class="icon fa-check-circle"></i>
						${success}
					</div>
				</c:if>

				<div>
					<b>Email Address: </b><form:input path="emailAddress" value="${emailAddress}" />
				</div>
				<div class="padding-top">
					<form:checkbox id="emailNotification" label="Send Email Notifications" path="emailNotification" value="${emailNotification}" />
				</div>
				<div class="padding-top">
					<input class="button special" id="updateUserSubmit" type="submit" value="Update User" />
				</div>
			</form:form>

			<c:choose>
				<c:when test="${(not empty user.subscriptionId) && ((not user.active) || (user.pendingCancellation))}">
					<form:form action="/resubscribe" id="resubscribeForm" method="POST">
						<input id="stripePublishableKey" type="hidden" value="${stripePublishableKey}"/>

						<input class="button special" id="resubscribe" title="If your current billing period has ended, you will be billed immediately. Otherwise, you be will be charged at the beginning of the next billing period." type="submit" value="Resubscribe" />
					</form:form>
				</c:when>
				<c:otherwise>
					<form:form action="/update_subscription" class="inline-form" id="updateBillingForm" method="POST">
						<input id="stripePublishableKey" type="hidden" value="${stripePublishableKey}"/>

						<input class="button special" id="updateBillingSubmit" title="Change the credit card used for subscription billing." type="submit" value="Update Billing Details" />
					</form:form>
					<form:form action="/delete_subscription" class="inline-form" method="POST">
						<input class="button special" id="cancelSubscription" title="Your subscription will be cancelled and no further billing will occur. You will be able to use the site for the remainder of your current billing period." type="submit" value="Cancel Subscription" />
					</form:form>
				</c:otherwise>
			</c:choose>
		</div>

		<%@ include file="footer.jspf" %>
	</body>
</html>