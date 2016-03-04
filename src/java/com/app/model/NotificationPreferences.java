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
public class NotificationPreferences {

	public int getEndOfDay() {
		return _endOfDay;
	}

	public int getStartOfDay() {
		return _startOfDay;
	}

	public int getUserId() {
		return _userId;
	}

	public boolean isBasedOnTime() {
		return _basedOnTime;
	}

	public boolean isEmailNotification() {
		return _emailNotification;
	}

	public boolean isTextNotification() {
		return _textNotification;
	}

	public boolean isWeekdayDayEmailNotification() {
		return _weekdayDayEmailNotification;
	}

	public boolean isWeekdayDayTextNotification() {
		return _weekdayDayTextNotification;
	}

	public boolean isWeekdayNightEmailNotification() {
		return _weekdayNightEmailNotification;
	}

	public boolean isWeekdayNightTextNotification() {
		return _weekdayNightTextNotification;
	}

	public boolean isWeekendDayEmailNotification() {
		return _weekendDayEmailNotification;
	}

	public boolean isWeekendDayTextNotification() {
		return _weekendDayTextNotification;
	}

	public boolean isWeekendNightEmailNotification() {
		return _weekendNightEmailNotification;
	}

	public boolean isWeekendNightTextNotification() {
		return _weekendNightTextNotification;
	}

	public void setBasedOnTime(boolean basedOnTime) {
		_basedOnTime = basedOnTime;
	}

	public void setEmailNotification(boolean emailNotification) {
		_emailNotification = emailNotification;
	}

	public void setEndOfDay(int endOfDay) {
		_endOfDay = endOfDay;
	}

	public void setStartOfDay(int startOfDay) {
		_startOfDay = startOfDay;
	}

	public void setTextNotification(boolean textNotification) {
		_textNotification = textNotification;
	}

	public void setUserId(int userId) {
		_userId = userId;
	}

	public void setWeekdayDayEmailNotification(
		boolean weekdayDayEmailNotification) {

		_weekdayDayEmailNotification = weekdayDayEmailNotification;
	}

	public void setWeekdayDayTextNotification(
		boolean weekdayDayTextNotification) {

		_weekdayDayTextNotification = weekdayDayTextNotification;
	}

	public void setWeekdayNightEmailNotification(
		boolean weekdayNightEmailNotification) {

		_weekdayNightEmailNotification = weekdayNightEmailNotification;
	}

	public void setWeekdayNightTextNotification(
		boolean weekdayNightTextNotification) {

		_weekdayNightTextNotification = weekdayNightTextNotification;
	}

	public void setWeekendDayEmailNotification(
		boolean weekendDayEmailNotification) {

		_weekendDayEmailNotification = weekendDayEmailNotification;
	}

	public void setWeekendDayTextNotification(
		boolean weekendDayTextNotification) {

		_weekendDayTextNotification = weekendDayTextNotification;
	}

	public void setWeekendNightEmailNotification(
		boolean weekendNightEmailNotification) {

		_weekendNightEmailNotification = weekendNightEmailNotification;
	}

	public void setWeekendNightTextNotification(
		boolean weekendNightTextNotification) {

		_weekendNightTextNotification = weekendNightTextNotification;
	}

	private boolean _basedOnTime;
	private boolean _emailNotification;
	private int _endOfDay;
	private int _startOfDay;
	private boolean _textNotification;
	private int _userId;
	private boolean _weekdayDayEmailNotification;
	private boolean _weekdayDayTextNotification;
	private boolean _weekdayNightEmailNotification;
	private boolean _weekdayNightTextNotification;
	private boolean _weekendDayEmailNotification;
	private boolean _weekendDayTextNotification;
	private boolean _weekendNightEmailNotification;
	private boolean _weekendNightTextNotification;

}