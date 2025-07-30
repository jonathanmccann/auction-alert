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

	public Category(String categoryId, String categoryName) {
		_categoryId = categoryId;
		_categoryName = categoryName;
	}

	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof Category)) {
			return false;
		}

		return _categoryId.equals(((Category)obj).getCategoryId());
	}

	public String getCategoryId() {
		return _categoryId;
	}

	public String getCategoryName() {
		return _categoryName;
	}

	public int hashCode() {
		return Integer.valueOf(_categoryId);
	}

	public void setCategoryId(String categoryId) {
		_categoryId = categoryId;
	}

	public void setCategoryName(String categoryName) {
		_categoryName = categoryName;
	}

	private String _categoryId;
	private String _categoryName;

}