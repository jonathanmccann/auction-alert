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
		<link href="<c:url value="/resources/css/tooltipster.css" />" rel="stylesheet">
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-tooltipster-3.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-validate-1.14.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/validate-search-query.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/skel.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/skel-layers.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/init.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/rss.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/subcategory.js" />" type="text/javascript"></script>
		<noscript>
			<link rel="stylesheet" href="/resources/css/skel.css" />
			<link rel="stylesheet" href="/resources/css/style.css" />
			<link rel="stylesheet" href="/resources/css/style-xlarge.css" />
		</noscript>
	</head>
	<body>
		<%@ include file="header.jspf" %>

		<section id="banner" class="minor">
			<div class="inner">
				<h2>Monitor</h2>
			</div>
		</section>

		<div class="container padding-top">
			<div id="search" class="icon fa-angle-down">
				<span class="monitor-header">Search Query</span>
			</div>

			<div id="searchQuery" class="container padding-top">
				<input id="campaignId" type="hidden" value="${campaignId}">

				<form id="searchQueryForm">
					<ul class="alt">
						<li>
							<b>Keywords:</b> <input id="keywords" name="keywords" maxlength="300" type="text" />
							<select id="categoryId">
								<option value="All Categories">All Categories</option>
								<c:forEach items="${searchQueryCategories}" var="searchQueryCategory">
									<option value="${searchQueryCategory.key}">${searchQueryCategory.value}</option>
								</c:forEach>
							</select>
							<select disabled id="subcategoryId" >
								<option value="All Subcategories"></option>
							</select>
						</li>
						<li>
							<b>Search Options:</b>

							<div>
								<input id="searchDescription" type="checkbox" />
								<label for="searchDescription">Search Description</label> <br>
								<input id="freeShippingOnly" type="checkbox" />
								<label for="freeShippingOnly">Free Shipping</label>
							</div>
						</li>
						<li>
							<b>Listing Type:</b>

							<div>
								<input id="auctionListing" type="checkbox" />
								<label for="auctionListing">Auction</label> <br>
								<input id="fixedPriceListing" type="checkbox" />
								<label for="fixedPriceListing">Buy It Now</label>
							</div>
						</li>
						<li>
							<b>Condition:</b>

							<div>
								<input id="newCondition" type="checkbox" />
								<label for="newCondition">New</label> <br>
								<input id="usedCondition" type="checkbox" />
								<label for="usedCondition">Used</label> <br>
								<input id="unspecifiedCondition" type="checkbox" />
								<label for="unspecifiedCondition">Unspecified</label>
							</div>
						</li>
						<li>
							<b>Price:</b>

							<div>
								Show items priced from <input id="minPrice" name="minPrice" type="text" value="0.00" /> to <input id="maxPrice" name="maxPrice" type="text" value="0.00" />
							</div>
						</li>
					</ul>
				</form>
			</div>

			<div>
				<button class="button special" id="startMonitoring">Start Monitoring</button>
				<button class="button special" id="stopMonitoring">Stop Monitoring</button>
				<button class="button special" id="clearResults">Clear Results</button>

				<div class="padding-top">
					<input id="desktopNotifications" type="checkbox" />
					<label for="desktopNotifications">Receive Desktop Notifications</label>
				</div>
			</div>

			<section class="special box">
				<h2 class="align-left">Results</h2>
				<div id="content">
					<h5>Please start monitoring in order to display results.</h5>
				</div>
			</section>
		</div>

		<%@ include file="footer.jspf" %>
	</body>
</html>