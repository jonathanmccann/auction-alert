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
		<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/tooltipster.css" />" rel="stylesheet">
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-tooltipster-3.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-validate-1.14.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/main.js" />" type="text/javascript"></script>
	</head>
	<body>
		<div>
			<form:form action="/my_account" commandName="user" id="updateUserForm" method="post">
				<form:input path="userId" type="hidden" value="${userId}" />

				<h2>My Account</h2>
				<div>
					<h3>My Details:</h3>

					Email Address <form:input path="emailAddress" value="${emailAddress}" />

					<c:if test="${not empty duplicateEmailAddressException}">
						${duplicateEmailAddressException}
					</c:if>

					<c:if test="${not empty invalidEmailAddressException}">
						${invalidEmailAddressException}
					</c:if>

					</br>

					<label for="emailNotification">Send Email Notifications</label>
					<form:checkbox id="emailNotification" path="emailNotification" value="${emailNotification}" />
				</div>
				<div>
					<input id="updateUserSubmit" type="submit" value="Update User" />
				</div>
			</form:form>

			<c:if test="${not user.active}">
				<form:form action="/create_subscription" method="POST">
					<script
						src="https://checkout.stripe.com/checkout.js" class="stripe-button"
						data-key="${stripePublishableKey}"
						data-image="images/marketplace.png"
						data-name="eBay Searcher"
						data-description="Subscription ($9.99 per month)"
						data-amount="999"
						data-label="Subscribe"
						data-allow-remember-me="false"
						data-email="${user.emailAddress}" >
					</script>

					<c:if test="${not empty invalidEmailAddressException}">
						${invalidEmailAddressException}
					</c:if>

					<c:if test="${not empty userActiveException}">
						${userActiveException}
					</c:if>

					<c:if test="${not empty paymentException}">
						${paymentException}
					</c:if>
				</form:form>
			</c:if>

			<div align="center">
				<a href="add_search_query">Add a Search Query</a> | <a href="view_search_queries">View Search Queries</a> | <a href="view_search_query_results">View Search Query Results</a> <br> <br>
				My Account | <a href="log_out">Log Out</a>
			</div>
		</div>
	</body>
</html>