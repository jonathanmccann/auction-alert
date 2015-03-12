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

package com.app.dao;

import com.app.exception.DatabaseConnectionException;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Jonathan McCann
 */
public interface SearchQueryPreviousResultDAO {

	public void addSearchQueryPreviousResult(
			int searchQueryId, String searchResultItemId)
		throws DatabaseConnectionException, SQLException;

	public void deleteSearchQueryPreviousResults(int searchQueryId)
		throws DatabaseConnectionException, SQLException;

	public void deleteSearchQueryPreviousResult(int searchQueryId)
		throws DatabaseConnectionException, SQLException;

	public List<String> getSearchQueryPreviousResults(
			int searchQueryId)
		throws DatabaseConnectionException, SQLException;

	public int getSearchQueryPreviousResultsCount(int searchQueryId)
		throws DatabaseConnectionException, SQLException;

}