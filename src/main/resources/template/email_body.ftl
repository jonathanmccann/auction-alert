<#compress>
<#list searchResultModels as searchResultModel>
	Item: ${searchResultModel.getItemTitle()}

	<#if (searchResultModel.getAuctionPrice() > 0) >
		Auction Price: $${searchResultModel.getAuctionPrice()}
	</#if>

	<#if (searchResultModel.getFixedPrice() > 0) >
		Fixed Price: $${searchResultModel.getFixedPrice()}
	</#if>

	URL: ${searchResultModel.getItemURL()}
</#list>
</#compress>