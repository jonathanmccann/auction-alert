<#list searchResultModels as searchResultModel>
${searchResultModel.getItemTitle()}
eBay://item/view?id=${searchResultModel.getItemId()}
</#list>