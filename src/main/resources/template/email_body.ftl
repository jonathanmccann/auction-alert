<#ftl strip_whitespace = true>
Keywords: ${searchQuery.getKeywords()}
<#list searchResults as searchResult>
Item: ${searchResult.getItemTitle()}
<#if (searchResult.getAuctionPrice() > 0)>
Auction Price: ${searchResult.getAuctionPrice()?string.currency}
</#if>
<#if (searchResult.getFixedPrice() > 0)>
Fixed Price: ${searchResult.getFixedPrice()?string.currency}
</#if>
URL: ${searchResult.getItemURL()}
</#list>