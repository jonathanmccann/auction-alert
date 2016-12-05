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
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<title>FAQ</title>

		<script src="/resources/js/jquery-2.1.3.min.js" type="text/javascript"></script>

		<script src="/resources/js/skel.min.js" type="text/javascript"></script>
		<script src="/resources/js/skel-layers.min.js" type="text/javascript"></script>
		<script src="/resources/js/init.js" type="text/javascript"></script>

		<noscript>
			<link rel="stylesheet" href="/resources/css/skel.css" />
			<link rel="stylesheet" href="/resources/css/style.css" />
			<link rel="stylesheet" href="/resources/css/style-xlarge.css" />
		</noscript>
	</head>
	<body>
		<header class="skel-layers-fixed" id="header">
			<h1><a href="/home">Auction Alert</a></h1>
			<nav id="nav">
				<ul>
					<shiro:guest>
						<li><a href="log_in" id="loginLink">Log In</a></li>
						<li><a href="create_account" class="button special">Sign Up</a></li>
					</shiro:guest>
					<shiro:user>
						<c:if test="${isActive}">
							<li><a href="add_search_query">Add Search Query</a></li>
							<li><a href="view_search_queries">Search Queries and Results</a></li>
							<li><a href="monitor">Monitor</a></li>
						</c:if>

						<li><a href="my_account">My Account</a></li>
						<li><a href="log_out" class="button special">Log Out</a></li>
					</shiro:user>
				</ul>
			</nav>
		</header>

		<section class="minor" id="banner">
			<div class="inner">
				<h2>Frequently Asked Questions</h2>
			</div>
		</section>

		<section class="wrapper style1">
			<div class="container">
				<div class="row">
					<div class="4u">
						<section class="special box">
							<h3><a href="/new_faq">New to Auction Alert?</a></h3>
							<div class="align-left">
								<li><a href="/new_faq#what">What does this site do?</a></li>
								<li><a href="/new_faq#who">Who is this application for?</a></li>
								<li><a href="/new_faq#competitors">How do you compare to competitors?</a></li>
							</div>
						</section>
					</div>
					<div class="4u">
						<section class="special box">
							<h3><a href="/general_faq">General</a></h3>
							<div class="align-left">
								<li><a href="/general_faq#no_emails">I am not receiving any emails!</a></li>
								<li><a href="/general_faq#emails">I am receiving too many emails!</a></li>
								<li><a href="/general_faq#item">The item was sold before I could buy it!</a></li>
								<li><a href="/general_faq#limit">Why am I limited to 10 queries and 30 emails?</a></li>
								<li><a href="/general_faq#charge">When is my account charged?</a></li>
								<li><a href="/general_faq#unsubscribe">What happens when I unsubscribe?</a></li>
							</div>
						</section>
					</div>
					<div class="4u">
						<section class="special box">
							<h3><a href="/account_faq">My Account</a></h3>
							<div class="align-left">
								<li><a href="/account_faq#credit_card">How do I change my credit card details?</a></li>
								<li><a href="/account_faq#cancel">How do I cancel my subscription?</a></li>
								<li><a href="/account_faq#account">How do I change my account details?</a></li>
								<li><a href="/account_faq#notifications">How do I turn off email notifications?</a></li>
								<li><a href="/account_faq#safety">How safe is my credit card and password?</a></li>
								<li><a href="/account_faq#domain">What is the preferred domain?</a></li>
								<li><a href="/account_faq#currency">What currency are the prices in?</a></li>
							</div>
						</section>
					</div>
				</div>
				<div class="row">
					<div class="4u">
						<section class="special box">
							<h3><a href="/query_faq">Search Query</a></h3>
							<div class="align-left">
								<li><a href="/query_faq#add">How do I add a search query?</a></li>
								<li><a href="/query_faq#edit">How do I edit a search query?</a></li>
								<li><a href="/query_faq#boolean">Can I use boolean logic?</a></li>
								<li><a href="/query_faq#after">What happens after I save a search query?</a></li>
								<li><a href="/query_faq#often">How often are queries searched?</a></li>
								<li><a href="/query_faq#active">What's the difference between active and inactive queries?</a></li>
							</div>
						</section>
					</div>
					<div class="4u">
						<section class="special box">
							<h3><a href="/result_faq">Search Result</a></h3>
							<div class="align-left">
								<li><a href="/result_faq#view">How do I view the results?</a></li>
								<li><a href="/result_faq#number">How many results are stored?</a></li>
								<li><a href="/result_faq#order">How are the results ordered?</a></li>
							</div>
						</section>
					</div>
					<div class="4u">
						<section class="special box">
							<h3><a href="/monitor_faq">Monitoring</a></h3>
							<div class="align-left">
								<li><a href="/monitor_faq#how">How do I monitor a query?</a></li>
								<li><a href="/monitor_faq#save">Can I save a monitored query?</a></li>
								<li><a href="/monitor_faq#often">How often is the monitored query searched?</a></li>
								<li><a href="/monitor_faq#notifications">Why aren't desktop notifications working?</a></li>
							</div>
						</section>
					</div>
				</div>
			</div>
			<div align="center" class="container">
				<h2>Have a question not answered here?</h2>
				Please use our <a href="/contact">contact</a> page to get in touch with us with any other questions you may have.
			</div>
		</section>

		<%@ include file="footer.jspf" %>
	</body>
</html>