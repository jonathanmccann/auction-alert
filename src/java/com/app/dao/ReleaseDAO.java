/**
 * Copyright (c) 2015-present Jonathan McCann
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

/**
 * @author Jonathan McCann
 */
public interface ReleaseDAO {

	public void addRelease(String releaseName, String version) throws Exception;

	public void deleteRelease(String releaseName) throws Exception;

	public String getReleaseVersion(String releaseName) throws Exception;

}