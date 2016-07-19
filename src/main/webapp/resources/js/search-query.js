function getSearchQueryResults(searchQueryId, active) {
	$.ajax({
		url: '/search_query_results',
		data: ({searchQueryId : searchQueryId}),
		success: function(data) {
			$("#searchQueryId").val(searchQueryId);

			if (active) {
				$("#activateButton").hide();
				$("#deactivateButton").show();
			}
			else {
				$("#activateButton").show();
				$("#deactivateButton").hide();
			}

			$("#searchQueryForm").fadeIn('slow');

			var resultsLength = data.length;

			if (resultsLength == 0) {
				$('#results').hide().html("There are no results for this query.").fadeIn('slow');
			}
			else {
				var html = "";

				for (var i = 0; i < data.length; i++) {
					html += '<div class="result-image">';
					html += '<img alt="' + data[i].galleryURL + '" src="' + data[i].galleryURL + '">';
					html += '</div>';
					html += '<div class="result-information">';
					html += '<a href="' + data[i].itemURL + '" target="_blank">' + data[i].itemTitle + '</a> <br>';

					if (data[i].auctionPrice > 0) {
						html += 'Auction Price: $' + data[i].auctionPrice.toFixed(2) + '<br>';
					}

					if (data[i].fixedPrice > 0) {
						html += 'Fixed Price: $' + data[i].fixedPrice.toFixed(2);
					}

					html += "</div>";
				}

				$('#results').hide().html(html).fadeIn('slow');
			}
		}
	});
}