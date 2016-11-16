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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<title>Forgot Password</title>

		<script src="/resources/js/jquery-2.1.3.min.js" type="text/javascript"></script>

		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>

		<script src="https://www.google.com/recaptcha/api.js" type="text/javascript"></script>

		<noscript>
			<link href="/resources/css/skel.css" rel="stylesheet" />
			<link href="/resources/css/style.css" rel="stylesheet" />
			<link href="/resources/css/style-xlarge.css" rel="stylesheet" />
		</noscript>
	</head>

	<body>
		<header class="skel-layers-fixed" id="header">
			<h1><a href="/home">Auction Alert</a></h1>
			<nav id="nav">
				<ul>
					<li><a href="log_in">Log In</a></li>
					<li><a href="create_account" class="button special">Sign Up</a></li>
				</ul>
			</nav>
		</header>

		<section class="minor" id="banner">
			<div class="inner">
				<h2>Forgot Password</h2>
			</div>
		</section>

		<div id="user-details">
			<c:choose>
				<c:when test="${not empty success}">
					<div id="success">
						<i class="fa-check-circle icon"></i>
						${success}
					</div>
				</c:when>
				<c:otherwise>
					<div id="info">
						<i class="fa-info-circle icon"></i>
						A password reset token will be sent to your email address.
					</div>
				</c:otherwise>
			</c:choose>

			<form:form action="forgot_password" commandName="forgot_password" method="post">
				<div>
					<b>Email Address: </b><input id="emailAddress" name="emailAddress" type="email" />
				</div>

				<div class="g-recaptcha padding-top" data-sitekey="${recaptchaSiteKey}"></div>
				<div class="padding-top">
					<input class="button special" type="submit" value="Send Password Reset Token" />
				</div>
			</form:form>
		</div>

		<%@ include file="footer.jspf" %>
	</body>
</html>