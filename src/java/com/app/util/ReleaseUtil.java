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

import com.app.dao.ReleaseDAO;
import com.app.exception.DatabaseConnectionException;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jonathan McCann
 */
@Service
public class ReleaseUtil {

	public static void addRelease(String releaseName, String version)
		throws DatabaseConnectionException, SQLException {

		_releaseDAO.addRelease(releaseName, version);
	}

	public static void deleteRelease(String releaseName)
		throws DatabaseConnectionException, SQLException {

		_releaseDAO.deleteRelease(releaseName);
	}

	public static String getReleaseVersion(String releaseName)
		throws DatabaseConnectionException, SQLException {

		return _releaseDAO.getReleaseVersion(releaseName);
	}

	@Autowired
	public void setReleaseDAO(ReleaseDAO releaseDAO) {
		_releaseDAO = releaseDAO;
	}

	private static ReleaseDAO _releaseDAO;

}