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
		<title>Add Search Query</title>
		<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/tooltipster.css" />" rel="stylesheet">
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-tooltipster-3.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-validate-1.14.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/main.js" />" type="text/javascript"></script>
	</head>
	<body>
		<div>
			<form:form action="add_search_query" commandName="searchQuery" id="addSearchQueryForm" method="post">
				<div>
					<h2>Add Search Query</h2>
					<div>
						<b>Keywords:</b> <form:input path="keywords" />
						<form:select path="categoryId">
							<form:option value="All Categories"></form:option>
							<form:options items="${searchQueryCategories}" />
						</form:select>
					</div>
					<hr>
					<div>
						<b>Shipping Options:</b>

						<div>
							<form:checkbox path="freeShippingOnly" label="Free Shipping"/>
						</div>
					</div>
					<hr>
					<div>
						<b>Listing Type:</b>

						<div>
							<form:checkbox path="auctionListing" label="Auction"/> <br>
							<form:checkbox path="fixedPriceListing" label="BIN"/>
						</div>
					</div>
					<hr>
					<div>
						<b>Condition:</b>

						<div>
							<form:checkbox path="newCondition" label="New"/> <br>
							<form:checkbox path="usedCondition" label="Used"/> <br>
							<form:checkbox path="unspecifiedCondition" label="Unspecified"/>
						</div>
					</div>
					<hr>
					<div>
						<b>Price:</b>

						<div>
							Show items priced from <form:input path="minPrice" value="0" /> to <form:input path="maxPrice" value="0" />
						</div>
					</div>
					<div>
						<c:choose>
							<c:when test="${disabled}">
								<img src="/resources/images/question_mark_small.png" title="You have reached the maximum number of search queries. Please either delete a search query or increase the limit."><input disabled id="submit" title="" type="submit" value="Add Search Query" />
							</c:when>
							<c:otherwise>
								<input id="submit" type="submit" value="Add Search Query" />
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</form:form>
			</br>
			<div align="center">
				Add Search Query | <a href="view_search_queries">View Search Queries</a> | <a href="view_search_query_results">View Search Query Results</a>
			</div>
		</div>
	</body>
</html>