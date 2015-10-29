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
	</head>
	<body>
		<div>
			<!-- Validation http://www.w3schools.com/js/tryit.asp?filename=tryjs_validation_js -->
			<form:form action="add_search_query" commandName="searchQuery" method="post">
				<div>
					<h2>Add Search Query</h2>
					<div>
						<b>Keywords:</b> <form:input path="keywords" />
						<form:select path="categoryId">
							<form:option value=""></form:option>
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
							Show items prices from <form:input path="minPrice" type="number" /> to <form:input path="maxPrice" type="number" />
						</div>
					</div>
					<div>
						<c:choose>
							<c:when test="${disabled}">
								<img src="/resources/images/question_mark_small.png" title="You have reached the maximum number of search queries. Please either delete a search query or increase the limit."><input disabled title="" type="submit" value="Add Search Query" />
							</c:when>
							<c:otherwise>
								<input type="submit" value="Add Search Query" />
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