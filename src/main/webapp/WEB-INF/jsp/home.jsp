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

		<script src="/resources/js/jquery-2.1.3.min.js" type="text/javascript"></script>

		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>

		<script src="/resources/js/popup.js" type="text/javascript"></script>
		<script src="/resources/js/scroll.js" type="text/javascript"></script>

		<noscript>
			<link href="/resources/css/skel.css" rel="stylesheet" />
			<link href="/resources/css/style.css" rel="stylesheet" />
			<link href="/resources/css/style-xlarge.css" rel="stylesheet" />
		</noscript>
	</head>

	<body>
		<div id="popup">
			<div class="popup-content">
				<div class="popup-header">
					<span id="close">X</span>
					<h2>Log In</h2>
				</div>

				<div class="popup-body">
					<form:form action="log_in" commandName="logIn" method="post">
						<div>
							<b>Email Address: </b><input id="emailAddress" name="emailAddress" type="email" />
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
				<section class="major" id="banner">
					<div class="inner">
						<h2>Auction Alert</h2>
						<p>Receive up to the minute alerts for newly listed items on eBay using highly configurable search queries.</p>
						<ul class="actions">
							<li><a href="create_account" class="button big special">Sign Up</a></li>
							<li><a href="#about" id="learn-more" class="button big alt">Learn More</a></li>
						</ul>
					</div>
				</section>

				<section class="style1 wrapper" id="about">
					<div class="container">
						<div class="row">
							<div class="3u">
								<section class="box special">
									<i class="fa-save icon major"></i>
									<h3>Save</h3>
									<p>Search queries are saved to be searched and monitored.</p>
								</section>
							</div>

							<div class="3u">
								<section class="box special">
									<i class="fa-search icon major"></i>
									<h3>Search</h3>
									<p>Each search query is searched every minute.</p>
								</section>
							</div>

							<div class="3u">
								<section class="box special">
									<i class="fa-send icon major"></i>
									<h3>Send</h3>
									<p>New results are sent directly to your email.</p>
								</section>
							</div>

							<div class="3u">
								<section class="box special">
									<i class="fa-refresh icon major"></i>
									<h3>Monitor</h3>
									<p>Keep tabs on a search query with real time updates.</p>
								</section>
							</div>
						</div>
					</div>
				</section>

				<section class="style2 wrapper" id="how">
					<header class="major">
						<h2>How does this all work?</h2>
					</header>

					<div class="container">
						<div class="row flex">
							<div class="6u second">
								<h3 align="left">Searching</h3>
								<p align="left">
									You will have the ability to craft search queries that will then be run against eBay and any new results are sent directly to your email.<br><br>
									These queries are run every minute so you are up to date with every new listing that is posted on eBay.<br><br>
									Many options are available to you so you can narrow down the items by category, type of listing, price, and even location.
								</p>
							</div>
							<div class="6u">
								<div class="image fit">
									<img alt="Adding Search Query" src="resources/images/adding_search_query.png">
								</div>
							</div>
						</div>

						<div class="row flex">
							<div class="6u">
								<h3 align="left">Monitoring</h3>
								<p align="left">
									Utilizing the monitoring tool, you can have real time updates for newly listed items on eBay, complete with desktop notifications.<br><br>
									With this, gone are the days of manually refreshing the eBay site, looking for good deals. All the work is done for you. Just tell us what you're looking for.
								</p>
							</div>
							<div class="6u">
								<div class="image fit">
									<img alt="Monitoring Results" src="resources/images/monitoring.png">
								</div>
							</div>
						</div>

						<div class="row flex">
							<div class="6u second">
								<h3 align="left">Email Notifications</h3>
								<p align="left">
									Every new result is sent immediately to your email address. From there, you are free to access the item's listing and bid or purchase.<br><br>
									In addition, up to 20 results are saved on the site so that you can look at the previous results without having to sift through your emails.
								</p>
							</div>
							<div class="6u">
								<div class="image fit">
									<img alt="Email Notification" src="resources/images/email_notification.png">
								</div>
							</div>
						</div>
					</div>
				</section>

				<section class="style1 wrapper" id="pricing">
					<div class="container">
						<section class="box special">
							<h2>Simply priced.</h2>
							<h3>$1 per month gives you access to</h3>
							<p>
								<b>10 search queries</b> <br>
								<b>30 emails a day</b> <br>
								<b>Unlimited monitoring</b>
							</p>

							<a class="button special" href="create_account">Sign Up Now</a>
						</section>
					</div>
				</section>
			</div>
		</shiro:guest>

		<shiro:user>
			<div>
				<section class="minor" id="banner">
					<div class="inner">
						<h2>Welcome</h2>
					</div>
				</section>

				<c:choose>
					<c:when test="${isActive}">
						<section class="style1 wrapper" id="user">
							<div class="container">
								<div class="row">
									<div class="4u">
										<a href="add_search_query">
											<section class="box special">
												<i class="fa-search icon major"></i>
												<h3>Add a New Query</h3>
											</section>
										</a>
									</div>

									<div class="4u">
										<a href="view_search_queries">
											<section class="box special">
												<i class="fa-archive icon major"></i>
												<h3>View Queries and Results</h3>
											</section>
										</a>
									</div>

									<div class="4u">
										<a href="my_account">
											<section class="box special">
												<i class="fa-gear icon major"></i>
												<h3>Manage My Account</h3>
											</section>
										</a>
									</div>
								</div>
							</div>
						</section>

						<section class="style2 wrapper" id="action">
							<div class="container">
								<div class="row">
									<div class="6u">
										<a href="view_search_queries">
											<section class="box special">
												<i class="fa-send icon major"></i>
												<h3>${emailsSent}</h3>
											</section>
										</a>
									</div>

									<div class="6u">
										<a href="my_account">
											<section class="box special">
												<i class="fa-calendar icon major"></i>
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
								<section class="box special">
									<i class="fa-calendar icon major"></i>
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