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
import com.app.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jonathan McCann
 */
public class ReleaseDAO {

	public void addRelease(String releaseName, String version)
		throws DatabaseConnectionException, SQLException {

		_log.debug(
			"Adding new release with name: {} and version: {}", releaseName,
			version);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_ADD_RELEASE_SQL)) {

			preparedStatement.setString(1, releaseName);
			preparedStatement.setString(2, version);

			preparedStatement.executeUpdate();
		}
	}

	public String getReleaseVersion(String releaseName)
		throws DatabaseConnectionException, SQLException {

		_log.debug("Getting release version with name: {}", releaseName);

		try (Connection connection = DatabaseUtil.getDatabaseConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				_GET_RELEASE_VERSION_SQL)) {

			preparedStatement.setString(1, releaseName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString("version");
				}
				else {
					return "";
				}
			}
		}
	}

	private static final String _ADD_RELEASE_SQL =
		"INSERT INTO Release_(releaseName, version) VALUES(?, ?) ON " +
			"DUPLICATE KEY UPDATE releaseName=VALUES(releaseName), " +
				"version=VALUES(version)";

	private static final String _GET_RELEASE_VERSION_SQL =
		"SELECT version FROM Release_ WHERE releaseName = ?";

	private static final Logger _log = LoggerFactory.getLogger(
		ReleaseDAO.class);

}