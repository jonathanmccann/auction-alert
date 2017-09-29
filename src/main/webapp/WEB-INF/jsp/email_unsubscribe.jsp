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

<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<title>Email Unsubscribe</title>

		<script src="/resources/js/jquery-2.1.3.min.js" type="text/javascript"></script>

		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>

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
					<shiro:guest>
						<li><a href="log_in">Log In</a></li>
						<li><a href="create_account" class="button special">Sign Up</a></li>
					</shiro:guest>

					<shiro:user>
						<c:if test="${isActive}">
							<li><a href="add_search_query">Add Search Query</a></li>
							<li><a href="view_search_queries">Search Queries and Results</a></li>
							<li><a href="monitor">Monitor</a></li>
						</c:if>

						<li><a href="my_account">My Account</a></li>
						<li><a href="log_out" class="button special">Log Out</a></li>
					</shiro:user>
				</ul>
			</nav>
		</header>

		<section class="minor narrow">
			<h1>Email Unsubscribe</h1>
		</section>

		<section class="style1 wrapper">
			<div class="container">
				<div class="align-center">
					<h2>${unsubscribeMessage}</h2>
				</div>
			</div>
		</section>

		<%@ include file="footer.jspf" %>
	</body>
</html>