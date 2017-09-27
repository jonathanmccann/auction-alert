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
		<title>General</title>

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

		<section class="minor">
			<h1>General</h1>

			<div class="sub-heading-indent">
				<h5><a href="/faq">FAQ Home</a></h5>
			</div>
		</section>

		<section class="wrapper style1">
			<div class="container small">
				<div>
					<span class="anchor" id="no_emails"></span>
					<h2>I am not receiving any emails!</h2>
					<p class="faq-paragraph">
						Please first confirm that the email address listed in your account is correct and that the emails are not in your spam folder.
						Additionally, confirm that the <b>Send Email Notifications</b> option is checked in your account.<br><br>
						If you are still not receiving any emails, please perform the same query on <a href="http://www.ebay.com" target="_blank">eBay</a>. If no results are returned, then the application is working correctly.
						If there are results populating on eBay, then please contact us with the search query in question so that we can investigate why you are not receiving notifications.
					</p>

					<span class="anchor" id="emails"></span>
					<h2>I am receiving too many emails!</h2>
					<p class="faq-paragraph">
						At most, you will receive a total of 30 emails per day. If you have a very broad query, then you may receive 30 emails in 30 minutes since the query continuously finds new items to alert you of.
						To mitigate that issue, please be sure to make a more narrow query.<br><br>
						If you no longer wish to receive email notifications, please uncheck the <b>Send Email Notifications</b> option in your account.
					</p>

					<span class="anchor" id="item"></span>
					<h2>The item was sold before I could buy it!</h2>
					<p class="faq-paragraph">
						Unfortunately, there will be other people that are also looking for the same item as you.
						While this application will help give you an edge on other buyers, others may still purchase the item before you are able to.
					</p>

					<span class="anchor" id="limit"></span>
					<h2>Why am I limited to 10 queries and 30 emails?</h2>
					<p class="faq-paragraph">
						Unfortunately, eBay has placed a limit on how many API calls can be made in a single day.
						Since every search query is performed every minute, it is easy to utilize the allotment quickly, especially with a number of users.
						10 queries is enough to set up a various range of searches, while still keeping the application within the confines of eBay's limits.<br><br>
						In order to keep your costs low as a subscriber, each user is limited to 30 emails per day.
						Since the search queries are performed every minute, a very broad query like <b>DVD</b> would retrieve new results every time it is searched.
						This would result in a total of 1,440 emails delivered to you every day. This is neither cost nor user friendly, so the limit is capped at 30 emails per day per user.
					</p>

					<span class="anchor" id="charge"></span>
					<h2>When is my account charged?</h2>
					<p class="faq-paragraph">
						When first signing up for an account, your credit card will be charged immediately. From that point forward, you will be charged once per month.
						For instance, if you signed up on the 5th of February, you will be charged again on the 5th of March, then on the 5th of April, continuing until you cancel your subscription.
					</p>

					<span class="anchor" id="unsubscribe"></span>
					<h2>What happens when I unsubscribe?</h2>
					<p class="faq-paragraph">
						When you unsubscribe, your credit card will not be charged again. You will be able to use the application for the remainder of the month, until the next charge would have occurred.
						At that time, your account will become inactive, and you will no longer be able to add search queries, receive email notifications, or use the monitoring tool.
						In order to regain access to these features, you will need to resubscribe, providing your credit card details again.<br><br>
						For instance, let's say you subscribed on the 5th of February. You will be charged immediately and have full access to the site.
						Then, on the 10th of February, you decided to unsubscribe. Your account will remain active until the 5th of March.
						However, once that day comes around, you will not be charged since you have unsubscribed. Your account will now become inactive and you will no longer be able to use the site.
					</p>

					<span class="anchor" id="country"></span>
					<h2>What countries do you support?</h2>
					<p class="faq-paragraph">
						Currently, we support searching the following eBay sites:
					</p>

					<p class="faq-paragraph-indent">
						http://www.ebay.com (United States)<br>
						http://www.ebay.ca (Canada)<br>
						http://www.ebay.co.uk (United Kingdom)<br><br>

						http://www.ebay.at (Austria)<br>
						http://www.ebay.com.au (Australia)<br>
						http://www.befr.ebay.be (Belgium)<br>
						http://www.ebay.fr (France)<br>
						http://www.ebay.de (Germany)<br>
						http://www.ebay.ie (Ireland)<br>
						http://www.ebay.it (Italy)<br>
						http://www.ebay.nl (Netherlands)<br>
						http://www.ebay.es (Spain)<br>
						http://www.ebay.ch (Switzerland)
					</p>
				</div>
			</div>
		</section>

		<%@ include file="footer.jspf" %>
	</body>
</html>