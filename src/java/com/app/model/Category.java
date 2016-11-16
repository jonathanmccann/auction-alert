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

package com.app.model;

/**
 * @author Jonathan McCann
 */
public class Category {

	public Category() {
	}

	public Category(
		String categoryId, String categoryName, String categoryParentId,
		int categoryLevel) {

		_categoryId = categoryId;
		_categoryName = categoryName;
		_categoryParentId = categoryParentId;
		_categoryLevel = categoryLevel;
	}

	public String getCategoryId() {
		return _categoryId;
	}

	public int getCategoryLevel() {
		return _categoryLevel;
	}

	public String getCategoryName() {
		return _categoryName;
	}

	public String getCategoryParentId() {
		return _categoryParentId;
	}

	public void setCategoryId(String categoryId) {
		_categoryId = categoryId;
	}

	public void setCategoryLevel(int categoryLevel) {
		_categoryLevel = categoryLevel;
	}

	public void setCategoryName(String categoryName) {
		_categoryName = categoryName;
	}

	public void setCategoryParentId(String categoryParentId) {
		_categoryParentId = categoryParentId;
	}

	private String _categoryId;
	private int _categoryLevel;
	private String _categoryName;
	private String _categoryParentId;

}