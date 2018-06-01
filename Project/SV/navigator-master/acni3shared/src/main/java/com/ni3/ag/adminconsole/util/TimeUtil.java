/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil{
	public static Date getWeekStart(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date now = c.getTime();
		return now;
	}

	public static Date getMonthStart(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date now = c.getTime();
		return now;
	}

	public static Date getToday(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date now = c.getTime();
		return now;
	}

	public static Date truncToDate(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date d = c.getTime();
		return d;
	}

	public static Date truncToMinute(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date d = c.getTime();
		return d;
	}

	public static Date truncToSecond(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.MILLISECOND, 0);
		Date d = c.getTime();
		return d;
	}

	public static Date addDays(Date date, int days){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, days);
		Date d = c.getTime();
		return d;
	}

	public static Date addMinutes(Date date, int minutes){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, minutes);
		Date d = c.getTime();
		return d;
	}

	public static Date getTodayEnd(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		Date now = c.getTime();
		return now;
	}

	/**
	 * 
	 * @return duration between two dates in format: hh:mm:ss
	 */
	public static long getDurationInMillis(Date from, Date to){
		long millis = to.getTime() - from.getTime();
		return millis;
	}

	/**
	 * 
	 * @return time in format: hh:mm:ss
	 */
	public static String getFormattedTime(long millis){
		int diff = (int) (millis / 1000);
		int seconds = diff % 60;
		int minutes = (diff % 3600) / 60;
		int hours = diff / 3600;
		return hours + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
	}
}
