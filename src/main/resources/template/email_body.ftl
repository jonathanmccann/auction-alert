<#ftl strip_whitespace = true>
<#list searchResultModels as searchResultModel>
Item: ${searchResultModel.getItemTitle()}
<#if (searchResultModel.getAuctionPrice() > 0)>
Auction Price: ${searchResultModel.getAuctionPrice()?string.currency}
</#if>
<#if (searchResultModel.getFixedPrice() > 0)>
Fixed Price: ${searchResultModel.getFixedPrice()?string.currency}
</#if>
URL: ${searchResultModel.getItemURL()}
</#list>