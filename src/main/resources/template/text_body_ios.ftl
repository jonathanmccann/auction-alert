<#list searchResults as searchResult>
${searchResult.getItemTitle()}
ebay://launch?itm=${searchResult.getItemId()}
</#list>