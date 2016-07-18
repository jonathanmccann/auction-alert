$(window).load(function() {
	var itemIds = [];

	var campaignId = $("#campaignId").val();

	var contentDiv = document.getElementById('content');

	var intervalId;

	var itemUrl = "http://rover.ebay.com/rover/1/711-53200-19255-0/1?icep_ff3=2&toolid=10001&campid=" + campaignId + "&ipn=psmain&icep_vectorid=229466&kwid=902099&mtid=824&kw=lg&icep_item=";

	var rssUrl = "http://rest.ebay.com/epn/v1/find/item.rss?programid=1&toolid=10039&lgeo=1&feedType=rss&sortOrder=StartTimeNewest&hideDuplicateItems=true&campaignid=" + campaignId + "&keyword=";

	function fetchRss(url) {
		$.ajax({
			url: "https://query.yahooapis.com/v1/public/yql?q=select%20title%2C%20guid%20from%20rss(0%2C10)%20where%20url%20%3D%20%22" + url + "%22&format=json",
			success: function (data) {
				if (data.query && data.query.results && data.query.results.item) {
					$.each(data.query.results.item, function (i, e) {
						var itemId = e.guid;

						if (itemIds.indexOf(itemId) < 0) {
							contentDiv.innerHTML = '<div id="' + itemId + '"> <a href="' + itemUrl + itemId + '" target="_blank">' + e.title + '</a> </div>' + contentDiv.innerHTML;

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

	$("#clearResults").click(function() {
		contentDiv.innerHTML = "";
	});

	$('#startMonitoring').click(function() {
		var valid = $("#monitorForm").valid();

		if (!valid) {
			$('html, body').animate({
				scrollTop: $('#search').offset().top - $('#header').height()
			}, 500);

			return;
		}

		if (intervalId) {
			clearInterval(intervalId);
		}

		if ($("#searchQuery").is(':visible')) {
			$("#searchQuery").slideToggle(500);

			$(this).toggleClass("fa-angle-down fa-angle-right")
		}

		$(header).find('i').toggleClass('fa-angle-down fa-angle-right')

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