<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>View Search Query Results</title>
<link href="<c:url value="/resources/css/main.css" />" rel="stylesheet">
<script src="<c:url value="/resources/js/jquery-2.1.3.min.js" />" type="text/javascript"></script>
<script src="<c:url value="/resources/js/main.js" />" type="text/javascript"></script>
</head>
<body>
	<div align="center">
		<h2>Current Search Results</h2>
			<c:choose>
				<c:when test="${empty searchResultModelMap}">
					<h4>There are currently no search query results</h4>
				</c:when>
				<c:otherwise>
					<c:forEach items="${searchResultModelMap}" var="entry">
						<div class="container">
							<div align="left" class="header">
								<h4><c:out value="Search Query: \"${entry.key}\""/></h4>
							</div>
							<div class="content">
								<table border="0">
									<c:forEach items="${entry.value}" var="item">
										<tr>
											<td><img alt="${item.galleryURL}" src="${item.galleryURL}"></td>
											<td>
												<a href="${item.itemURL}" target="_blank">${item.itemTitle}</a></br>
												<c:if test="${item.auctionPrice gt 0.00}">
													Auction Price: <fmt:formatNumber value="${item.auctionPrice}" type="currency"/></br>
												</c:if>
												<c:if test="${item.fixedPrice gt 0.00}">
													Fixed Price: <fmt:formatNumber value="${item.fixedPrice}" type="currency"/>
												</c:if>
											</td>
										</tr>
									</c:forEach>
								</table>
							</div>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</br>
		<a href="add_search_query">Add a Search Query</a> | <a href="view_search_queries">View Search Queries</a> | View Search Query Results
	</div>
</body>
</html>