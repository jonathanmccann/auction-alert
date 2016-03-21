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
		<title>View Search Queries</title>
		<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
	</head>
	<body>
		<div>
			<form:form commandName="searchQueryCheckboxes" method="post">
				<div>
					<h2>Current Search Queries</h2>
					<div>
						<b>Active Search Queries</b>
						<c:choose>
							<c:when test="${empty activeSearchQueries}">
								<div>
									There are currently no active search queries.
								</div>
							</c:when>
							<c:otherwise>
								<c:forEach items="${activeSearchQueries}" var="activeSearchQuery">
									<div>
										<input id="activeCheckboxes" name="activeSearchQueryIds" type="checkbox" value="${activeSearchQuery.searchQueryId}" /><label><a href="update_search_query?searchQueryId=${activeSearchQuery.searchQueryId}">${activeSearchQuery.keywords}</a></label>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</div>
					<div>
						<b>Inactive Search Queries</b>
						<c:choose>
							<c:when test="${empty inactiveSearchQueries}">
								<div>
									There are currently no inactive search queries.
								</div>
							</c:when>
							<c:otherwise>
								<c:forEach items="${inactiveSearchQueries}" var="inactiveSearchQuery">
									<div>
										<input id="inactiveCheckboxes" name="inactiveSearchQueryIds" type="checkbox" value="${inactiveSearchQuery.searchQueryId}" /><label><a href="update_search_query?searchQueryId=${inactiveSearchQuery.searchQueryId}">${inactiveSearchQuery.keywords}</a></label>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</div>
					<c:if test="${(not empty activeSearchQueries) || (not empty inactiveSearchQueries)}">
						<div>
							<div>
								<input formaction="delete_search_query" type="submit" value="Delete Search Query" />
								<input formaction="activate_search_query" type="submit" value="Activate Search Query" />
								<input formaction="deactivate_search_query" type="submit" value="Deactivate Search Query" />
							</div>
						</div>
					</c:if>
				</div>
			</form:form>
			</br>
			<div align="center">
				<a href="add_search_query">Add a Search Query</a> | View Search Queries | <a href="view_search_query_results">View Search Query Results</a> | <a href="my_account">My Account</a>
			</div>
		</div>
	</body>
</html>