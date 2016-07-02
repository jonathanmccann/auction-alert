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
		<script src="<c:url value="/resources/js/stripe.js" />" type="text/javascript"></script>
		<script src="https://checkout.stripe.com/checkout.js" type="text/javascript"></script>
	</head>
	<body>
		<div>
			<h2>Create Account</h2>
			<h3>My Details:</h3>

			<shiro:guest>
				<input id="stripePublishableKey" type="hidden" value="${stripePublishableKey}"/>

				<form:form action="create_account" commandName="createAccount" id="createAccountForm" method="post">
					<div>
						<b>Email Address: </b><input id="emailAddress" name="emailAddress" />

						<c:if test="${not empty error}">
							${error}
						</c:if>

						<br>

						<b>Password: </b><input id="password" name="password" type="password" />

						<br>

						<input id="createAccountSubmit" type="submit" value="Subscribe" />
					</div>
				</form:form>
			</shiro:guest>
		</div>
	</body>
</html>