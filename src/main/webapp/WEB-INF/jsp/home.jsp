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
		<title>Home</title>
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>
		<script src="/resources/js/login.js" type="text/javascript"></script>
		<script src="/resources/js/scroll.js" type="text/javascript"></script>
		<noscript>
			<link rel="stylesheet" href="/resources/css/skel.css" />
			<link rel="stylesheet" href="/resources/css/style.css" />
			<link rel="stylesheet" href="/resources/css/style-xlarge.css" />
		</noscript>
	</head>
	<body>
		<div id="login">
			<div class="login-content">
				<div class="login-header">
					<span id="close">X</span>
					<h2>Log In</h2>
				</div>
				<div class="login-body">
					<form:form action="log_in" commandName="logIn" method="post">
						<div>
							<b>Email Address: </b><input id="emailAddress" name="emailAddress" type="email"/>
						</div>
						<div>
							<b>Password: </b><input id="password" name="password" type="password" />
						</div>
						<div class="padding-top">
							<input class="button special" type="submit" value="Log In" />
						</div>

						<a href="/forgot_password">Forgot password?</a>
					</form:form>
				</div>
			</div>
		</div>
		<header id="header" class="skel-layers-fixed">
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
							<li><a href="monitor">Monitor</a></li>
						</c:if>

						<li><a href="my_account">My Account</a></li>
						<li><a href="log_out" class="button special">Log Out</a></li>
					</shiro:user>
				</ul>
			</nav>
		</header>

		<shiro:guest>
			<div>
				<section id="banner" class="major">
					<div class="inner">
						<h2>This is Auction Alert</h2>
						<p>The simple way to stay informed</p>
						<ul class="actions">
							<li><a href="create_account" class="button big special">Sign Up</a></li>
							<li><a href="#about" id="learn-more" class="button big alt">Learn More</a></li>
						</ul>
					</div>
				</section>

				<section id="about" class="wrapper style1">
					<div class="container">
						<div class="row">
							<div class="4u">
								<section class="special box">
									<i class="icon fa-save major"></i>
									<h3>Save</h3>
									<p>Search queries are saved to be edited, searched, and monitored.</p>
								</section>
							</div>
							<div class="4u">
								<section class="special box">
									<i class="icon fa-search major"></i>
									<h3>Search</h3>
									<p>Each search query is searched every minute.</p>
								</section>
							</div>
							<div class="4u">
								<section class="special box">
									<i class="icon fa-send major"></i>
									<h3>Send</h3>
									<p>New results are sent directly to your email.</p>
								</section>
							</div>
						</div>
					</div>
				</section>

				<section id="monitor" class="wrapper style2">
					<div class="container">
						<section class="special box">
							<i class="icon fa-refresh major"></i>
							<h3>Monitoring</h3>
							<p>Keep tabs on a search query with five second searches and desktop notifications.</p>
						</section>
					</div>
				</section>

				<section id="pricing" class="wrapper style1">
					<div class="container">
						<section class="special box">
							<i class="icon fa-dollar major"></i>
							<h3>Pricing</h3>
							<p>
								<b>$10 per month</b> <br> <br>
								10 search queries <br>
								30 emails a day <br>
								Unlimited monitoring
							</p>
							<a href="create_account" class="button special">Sign Up</a>
						</section>
					</div>
				</section>
			</div>
		</shiro:guest>

		<shiro:user>
			<div>
				<section id="banner" class="minor">
					<div class="inner">
						<h2>Welcome</h2>
					</div>
				</section>

				<c:choose>
					<c:when test="${isActive}">
						<section id="user" class="wrapper style1">
							<div class="container">
								<div class="row">
									<div class="4u">
										<a href="add_search_query">
											<section class="special box">
												<i class="icon fa-search major"></i>
												<h3>Add New Query</h3>
											</section>
										</a>
									</div>
									<div class="4u">
										<a href="view_search_queries">
											<section class="special box">
												<i class="icon fa-archive major"></i>
												<h3>Queries and Results</h3>
											</section>
										</a>
									</div>
									<div class="4u">
										<a href="my_account">
											<section class="special box">
												<i class="icon fa-gear major"></i>
												<h3>My Account</h3>
											</section>
										</a>
									</div>
								</div>
							</div>
						</section>

						<section id="action" class="wrapper style2">
							<div class="container">
								<div class="row">
									<div class="6u">
										<a href="view_search_queries">
											<section class="special box">
												<i class="icon fa-send major"></i>
												<h3>${emailsSent}</h3>
											</section>
										</a>
									</div>
									<div class="6u">
										<a href="my_account">
											<section class="special box">
												<i class="icon fa-calendar major"></i>
												<h3>${nextChargeDate}</h3>
											</section>
										</a>
									</div>
								</div>
							</div>
						</section>
					</c:when>
					<c:otherwise>
						<div class="container padding-top">
							<a href="my_account">
								<section class="special box">
									<i class="icon fa-calendar major"></i>
									<h3>${nextChargeDate}</h3>
									<h5>Please resubscribe to access these features</h5>
								</section>
							</a>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</shiro:user>

		<%@ include file="footer.jspf" %>
	</body>
</html>