$(window).load(function() {
	var itemIds = [];

	var campaignId = $("#campaignId").val();

	var search = $("#search");

	var searchQuery = $("#searchQuery");

	var contentDiv = document.getElementById('content');

	var imageRegex = new RegExp("<img src='([^']+)");

	var intervalId;

	var itemUrl = "http://rover.ebay.com/rover/1/711-53200-19255-0/1?icep_ff3=2&toolid=10001&campid=" + campaignId + "&ipn=psmain&icep_vectorid=229466&kwid=902099&mtid=824&kw=lg&icep_item=";

	var rssUrl = "http://rest.ebay.com/epn/v1/find/item.rss?programid=1&toolid=10039&lgeo=1&feedType=rss&sortOrder=StartTimeNewest&hideDuplicateItems=true&campaignid=" + campaignId + "&keyword=";

	function fetchRss(url) {
		$.ajax({
			url: "https://query.yahooapis.com/v1/public/yql?q=select%20title%2C%20description%2C%20guid%2C%20e%3ACurrentPrice%2C%20e%3AListingType%2C%20e%3ABuyItNowPrice%20from%20rss(0%2C10)%20where%20url%20%3D%20%22" + url + "%22&format=json",
			success: function (data) {
				if (data.query && data.query.results && data.query.results.item) {
					$.each(data.query.results.item, function (i, e) {
						var itemId = e.guid;

						if (itemIds.indexOf(itemId) < 0) {
							var imageUrl = imageRegex.exec(e.description)[1];

							var html = '<div align="left" id="' + itemId + '"> <div class="monitor-result-image"> <img src=' + imageUrl + '> </div> <div class="monitor-result-information"> <a href="' + itemUrl + itemId + '" target="_blank">' + e.title + '</a> <br>';

							if (e.ListingType == "Auction") {
								html += 'Auction Price: $' + Number(e.CurrentPrice).toFixed(2);
							}
							else if (e.ListingType == "AuctionWithBIN") {
								html += 'Auction Price: $' + Number(e.CurrentPrice).toFixed(2) + '<br>';
								html += 'Fixed Price: $' + Number(e.BuyItNowPrice).toFixed(2);
							}
							else if ((e.ListingType == "FixedPrice") || (e.ListingType == "StoreInventory")) {
								html += 'Fixed Price: $' + Number(e.CurrentPrice).toFixed(2);
							}

							contentDiv.innerHTML = html + '</div> </div>' + contentDiv.innerHTML;

							sendNotification(e.title);

							itemIds.unshift(itemId);
						}
						else if (itemIds.length > 30) {
							var lastItemId = "#" + itemIds[itemIds.length - 1];

							$(lastItemId).remove();

							itemIds.splice(itemIds.length - 1, 1);
						}
					});
				}
			}
		});
	}

	function sendNotification(title) {
		if (!$("#desktopNotifications").is(":checked")) {
			return;
		}

		Notification.requestPermission().then(function(result) {
			if ((result === 'denied') || (result === 'default')) {
				return;
			}

			new Notification(title);
		});
	}

	function collapseSearchQuery() {
		searchQuery.slideToggle(500);

		search.toggleClass("fa-angle-down fa-angle-right")
	}

	search.click(function() {
		collapseSearchQuery();
	});

	$("#clearResults").click(function() {
		contentDiv.innerHTML = "<h5>Please start monitoring in order to display results.</h5>";
	});

	$('#startMonitoring').click(function() {
		var valid = $("#searchQueryForm").valid();

		if (!valid) {
			$('html, body').animate({
				scrollTop: $('#search').offset().top - $('#header').height()
			}, 500);

			return;
		}

		if (intervalId) {
			clearInterval(intervalId);
		}

		if (searchQuery.is(':visible')) {
			collapseSearchQuery();
		}

		var url = rssUrl + $("#keywords").val().replace(/ /g, '%20').replace(/"/g, '%22');

		var subcategoryId = $('#subcategoryId').val();
		var categoryId = $('#categoryId').val();

		if (subcategoryId && (subcategoryId != "All Subcategories")) {
			url += "&categoryId1=" + subcategoryId
		}
		else if (categoryId && (categoryId != "All Categories")) {
			url += "&categoryId1=" + categoryId
		}

		if ($("#searchDescription").is(":checked")) {
			url += "&descriptionSearch=true";
		}

		if ($("#freeShippingOnly").is(":checked")) {
			url += "&freeShipping=true";
		}

		if ($("#newCondition").is(":checked")) {
			url += "&condition1=New";
		}

		if ($("#usedCondition").is(":checked")) {
			url += "&condition1=Used";
		}

		if ($("#unspecifiedCondition").is(":checked")) {
			url += "&condition1=Unspecified";
		}

		if ($("#auctionListing").is(":checked")) {
			url += "&listingType1=AuctionWithBIN&listingType2=Auction";
		}

		if ($("#fixedPriceListing").is(":checked")) {
			url += "&listingType1=AuctionWithBIN&listingType2=FixedPrice";
		}

		var minPrice = $("#minPrice").val();
		var maxPrice = $("#maxPrice").val();

		if (minPrice && (minPrice > 0)) {
			url += "&minPrice=" + minPrice;
		}

		if (maxPrice && (maxPrice > 0)) {
			url += "&maxPrice=" + maxPrice;
		}

		fetchRss(encodeURIComponent(url + "&" + new Date().getTime()));

		intervalId = setInterval(function() {
			fetchRss(encodeURIComponent(url + "&" + new Date().getTime()));
		}, 5000);
	});

	$("#stopMonitoring").click(function() {
		if (intervalId) {
			clearInterval(intervalId);

			intervalId = null;
		}
	});
});