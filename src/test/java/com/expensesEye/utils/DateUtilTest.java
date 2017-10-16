package com.expensesEye.utils;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.expensesEye.utils.DateUtils;

public class DateUtilTest {

	@Test
	public void testNbrOfDaysFromCurrentValidDate() {
		Date date = DateUtils.getDate(2017, 1, 1);

		int expectedDays = 0;
		int actualDays = DateUtils.nbrOfDaysFromCurrent(date);

		Assert.assertEquals(expectedDays, actualDays);
	}

	@Test
	public void testNbrOfDaysFromCurrentInValidDate() {
		int expectedDays = -1;
		int actualDays = DateUtils.nbrOfDaysFromCurrent(null);

		Assert.assertEquals(expectedDays, actualDays);
	}

	@Test
	public void testNbrOfDaysBetweenValidDates() {
		Date startDate = DateUtils.getDate(2017, 1, 1);
		Date endDate = DateUtils.getDate(2017, 1, 16);

		int expectedDays = 15;
		int actualDays = DateUtils.nbrOfDaysBetween(startDate, endDate);

		Assert.assertEquals(expectedDays, actualDays);
	}

	@Test
	public void testNbrOfDaysBetweenInValidDates() {
		Date startDate = DateUtils.getDate(2017, 1, 30);
		Date endDate = DateUtils.getDate(2017, 1, 16);

		int expectedDays = 0;
		int actualDays = DateUtils.nbrOfDaysBetween(startDate, endDate);

		Assert.assertEquals(expectedDays, actualDays);
	}

	@Test
	public void testValidDateRange() {
		Date startDate = DateUtils.getDate(2020, 2, 23);
		Date endDate = DateUtils.getDate(2020, 2, 25);

		boolean expected = true;
		boolean actual = DateUtils.isValidDateRange(startDate, endDate);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testInValidDateRange() {
		Date startDate = DateUtils.getDate(2020, 2, 23);
		Date endDate = DateUtils.getDate(2020, 2, 22);

		boolean expected = false;
		boolean actual = DateUtils.isValidDateRange(startDate, endDate);
		Assert.assertEquals(expected, actual);
	}
}
