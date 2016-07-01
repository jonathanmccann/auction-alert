<#ftl strip_whitespace = true>
<#list searchQueryResultMap?keys as searchQuery>
Keywords: ${searchQuery.getKeywords()}

<#list searchQueryResultMap?api.get(searchQuery) as searchResult>
Item: ${searchResult.getItemTitle()}
<#if (searchResult.getAuctionPrice() > 0)>
Auction Price: ${searchResult.getAuctionPrice()?string.currency}
</#if>
<#if (searchResult.getFixedPrice() > 0)>
Fixed Price: ${searchResult.getFixedPrice()?string.currency}
</#if>
URL: ${searchResult.getItemURL()}

</#list>
</#list>
<a href="/unsubscribe?emailAddress=${emailAddress}&unsubscribeToken=${unsubscribeToken}">Unsubscribe</a>