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
		<title>Log In</title>
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
			<h1><a href="home">Auction Alert</a></h1>
			<nav id="nav">
				<ul>
					<li><a href="log_in" id="loginLink">Log In</a></li>
					<li><a href="create_account" class="button special">Sign Up</a></li>
				</ul>
			</nav>
		</header>

		<section id="banner" class="minor">
			<div class="inner">
				<h2>Log In</h2>
			</div>
		</section>

		<div id="user-details">
			<c:if test="${not empty error}">
				<div id="error">
					<i class="icon fa-times-circle"></i>
					${error}
				</div>
			</c:if>

			<form:form action="log_in" commandName="logIn" method="post">
				<input id="redirect" name="redirect" type="hidden" value="${redirect}" />

				<div>
					<b>Email Address: </b><input id="emailAddress" name="emailAddress" type="text"/>
				</div>
				<div>
					<b>Password: </b><input id="password" name="password" type="password" />
				</div>
				<c:if test="${not empty recaptchaSiteKey}">
					<div class="padding-top g-recaptcha" data-sitekey="${recaptchaSiteKey}"></div>
				</c:if>
				<div class="padding-top">
					<input class="button special" type="submit" value="Log In" />
				</div>

				<a href="/forgot_password">Forgot password?</a>
			</form:form>
		</div>

		<%@ include file="footer.jspf" %>
	</body>
</html>