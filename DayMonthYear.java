package sk.upjs.ics.novotnyr.daygrid;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.util.MonthDisplayHelper;

public class DayMonthYear implements Serializable {
	private int day;
	
	private int month;
	
	private int year;
	
	public DayMonthYear() {
		this(now().year, now().month, now().day);
	}
	
	private DayMonthYear(Calendar calendar) {
		this(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	private static DayMonthYear now() {
		Calendar calendar = Calendar.getInstance();
		return new DayMonthYear(calendar);
	}
	
	public DayMonthYear(DayMonthYear day, int differentDayOfMonth) {
		this(day.getYear(), day.getMonth(), differentDayOfMonth);
	}
	
	public DayMonthYear(int year, int month, int day) {
		super();
		this.day = day;
		this.month = month;
		this.year = year;
	}

	public DayMonthYear(MonthDisplayHelper monthDisplayHelper, int day) {
		this(monthDisplayHelper.getYear(), monthDisplayHelper.getMonth() + 1, day);
	}
	
	public DayMonthYear nextMonth() {
		Calendar calendar = new GregorianCalendar(year, month - 1, day);
		calendar.add(Calendar.MONTH, 1);
		
		return new DayMonthYear(calendar); 
	}

	public DayMonthYear previousMonth() {
		Calendar calendar = new GregorianCalendar(year, month - 1, day);
		calendar.add(Calendar.MONTH, -1);
		
		return new DayMonthYear(calendar); 
	}
	
	public long toUnixTimestamp() {
		Calendar calendar = new GregorianCalendar(year, month - 1, day);
		return calendar.getTimeInMillis() / 1000;		
	}
	
	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}
	
	public String toSqlLiteString() {
		return String.format("%d-%02d-%02d", year, month, day);
	}	

	public MonthDisplayHelper toMonthDisplayHelper() {
		return new MonthDisplayHelper(year, month - 1, Calendar.MONDAY);
	}	

	@Override
	public String toString() {
		return toSqlLiteString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + month;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DayMonthYear other = (DayMonthYear) obj;
		if (day != other.day)
			return false;
		if (month != other.month)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	
	
}
