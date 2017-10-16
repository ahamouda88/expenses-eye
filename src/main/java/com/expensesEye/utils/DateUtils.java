package com.expensesEye.utils;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.Days;
import org.joda.time.LocalDate;

/**
 * A Utility class that perform operations on a {@link Date}(s)
 */
public final class DateUtils {

	private DateUtils() {
	}

	/**
	 * This method return the number of days starting from today/current date, till the given date
	 * 
	 * @param end
	 *            the end date
	 * @return number of days between current date, and the given end date, returns <b>-1</b> if given date is null, and
	 *         <b>0</b> if date is already passed
	 */
	public static int nbrOfDaysFromCurrent(Date end) {
		return nbrOfDaysBetween(Calendar.getInstance().getTime(), end);
	}

	/**
	 * This method return the number of days between two given dates
	 * 
	 * @param start
	 *            the start date
	 * @param end
	 *            the end date
	 * @return number of days between the start and the end dates, returns <b>-1</b> if any of the given dates is null,
	 *         and <b>0</b> if dates are invalid
	 */
	public static int nbrOfDaysBetween(Date start, Date end) {
		if (start == null || end == null) return -1;

		int days = getDaysBetween(start, end);
		return days > 0 ? days : 0;
	}

	/**
	 * This method will validate if the start date is before the end date and is equal to or after today's date, and
	 * also will validate if the end date is after the start and today's date
	 * 
	 * @param start
	 *            a start date
	 * @param end
	 *            an end date
	 * @return true if two dates are valid, false otherwise
	 */
	public static boolean isValidDateRange(Date start, Date end) {
		if (start == null || end == null) return false;

		Date todayDate = LocalDate.now().toDate();
		int startTodayDays = getDaysBetween(todayDate, start);
		int endTodayDays = getDaysBetween(todayDate, end);
		int startEndDays = getDaysBetween(start, end);

		return startTodayDays >= 0 && endTodayDays >= 0 && startEndDays >= 0;
	}

	/**
	 * This method returns a date given the year, month and day
	 * 
	 * @param year
	 *            a valid year
	 * @param month
	 *            a valid month
	 * @param day
	 *            a valid day
	 * @return a new created date
	 */
	public static Date getDate(int year, int month, int day) {
		LocalDate localDate = new LocalDate(year, month, day);
		return localDate.toDate();
	}

	private static int getDaysBetween(Date start, Date end) {
		LocalDate currentDate = new LocalDate(start);
		LocalDate endDate = new LocalDate(end);

		return Days.daysBetween(currentDate, endDate).getDays();
	}
}
