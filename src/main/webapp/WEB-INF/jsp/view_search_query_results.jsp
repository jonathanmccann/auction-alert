<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>View Search Queries</title>
</head>
<body>
	<div align="center">
		<table border="0">
			<tr>
				<td colspan="2" align="center"><h2>Current Search Results</h2></td>
			</tr>
			<c:choose>
				<c:when test="${empty searchQueryModels}">
					<td>There are currently no search query results</td>
				</c:when>
				<c:otherwise>
					<c:forEach items="${searchResultModelMap}" var="entry">
						<tr>
							<td><c:out value="Search Query - ${entry.key}"/></td>
							<c:forEach items="${entry.value}" var="item">
								<tr>
									<td><c:out value="${item.itemTitle}"/></td>
								</tr>
							</c:forEach>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</table>
		</br>
		<a href="/eBay-webapp/add_search_query">Add a Search Query</a>
		</br>
		<a href="/eBay-webapp/view_search_queries">View Search Queries</a>
	</div>
</body>
</html>