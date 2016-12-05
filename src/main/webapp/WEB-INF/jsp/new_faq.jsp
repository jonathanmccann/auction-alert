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
		<title>New to Auction Alert</title>

		<script src="/resources/js/jquery-2.1.3.min.js" type="text/javascript"></script>

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

		<section class="minor" id="banner">
			<div class="inner">
				<h2>New to Auction Alert</h2>
			</div>
		</section>

		<section class="wrapper style1">
			<div class="container small">
				<div>
					<span class="anchor" id="what"></span>
					<h2>What does this site do?</h2>
					<p class="faq-paragraph">
						This site allows you to receive up to the minute email notifications for newly listed items on eBay.<br><br>
						You can save extremely detailed search queries, which can be edited later, and the site will immediately start searching for items matching that query.
						Whenever a new item is found, an email is sent directly to you allowing you to view and purchase the item.
					</p>

					<span class="anchor" id="who"></span>
					<h2>Who is this site for?</h2>
					<p class="faq-paragraph">
						This site is for people who either need up to the minute searches since they are looking for very rare items or those who feel unsatisfied by eBay's current saved searches implementation.<br><br>
						If you are looking for rare items, you know that they can be sold within minutes of being listed.
						This site will help even the playing field by allowing you to know right when an item has been listed and give you a chance to purchase it.<br><br>
						If you find eBay's saved searches cumbersome or lackluster, then this site might alleviate those issues.
						With an easy to user interface and real time notifications instead of a daily digest, this site takes eBay's saved searches to the next level.
					</p>

					<span class="anchor" id="competitors"></span>
					<h2>How do you compare to competitors?</h2>
					<p class="faq-paragraph">
						As can be expected, there are competitors in this space, so let's take a look at them to see the benefits and drawbacks to each type.<br><br>
						The first type is a site that searches both auctions and Buy It Now listings.
					</p>

					<p class="faq-paragraph-indent">
						In general, these other sites offer searches occurring on a 15 minute interval and only one email sent once every three hours. However, they allow you to have as many search queries as you want.<br><br>
						In contrast, this site performs your search queries every minute and emails are sent immediately after a new result has been found.
						Unfortunately, you are limited in how many queries you can have and how many emails you can receive in a day.
					</p>

					<p class="faq-paragraph">
						The other type of site is one that only searches for Buy It Now listings.
					</p>

					<p class="faq-paragraph-indent">
						With this, you receive real time notifications but you miss out on auctions and there are no email notifications so you constantly need to have the website open.<br><br>
						How does our site stack up against this type of competitor? Real time monitoring is provided for both auctions and Buy It Now listings and it has the added benefit of offering email notifications for when you are away from your computer.
					</p>

					<p class="faq-paragraph">
						You will need to evaluate your needs and choose the best option for your use case. In the end, if you do not need real time notifications, then perhaps one of our competitors would work better for you.
						If you do need up to the minute notifications or more robust real time monitoring, then this site will fit your needs perfectly.
					</p>
				</div>
			</div>
		</section>

		<%@ include file="footer.jspf" %>
	</body>
</html>