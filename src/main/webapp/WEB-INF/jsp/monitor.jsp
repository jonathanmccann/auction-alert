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
		<title>Monitor</title>
		<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/tooltipster.css" />" rel="stylesheet">
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-tooltipster-3.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-validate-1.14.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/main.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/subcategory.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/rss.js" />" type="text/javascript"></script>
	</head>
	<body>
		<div>
			<input id="campaignId" type="hidden" value="${campaignId}">

			<form id="monitorForm" name="monitorForm">
				<h2>Monitor</h2>

				<div>
					<b>Keywords:</b> <input id="keywords" name="keywords" maxlength="300" />
					<select id="categoryId">
						<option value="All Categories">All Categories</option>
						<c:forEach items="${searchQueryCategories}" var="searchQueryCategory">
							<option value="${searchQueryCategory.key}">${searchQueryCategory.value}</option>
						</c:forEach>
					</select>
					<select disabled id="subcategoryId" >
						<option value="All Subcategories"></option>
					</select>
				</div>
				<div>
					<input id="searchDescription" type="checkbox">Search Description
				</div>
				<hr>
				<div>
					<b>Shipping Options:</b>

					<div>
						<input id="freeShippingOnly" type="checkbox">Free Shipping
					</div>
				</div>
				<hr>
				<div>
					<b>Listing Type:</b>

					<div>
						<input id="auctionListing" type="checkbox">Auction <br>
						<input id="fixedPriceListing" type="checkbox">Buy It Now
					</div>
				</div>
				<hr>
				<div>
					<b>Condition:</b>

					<div>
						<input id="newCondition" type="checkbox">New <br>
						<input id="usedCondition" type="checkbox">Used <br>
						<input id="unspecifiedCondition" type="checkbox">Unspecified
					</div>
				</div>
				<hr>
				<div>
					<b>Price:</b>

					<div>
						Show items priced from <input id="minPrice" name="minPrice" value="0.00" /> to <input id="maxPrice" name="maxPrice" value="0.00" />
					</div>
				</div>
			</form>
			<div>
				<button id="startMonitoring">Start Monitoring</button>
				<button id="stopMonitoring">Stop Monitoring</button>
				<button id="clearResults">Clear Results</button> <br>
				<input id="desktopNotifications" type="checkbox">Receive Desktop Notifications
			</div>
			<div id="content">
			</div>
			</br>
			<div align="center">
				<a href="add_search_query">Add a Search Query</a> | View Search Queries | <a href="view_search_query_results">View Search Query Results</a> <br> <br>
				<a href="my_account">My Account</a> | <a href="log_out">Log Out</a>
			</div>
		</div>
	</body>
</html>