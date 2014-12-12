<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>Add Search Query</title>
</head>
<body>
	<div align="center">
		There was an error adding the search query ${searchQueryModel.getSearchQuery()}
		</br>
		<a href="/eBay-webapp/add_search_query">Add a Search Query</a>
		</br>
		<a href="/eBay-webapp/view_search_queries">View Search Queries</a>
		</br>
		<a href="/eBay-webapp/view_search_query_results">View Search Query Results</a>
	</div>
</body>
</html>