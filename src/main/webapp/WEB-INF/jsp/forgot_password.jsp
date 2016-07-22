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
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>
		<script src="https://www.google.com/recaptcha/api.js" type="text/javascript"></script>
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
					<li><a href="log_in">Log In</a></li>
					<li><a href="create_account" class="button special">Sign Up</a></li>
				</ul>
			</nav>
		</header>

		<section id="banner" class="minor">
			<div class="inner">
				<h2>Forgot Password</h2>
			</div>
		</section>

		<div class="padding-top">
			<c:choose>
				<c:when test="${not empty success}">
					<div id="success">
						<i class="icon fa-check-circle"></i>
						${success}
					</div>
				</c:when>
				<c:otherwise>
					<div id="info">
						<i class="icon fa-info-circle"></i>
						A password reset token will be sent to your email address. Please use this token to reset your password and note that the token is only valid for one hour.
					</div>
				</c:otherwise>
			</c:choose>
		</div>

		<div id="user-details">
			<form:form action="forgot_password" commandName="forgot_password" method="post">
				<div>
					<b>Email Address: </b><input id="emailAddress" name="emailAddress" type="text" />
				</div>
				<div class="padding-top g-recaptcha" data-sitekey="${recaptchaSiteKey}"></div>
				<div class="padding-top">
					<input class="button special" type="submit" value="Send Password Reset Token" />
				</div>
			</form:form>
		</div>

		<%@ include file="footer.jspf" %>
	</body>
</html>