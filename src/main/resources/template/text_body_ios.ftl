<#list searchResultModels as searchResultModel>
${searchResultModel.getItemTitle()}
ebay://launch?itm=${searchResultModel.getItemId()}
</#list>