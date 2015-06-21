package rent;

import rps.Rates;
import rps.paymentMethod.Money;
import rps.time.Duration;

public class CalculateChargeFairly {
	public static int hours = 0;
	public static int days = 0;
	public static int weeks = 0;
	public static double cost;
	private static Rates rates;
	private static Money perHourCost;
	private static Money perDayCost;
	private static Money perWeekCost;
	
    public CalculateChargeFairly(Rates setRates) {
		rates = setRates;
    }
    public CalculateChargeFairly() {
    	//
    }
	public Money costInDollarDollarSlashHourDollarSlashDayHoursDays(String perHour, String perDay, String setHours, String setDays){
		weeks = 0;
		if (!perHour.equals(""))
			perHourCost = new Money(Double.parseDouble(perHour));
		if (!perDay.equals(""))
			perDayCost = new Money(Double.parseDouble(perDay));
		rates = new Rates(perHourCost, perDayCost, new Money(500000));
		if (!setHours.equals(""))
			hours = Integer.parseInt(setHours);
		if (!setDays.equals(""))
			days = Integer.parseInt(setDays);
		return costInDollar();
	}
	public Money costInDollarDollarSlashHourDollarSlashDayDollarSlashWeekHoursDaysWeeks(String perHour,String perDay, String perWeek, String setHours, String setDays, String setWeeks){
		if(!perHour.equals(""))
			perHourCost = new Money(Double.parseDouble(perHour));
		if (!perDay.equals(""))
			perDayCost = new Money(Double.parseDouble(perDay));
		if (!perWeek.equals(""))
			perWeekCost = new Money(Double.parseDouble(perWeek));
		rates = new Rates(perHourCost, perDayCost, perWeekCost);
		if(!setHours.equals(""))
			hours = Integer.parseInt(setHours);
		if (!setDays.equals(""))
			days = Integer.parseInt(setDays);
		if (!setWeeks.equals(""))
			weeks = Integer.parseInt(setWeeks);
		return costInDollar() ;
	}
    public Money costInDollar() {
		return rates.costForPeriod(new Duration(hours,days,weeks));
	}
	public Money costInDollarDuration(Duration duration) {
		Duration period = rates.fairDuration(duration);
		return rates.costForPeriod(period);
	    
	}
}
