/**
 * Copyright (c) 2014-present Jonathan McCann
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package com.app.util;

import com.app.dao.SearchQueryDAO;
import com.app.exception.DatabaseConnectionException;
import com.app.exception.SearchQueryException;
import com.app.model.SearchQuery;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class SearchQueryUtil {

	public static void activateSearchQuery(int userId, int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.activateSearchQuery(userId, searchQueryId);
	}

	public static int addSearchQuery(SearchQuery searchQuery)
		throws DatabaseConnectionException, SearchQueryException, SQLException {

		if (exceedsMaximumNumberOfSearchQueries(searchQuery.getUserId())) {
			throw new SearchQueryException();
		}

		_validateSearchQuery(searchQuery);

		_escapeSearchQueryKeywords(searchQuery);
		_normalizeSearchQuery(searchQuery);

		return _searchQueryDAO.addSearchQuery(searchQuery);
	}

	public static void deactivateSearchQuery(int userId, int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.deactivateSearchQuery(userId, searchQueryId);
	}

	public static void deleteSearchQueries(int userId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.deleteSearchQueries(userId);
	}

	public static void deleteSearchQuery(int userId, int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		_searchQueryDAO.deleteSearchQuery(userId, searchQueryId);
	}

	public static String getCurrencySymbol(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		SearchQuery searchQuery = getSearchQuery(searchQueryId);

		return _CURRENCY_SYMBOLS.get(searchQuery.getGlobalId());
	}

	public static Map<String, String> getGlobalIds() {
		return _GLOBAL_IDS;
	}

	public static Map<String, String> getRssGlobalIds() {
		return _RSS_GLOBAL_IDS;
	}

	public static List<SearchQuery> getSearchQueries(int userId)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.getSearchQueries(userId);
	}

	public static List<SearchQuery> getSearchQueries(int userId, boolean active)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.getSearchQueries(userId, active);
	}

	public static SearchQuery getSearchQuery(int searchQueryId)
		throws DatabaseConnectionException, SQLException {

		return _searchQueryDAO.getSearchQuery(searchQueryId);
	}

	public static boolean exceedsMaximumNumberOfSearchQueries(int userId)
		throws DatabaseConnectionException, SQLException {

		int searchQueryCount = _searchQueryDAO.getSearchQueryCount(userId);

		if ((searchQueryCount + 1) >
				PropertiesValues.MAXIMUM_NUMBER_OF_SEARCH_QUERIES) {

			return true;
		}

		return false;
	}

	public static void updateSearchQuery(int userId, SearchQuery searchQuery)
		throws DatabaseConnectionException, SearchQueryException, SQLException {

		_escapeSearchQueryKeywords(searchQuery);
		_normalizeSearchQuery(searchQuery);
		_validateSearchQuery(searchQuery);

		_searchQueryDAO.updateSearchQuery(userId, searchQuery);
	}

	@Autowired
	public void setSearchQueryDAO(SearchQueryDAO searchQueryDAO) {
		_searchQueryDAO = searchQueryDAO;
	}

	private static void _escapeSearchQueryKeywords(SearchQuery searchQuery) {
		String keywords = searchQuery.getKeywords();

		searchQuery.setKeywords(
			_KEYWORDS_INVALID_CHARACTERS_PATTERN.matcher(keywords).replaceAll(""));
	}

	private static void _normalizeSearchQuery(SearchQuery searchQuery) {
		if (!searchQuery.isNewCondition() && !searchQuery.isUsedCondition() &&
			!searchQuery.isUnspecifiedCondition()) {

			searchQuery.setNewCondition(true);
			searchQuery.setUsedCondition(true);
			searchQuery.setUnspecifiedCondition(true);
		}

		if (!searchQuery.isAuctionListing() &&
			!searchQuery.isFixedPriceListing()) {

			searchQuery.setAuctionListing(true);
			searchQuery.setFixedPriceListing(true);
		}

		String categoryId = searchQuery.getCategoryId();
		String subcategoryId = searchQuery.getSubcategoryId();

		if (ValidatorUtil.isNull(categoryId) ||
			categoryId.equalsIgnoreCase("All Categories")) {

			searchQuery.setCategoryId("");
			searchQuery.setSubcategoryId("");
		}

		if (ValidatorUtil.isNull(subcategoryId) ||
			subcategoryId.equalsIgnoreCase("All Subcategories")) {

			searchQuery.setSubcategoryId("");
		}
	}

	private static void _validateSearchQuery(SearchQuery searchQuery)
		throws SearchQueryException{

		if (ValidatorUtil.isNull(searchQuery.getKeywords())) {
			throw new SearchQueryException();
		}

		if (searchQuery.getMinPrice() > searchQuery.getMaxPrice()) {
			throw new SearchQueryException();
		}
	}

	private static final Map<String, String> _CURRENCY_SYMBOLS = new HashMap<>();
	private static final Map<String, String> _GLOBAL_IDS = new LinkedHashMap<>();
	private static final Map<String, String> _RSS_GLOBAL_IDS =
		new LinkedHashMap<>();

	private static final Pattern _KEYWORDS_INVALID_CHARACTERS_PATTERN =
		Pattern.compile("[<>]");

	private static SearchQueryDAO _searchQueryDAO;

	static {
		_CURRENCY_SYMBOLS.put("EBAY-US", "$");
		_CURRENCY_SYMBOLS.put("EBAY-ENCA", "C $");
		_CURRENCY_SYMBOLS.put("EBAY-GB", "£");
		_CURRENCY_SYMBOLS.put("EBAY-AT", "€");
		_CURRENCY_SYMBOLS.put("EBAY-AU", "AU $");
		_CURRENCY_SYMBOLS.put("EBAY-FRBE", "€");
		_CURRENCY_SYMBOLS.put("EBAY-CH", "CHF ");
		_CURRENCY_SYMBOLS.put("EBAY-DE", "€");
		_CURRENCY_SYMBOLS.put("EBAY-ES", "€");
		_CURRENCY_SYMBOLS.put("EBAY-FR", "€");
		_CURRENCY_SYMBOLS.put("EBAY-IE", "€");
		_CURRENCY_SYMBOLS.put("EBAY-IT", "€");
		_CURRENCY_SYMBOLS.put("EBAY-NL", "€");

		_GLOBAL_IDS.put("EBAY-US", "United States");
		_GLOBAL_IDS.put("EBAY-ENCA", "Canada");
		_GLOBAL_IDS.put("EBAY-GB", "United Kingdom");
		_GLOBAL_IDS.put("EBAY-AT", "Austria");
		_GLOBAL_IDS.put("EBAY-AU", "Australia");
		_GLOBAL_IDS.put("EBAY-FRBE", "Belgium");
		_GLOBAL_IDS.put("EBAY-CH", "Switzerland");
		_GLOBAL_IDS.put("EBAY-DE", "Germany");
		_GLOBAL_IDS.put("EBAY-ES", "Spain");
		_GLOBAL_IDS.put("EBAY-FR", "France");
		_GLOBAL_IDS.put("EBAY-IE", "Ireland");
		_GLOBAL_IDS.put("EBAY-IT", "Italy");
		_GLOBAL_IDS.put("EBAY-NL", "Netherlands");

		_RSS_GLOBAL_IDS.put("1", "United States");
		_RSS_GLOBAL_IDS.put("7", "Canada");
		_RSS_GLOBAL_IDS.put("15", "United Kingdom");
		_RSS_GLOBAL_IDS.put("3", "Austria");
		_RSS_GLOBAL_IDS.put("4", "Australia");
		_RSS_GLOBAL_IDS.put("5", "Belgium");
		_RSS_GLOBAL_IDS.put("14", "Switzerland");
		_RSS_GLOBAL_IDS.put("11", "Germany");
		_RSS_GLOBAL_IDS.put("13", "Spain");
		_RSS_GLOBAL_IDS.put("10", "France");
		_RSS_GLOBAL_IDS.put("2", "Ireland");
		_RSS_GLOBAL_IDS.put("12", "Italy");
		_RSS_GLOBAL_IDS.put("16", "Netherlands");
	}

}