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
		<title>Search Queries and Results</title>
		<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/skel.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/skel-layers.min.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/init.js" />" type="text/javascript"></script>
		<script src="<c:url value="/resources/js/search-query.js" />" type="text/javascript"></script>
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
				<h2>Search Queries and Results</h2>
			</div>
		</section>

		<section class="wrapper style1">
			<div class="container">
				<div class="row">
					<div class="4u">
						<section class="special box">
							<h2 class="align-left">Active</h2>
							<c:forEach items="${activeSearchQueries}" var="activeSearchQuery">
								<div>
									<a href="javascript:void(0)" onclick="getSearchQueryResults(${activeSearchQuery.searchQueryId}, true);">${activeSearchQuery.keywords}</a>
								</div>
							</c:forEach>
							<h2 class="align-left">Inactive</h2>
							<c:forEach items="${inactiveSearchQueries}" var="inactiveSearchQuery">
								<div>
									<a href="javascript:void(0)" onclick="getSearchQueryResults(${inactiveSearchQuery.searchQueryId}, false);">${inactiveSearchQuery.keywords}</a>
								</div>
							</c:forEach>
						</section>
					</div>
					<div class="8u">
						<form:form class="hidden" commandName="searchQuery" id="searchQueryForm">
							<input id="searchQueryId" name="searchQueryId" type="hidden" />

							<input class="button special" formaction="activate_search_query" formmethod="post" id="activateButton" type="submit" value="Activate" />
							<input class="button special" formaction="deactivate_search_query" formmethod="post" id="deactivateButton" type="submit" value="Deactivate" />
							<input class="button special" formaction="update_search_query" formmethod="get" id="editButton" type="submit" value="Edit" />
							<input class="button special" formaction="delete_search_query" formmethod="post" id="deleteButton" type="submit" value="Delete" />
						</form:form>
						<section class="special box">
							<h2 class="align-left">Results</h2>

							<div id="results">
								<h5>Please select a search query.</h5>
							</div>
						</section>
					</div>
				</div>
			</div>
		</section>
	</body>
</html>