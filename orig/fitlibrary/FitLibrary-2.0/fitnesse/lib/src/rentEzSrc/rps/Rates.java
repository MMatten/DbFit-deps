package rps;

import java.util.Date;
import java.util.StringTokenizer;

import rps.paymentMethod.Money;
import rps.time.Duration;

/**
 * Contains the charge Rates for an item. It's immutable
 *
 */
public class Rates {
	private final Money hourly;
	private Money daily;
	private Money weekly;
	
	public Rates(Rates old) {
		this(old.hourly,old.daily,old.weekly);
	}
	public Rates(Money hourly, Money daily, Money weekly) {
		this.hourly = hourly;
		this.daily = daily;
		this.weekly = weekly;
		
		if(daily.isZero()) {
			this.daily = hourly.times(24);
		}
		if(weekly.isZero()) {
			this.weekly = daily.times(7);
		}
		
	}
	public Money getDaily() {
		return daily;
	}
	public Money getHourly() {
		return hourly;
	}
	public Money getWeekly() {
		return weekly;
	}
	public Money costForPeriod(Duration duration) {
		Duration period = fairDuration(duration);
		Money hours = hourly.times(period.getHours());
        Money days = daily.times(period.getDays());
        Money weeks = weekly.times(period.getWeeks());
        return hours.plus(days).plus(weeks);
	}
	public Duration fairDuration(Date start, Date end) {
		Duration duration = Duration.createDuration(start,end);
		return fairDuration(duration);
	}
	public Duration fairDuration(Duration duration) {
		int hours = duration.getHours();
		int days = duration.getDays();
		int weeks = duration.getWeeks();	
		if (hourly.times(hours).greaterThanEqual(daily)) {
			hours = 0;
			days++;
		}
		if(!hourly.times(days*24).greaterThanEqual(daily.times(days))){
			hours += days*24;
			days = 0;
		}
		if (daily.times(days).greaterThanEqual(weekly)) {
			days = 0;
			weeks++;
		}
		if(!daily.times(weeks*7).greaterThanEqual(weekly.times(weeks))){
			days += weeks*7;
			weeks = 0;
		}
		if (weeks > 0)
		    hours = 0;
		return new Duration(hours,days,weeks);
	}
    @Override
	public boolean equals(Object o) {
    	if (o instanceof Rates) {
			Rates other = (Rates)o;
    		return hourly.equals(other.hourly) && daily.equals(other.daily) && weekly.equals(other.weekly);
    	}
    	return false;
    }	
    @Override
	public String toString() {
        return hourly+", "+daily+", "+weekly;
    }
    public static Rates parse(String s) {
    	StringTokenizer tok = new StringTokenizer(s,",");
    	Money hourly = Money.parse(tok.nextToken());
    	Money daily = Money.parse(tok.nextToken());
    	Money weekly = Money.parse(tok.nextToken());
    	if (tok.hasMoreTokens())
    		throw new RuntimeException("Invalis Rates: three values only");
    	return new Rates(hourly,daily,weekly);
    }
}
