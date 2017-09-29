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

		<script src="/resources/js/jquery-2.1.3.min.js" type="text/javascript"></script>

		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>

		<script src="/resources/js/search-query.js" type="text/javascript"></script>

		<noscript>
			<link href="/resources/css/skel.css" rel="stylesheet" />
			<link href="/resources/css/style.css" rel="stylesheet" />
			<link href="/resources/css/style-xlarge.css" rel="stylesheet" />
		</noscript>

		<c:if test="${not empty currentSearchQueryId}">
			<script type="text/javascript">
				$(window).load(function() {
					getSearchQueryResults(${currentSearchQueryId}, ${isCurrentSearchQueryActive});
				});
			</script>
		</c:if>
	</head>

	<body>
		<%@ include file="header.jspf" %>

		<section class="minor">
			<h1>Search Queries and Results</h1>
		</section>

		<section class="style1 wrapper">
			<div class="container">
				<div class="row">
					<div class="4u">
						<section class="box special">
							<h2 class="align-left">Active</h2>
							<c:forEach items="${activeSearchQueries}" var="activeSearchQuery" varStatus="loop">
								<div id="${activeSearchQuery.searchQueryId}">
									<a href="javascript:void(0)" onclick="getSearchQueryResults(${activeSearchQuery.searchQueryId}, true);">${activeSearchQuery.keywords}</a>
								</div>

								<c:if test="${!loop.last}">
									-
								</c:if>
							</c:forEach>

							<h2 class="align-left">Inactive</h2>
							<c:forEach items="${inactiveSearchQueries}" var="inactiveSearchQuery" varStatus="loop">
								<div id="${inactiveSearchQuery.searchQueryId}">
									<a href="javascript:void(0)" onclick="getSearchQueryResults(${inactiveSearchQuery.searchQueryId}, false);">${inactiveSearchQuery.keywords}</a>

									<c:if test="${!loop.last}">
										<br>-
									</c:if>
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
							<input class="button special" formaction="monitor" formmethod="get" id="monitorButton" type="submit" value="Monitor" />
							<input class="button delete" formaction="delete_search_query" formmethod="post" id="deleteButton" type="submit" value="Delete" />
						</form:form>

						<section class="box special">
							<h2 class="align-left">Results</h2>

							<div id="results">
								<h5>Please select a search query.</h5>
							</div>
						</section>
					</div>
				</div>
			</div>
		</section>

		<%@ include file="footer.jspf" %>
	</body>
</html>