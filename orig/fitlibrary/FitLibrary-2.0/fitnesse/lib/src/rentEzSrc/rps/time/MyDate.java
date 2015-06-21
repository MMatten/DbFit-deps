package rps.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Rick Mugridge, July 2005
 * 
 * Immutable Date that hides details of Date processing
 *
 */
public class MyDate implements Comparable<MyDate> {
	private final Date time;
    private static final DateFormat DATE_FORMAT
		= new SimpleDateFormat("yyyy/MM/dd HH:mm");

	public MyDate() {
		this.time = new Date();
	}
	public MyDate(Date time) {
		this.time = time;
	}
	public MyDate(MyDate date) {
		this.time = date.time;
	}
	public MyDate plus(Duration duration) {
		Date startDate = new Date(time.getTime());
		Calendar end = Calendar.getInstance();
		end.setTime(startDate);
		end.add(Calendar.HOUR_OF_DAY, duration.getHours());
		end.add(Calendar.DAY_OF_YEAR, duration.getDays());
		end.add(Calendar.WEEK_OF_YEAR, duration.getWeeks());
		return new MyDate(end.getTime());
	}
	public Duration durationTo(MyDate endDate) {
		long mins = (endDate.time.getTime() - time.getTime()) / 60000;
		if (mins < 0)
			throw new RuntimeException("A Duration can't be negative!");
		int hours = (int)(mins / 60) % 24;
		int days =  (int)(mins / (60 * 24)) % 7;
		int weeks = (int) mins / (60*24*7);
		return new Duration(hours % 24,days % 7,weeks);
	}
	public boolean before(MyDate otherTime) {
		return time.before(otherTime.time);
	}
	public boolean before(Date otherTime) {
		return time.before(otherTime);
	}
	public MyDate afterMonths(int months) {
		Calendar maintenanceDue = Calendar.getInstance();
		maintenanceDue.setTime(time);
		maintenanceDue.add(Calendar.MONTH, months);
		return new MyDate(maintenanceDue.getTime());
	}
	public boolean after(MyDate otherDate) {
		return time.after(otherDate.time);
	}
	public int compareTo(MyDate otherDate) {
		return time.compareTo(otherDate.time);
	}
	public static MyDate parse(String s) throws ParseException {
		return new MyDate(DATE_FORMAT.parse(s));
	}
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MyDate))
			return false;
		MyDate other = (MyDate)object;
		return time.equals(other.time);
	}
	@Override
	public String toString() {
		return DATE_FORMAT.format(time);
	}
}
