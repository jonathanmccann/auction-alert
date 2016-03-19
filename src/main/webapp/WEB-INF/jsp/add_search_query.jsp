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

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<c:choose>
			<c:when test="${isAdd}">
				<title>Add Search Query</title>
			</c:when>
			<c:otherwise>
				<title>Update Search Query</title>
			</c:otherwise>
		</c:choose>
		<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
		<link href="<c:url value="/resources/css/tooltipster.css" />" rel="stylesheet">
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-tooltipster-3.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-validate-1.14.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/main.js" />" type="text/javascript"></script>
	</head>
	<body>
		<div>
			<form:form commandName="searchQuery" id="addSearchQueryForm">
				<form:input path="searchQueryId" type="hidden" value="${searchQuery.searchQueryId}" />
				<form:input path="muted" type="hidden" value="${searchQuery.muted}" />

				<fmt:formatNumber value="${searchQuery.minPrice}" pattern="0.00" var="minPrice" />
				<fmt:formatNumber value="${searchQuery.maxPrice}" pattern="0.00" var="maxPrice" />

				<div>
					<c:choose>
						<c:when test="${isAdd}">
							<h2>Add Search Query</h2>
						</c:when>
						<c:otherwise>
							<h2>Update Search Query</h2>
						</c:otherwise>
					</c:choose>
					<div>
						<b>Keywords:</b> <form:input path="keywords" value="${searchQuery.keywords}" />
						<form:select path="categoryId">
							<form:option value="All Categories"></form:option>
							<form:options items="${searchQueryCategories}" />
						</form:select>
					</div>
					<div>
						<form:checkbox path="searchDescription" label="Search Description" value="${searchQuery.searchDescription}"/>
					</div>
					<hr>
					<div>
						<b>Shipping Options:</b>

						<div>
							<form:checkbox path="freeShippingOnly" label="Free Shipping" value="${searchQuery.freeShippingOnly}"/>
						</div>
					</div>
					<hr>
					<div>
						<b>Listing Type:</b>

						<div>
							<form:checkbox path="auctionListing" label="Auction" value="${searchQuery.auctionListing}"/> <br>
							<form:checkbox path="fixedPriceListing" label="BIN" value="${searchQuery.fixedPriceListing}"/>
						</div>
					</div>
					<hr>
					<div>
						<b>Condition:</b>

						<div>
							<form:checkbox path="newCondition" label="New" value="${searchQuery.newCondition}"/> <br>
							<form:checkbox path="usedCondition" label="Used" value="${searchQuery.usedCondition}"/> <br>
							<form:checkbox path="unspecifiedCondition" label="Unspecified" value="${searchQuery.unspecifiedCondition}"/>
						</div>
					</div>
					<hr>
					<div>
						<b>Price:</b>

						<div>
							Show items priced from <form:input path="minPrice" value="${minPrice}" /> to <form:input path="maxPrice" value="${maxPrice}" />
						</div>
					</div>
					<div>
						<c:choose>
							<c:when test="${disabled}">
								<img src="/resources/images/question_mark_small.png" title="You have reached the maximum number of search queries. Please either delete a search query or increase the limit."><input disabled id="updateSearchQuerySubmit" title="" type="submit" value="Add Search Query" />
							</c:when>
							<c:when test="${isAdd}">
								<input id="updateSearchQuerySubmit" type="submit" value="Add Search Query" formmethod="post" formaction="add_search_query" />
							</c:when>
							<c:otherwise>
								<input id="updateSearchQuerySubmit" type="submit" value="Update Search Query" formmethod="post" formaction="update_search_query" />
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</form:form>
			</br>
			<div align="center">
				<c:choose>
					<c:when test="${isAdd}">
						Add Search Query | <a href="view_search_queries">View Search Queries</a> | <a href="view_search_query_results">View Search Query Results</a> | <a href="my_account">My Account</a>
					</c:when>
					<c:otherwise>
						<a href="add_search_query">Add a Search Query</a> | <a href="view_search_queries">View Search Queries</a> | <a href="view_search_query_results">View Search Query Results</a> | <a href="my_account">My Account</a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</body>
</html>