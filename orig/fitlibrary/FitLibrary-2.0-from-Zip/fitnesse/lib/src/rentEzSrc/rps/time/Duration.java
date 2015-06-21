package rps.time;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class Duration {
	private int hours;
	private int days;
	private int weeks;
	
	public Duration(int hours, int days, int weeks) {
		this.hours = hours;
		this.days = days;
		this.weeks = weeks;
	}
	public static Duration createDuration(Date startDate, Date endDate) {
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		int weeks = end.get(Calendar.WEEK_OF_YEAR)-start.get(Calendar.WEEK_OF_YEAR);
		int days = end.get(Calendar.DAY_OF_YEAR)-start.get(Calendar.DAY_OF_YEAR);
		int hours = end.get(Calendar.HOUR)-start.get(Calendar.HOUR);
		if (end.get(Calendar.MINUTE)-start.get(Calendar.MINUTE) > 10)
		    hours++;
		return new Duration(hours % 24,days % 7,weeks);
	}
	public int getDays() {
		return days;
	}
	public int getHours() {
		return hours;
	}
	public int getWeeks() {
		return weeks;
	}
	public MyDate dateAfter(MyDate startDate) {
		return startDate.plus(this);
	}
	public int totalHours() {
		return ((weeks * 7) + days ) * 24 + hours;
	}
	public boolean greaterThan(Duration other) {
		return totalHours() > other.totalHours();
	}
	public boolean lessThan(Duration other) {
		return totalHours() < other.totalHours();
	}
	public Duration subtract(Duration other) {
		return new Duration(hours - other.hours, days - other.days, weeks - other.weeks);
	}
	public Duration add(Duration other) {
		return new Duration(hours + other.hours, days + other.days, weeks + other.weeks);
	}
	public Duration normalize() {
		return new Duration(hours % 24, (days + hours / 24) % 7, weeks + (days + hours / 24) / 7);
	}
	public static Duration parse(String sInitial) {
        String s = sInitial.trim();
        int hours = 0, days = 0, weeks = 0;
        StringTokenizer tok = new StringTokenizer(s," ");
        while (tok.hasMoreTokens()) {
            String number = tok.nextToken();
            if (!tok.hasMoreElements())
                throw new RuntimeException("Missing units");
            String units = tok.nextToken();
            if (units.equals("hour") || units.equals("hours"))
                hours = Integer.parseInt(number);
            if (units.equals("day") || units.equals("days"))
                days = Integer.parseInt(number);
            if (units.equals("week") || units.equals("weeks"))
                weeks = Integer.parseInt(number);
        }
        return new Duration(hours,days,weeks);
    }
    @Override
	public boolean equals(Object o) {
    	if (o instanceof Duration) {
    		Duration other = (Duration)o;
    		return hours == other.hours && days == other.days && weeks == other.weeks;
    	}
    	return false;
    }
    @Override
	public String toString() {
        String s = "";
        if (hours > 1)
            s += hours+" hours ";
        else if (hours == 1)
            s += "1 hour ";
        if (days > 1)
            s += days+" days ";
        else if (days == 1)
            s += "1 day ";
        if (weeks > 1)
            s += weeks+" weeks ";
        else if (weeks == 1)
            s += "1 week ";
        return s.trim();
    }
    public String value() {
    	String dur = toString();
    	int space = dur.indexOf(" ");
    	return dur.substring(0,space);
    }
    public String units() {
    	String dur = toString();
    	int space = dur.indexOf(" ");
    	return dur.substring(space+1);
    }
}
