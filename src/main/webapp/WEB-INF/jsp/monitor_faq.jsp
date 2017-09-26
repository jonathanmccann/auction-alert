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
		<title>Monitoring</title>

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
			<div class="row">
				<div class="4u">
					<h1>Monitoring</h1>
				</div>
			</div>
		</section>

		<section class="wrapper style1">
			<div class="container small">
				<div>
					<span class="anchor" id="how"></span>
					<h2>How do I monitor a query?</h2>
					<p class="faq-paragraph">
						The monitor page is extremely similar to the Add Search Query page. For more information about the various options, please see the <a href="/faq/query_faq" target="_blank">search query FAQ</a>.<br><br>
						After crafting your search query, you can begin the monitoring by clicking <b>Start Monitoring</b> button.
						Once this button is clicked, your search query will be performed every five seconds and results will be displayed in the results section underneath the search query.<br><br>
						When the query is being monitored, the button will change to read <b>Stop Monitoring</b>. If you wish to stop searching and receiving new results, click this button.
						Additionally, you are also able to clear the found results by click on the <b>Clear Results</b> button. This will remove all of the items currently listed in the Results area.<br><br>
						You are able to monitor a query even if the page is not active on the screen. In order to see results wherever you are, you can check the <b>Receive Desktop Notifications</b> option.
						This will then create a notification containing the details about the results found and allow you to access the item on eBay directly from the notification.
						Please note that notifications do not work on mobile devices currently.
					</p>

					<span class="anchor" id="save"></span>
					<h2>Can I save a monitored query?</h2>
					<p class="faq-paragraph">
						At this time, you are unable to save a monitored query.
						However, you are able to monitor a saved search query from the <a href="/view_search_queries" target="_blank">view search queries</a> page by choosing a query and then clicking the <b>Monitor</b> button.
					</p>

					<span class="anchor" id="often"></span>
					<h2>How often is the monitored query searched?</h2>
					<p class="faq-paragraph">
						Monitored queries are searched every five seconds.
					</p>

					<span class="anchor" id="notifications"></span>
					<h2>Why aren't desktop notifications working?</h2>
					<p class="faq-paragraph">
						If notifications are not working correctly, please confirm that you have allowed the page to provide notifications to you and that you are not on a mobile device.
					</p>
				</div>
			</div>
		</section>

		<%@ include file="footer.jspf" %>
	</body>
</html>