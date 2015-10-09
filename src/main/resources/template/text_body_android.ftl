<#list searchResults as searchResult>
${searchResult.getItemTitle()}
eBay://item/view?id=${searchResult.getItemId()}
</#list>