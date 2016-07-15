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

		<link href="<c:url value="/resources/css/tooltipster.css" />" rel="stylesheet">
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-tooltipster-3.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/jquery-validate-1.14.0.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/main.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/skel.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/skel-layers.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/init.js" />" type="text/javascript"></script>
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
			<c:choose>
				<c:when test="${isAdd}">
					<div class="inner">
						<h2>Add Search Query</h2>
					</div>
				</c:when>
				<c:otherwise>
					<div class="inner">
						<h2>Update Search Query</h2>
					</div>
				</c:otherwise>
			</c:choose>
		</section>

		<div class="container padding-top">
			<c:if test="${not empty info}">
				<div id="info">
					<i class="icon fa-info-circle"></i>
					${info}
				</div>
			</c:if>

			<div class="padding-top">
				<form:form commandName="searchQuery" id="addSearchQueryForm">
					<c:choose>
						<c:when test="${disabled}">
							<fieldset disabled>
						</c:when>
						<c:otherwise>
							<fieldset>
						</c:otherwise>
					</c:choose>

						<c:choose>
							<c:when test="${isAdd}">
								<form:input path="active" type="hidden" value="true" />
							</c:when>
							<c:otherwise>
								<form:input path="active" type="hidden" value="${searchQuery.active}" />
							</c:otherwise>
						</c:choose>

						<form:input path="searchQueryId" type="hidden" value="${searchQuery.searchQueryId}" />
						<form:input path="userId" type="hidden" value="${searchQuery.userId}" />

						<input id="initialSubcategoryId" type="hidden" value="${searchQuery.subcategoryId}" />

						<fmt:formatNumber pattern="0.00" value="${searchQuery.minPrice}" var="minPrice" />
						<fmt:formatNumber pattern="0.00" value="${searchQuery.maxPrice}" var="maxPrice" />

						<ul class="alt">
						<li>
							<b>Keywords:</b> <form:input maxlength="300" path="keywords" value="${searchQuery.keywords}" />
							<form:select path="categoryId">
								<form:option value="All Categories"></form:option>
								<form:options items="${searchQueryCategories}" />
							</form:select>
							<form:select disabled="true" id="subcategoryId" path="subcategoryId">
								<form:option value="All Subcategories"></form:option>
							</form:select>
						</li>
						<li>
							<b>Search Options:</b>

							<div>
								<form:checkbox label="Search Description" path="searchDescription" value="${searchQuery.searchDescription}" /> <br>
								<form:checkbox label="Free Shipping" path="freeShippingOnly" value="${searchQuery.freeShippingOnly}" />
							</div>
						</li>
						<li>
							<b>Listing Type:</b>

							<div>
								<form:checkbox label="Auction" path="auctionListing" value="${searchQuery.auctionListing}"/> <br>
								<form:checkbox label="Buy It Now" path="fixedPriceListing" value="${searchQuery.fixedPriceListing}" />
							</div>
						</li>
						<li>
							<b>Condition:</b>

							<div>
								<form:checkbox label="New" path="newCondition" value="${searchQuery.newCondition}"/> <br>
								<form:checkbox label="Used" path="usedCondition" value="${searchQuery.usedCondition}"/> <br>
								<form:checkbox label="Unspecified" path="unspecifiedCondition" value="${searchQuery.unspecifiedCondition}" />
							</div>
						</li>
						<li>
							<b>Price:</b>

							<div>
								Show items priced from <form:input path="minPrice" value="${minPrice}" /> to <form:input path="maxPrice" value="${maxPrice}" />
							</div>
						</li>
						<li class="padding-top">
							<c:choose>
								<c:when test="${disabled}">
									<input class="button special" disabled id="updateSearchQuerySubmit" title="You have reached your maximum number of search queries." type="submit" value="Add Search Query" />
								</c:when>
								<c:when test="${isAdd}">
									<input class="button special" formaction="add_search_query" formmethod="post" id="updateSearchQuerySubmit" type="submit" value="Add Search Query" />
								</c:when>
								<c:otherwise>
									<input class="button special" formaction="update_search_query" formmethod="post" id="updateSearchQuerySubmit" type="submit" value="Update Search Query" />
								</c:otherwise>
							</c:choose>
						</li>
					</fieldset>
				</form:form>
			</div>
		</div>

		<%@ include file="footer.jspf" %>
	</body>
</html>