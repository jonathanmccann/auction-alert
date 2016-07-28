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
		<title>FAQ</title>
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
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

		<section id="banner" class="minor">
			<div class="inner">
				<h2>Frequently Asked Questions</h2>
			</div>
		</section>

		<section id="about" class="wrapper style1">
			<div class="container">
				<div>
					<h2>What do I receive when I sign up?</h2>
					<h4>Email Notifications</h4>
					<p class="faq-paragraph">
						As a subscriber, you have the ability to save 10 search queries that will be searched every minute.
						Whenever a new result is found, an email notification is sent immediately to you, up to 30 emails a day.
					</p>
					<h4>Real Time Monitoring</h4>
					<p class="faq-paragraph">
						You will also have the added benefit of having real time monitoring of a single search query.
						The query will be searched every five seconds and the results will be populated on the web page. Optionally, desktop notifications can be delivered for each new result.
						Please note that desktop notifications are subject to the operating system and browser and may not be supported.
					</p>
					<h2>What regions does this application service?</h2>
					<p class="faq-paragraph">
						While anyone can register for an account, this application currently only searches the US eBay site.
						However, this feature could be implemented in the future so please send your feedback to us.
					</p>
					<h2>How secure is my card information?</h2>
					<p class="faq-paragraph">
						Your card information is sent securely to Stripe and those details are not stored anywhere in this application.
						For more information about Stripe's security and how they are keeping your information secure, please visit <a href="https://stripe.com/docs/security/stripe" target="_blank">their site</a>.
					</p>
					<h2>Is Javascript required for using this website?</h2>
					<p class="faq-paragraph">
						Yes. Javascript is essential to the use of this website and its services.
					</p>
					<h2>Who do I contact if I have further questions?</h2>
					<p class="faq-paragraph">
						Please feel free to use our <a href="/contact">contact</a> page to get in touch with us.
					</p>
					<h2>CSS Design</h2>
					<p class="faq-paragraph">
						The CSS on this site was significantly sourced from <a href="https://templated.co/">TEMPLATED</a>.
					</p>
				</div>
			</div>
		</section>

		<%@ include file="footer.jspf" %>
	</body>
</html>