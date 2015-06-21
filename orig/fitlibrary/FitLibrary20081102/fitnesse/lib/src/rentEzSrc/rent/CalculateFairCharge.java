/*
 * @author Rick Mugridge 4/10/2004
 * Released under the terms of the GNU General Public License version 2 or later.
 */
package rent;

import rps.Rates;
import rps.paymentMethod.Money;
import fitlibrary.DoFixture;

/**
 * 
 */
public class CalculateFairCharge extends DoFixture { //COPY:ALL
	public FairCharge ratesDollarPerHourPerDayPerWeek(Money perHour,  //COPY:ALL
	        Money perDay, Money perWeek) { //COPY:ALL
		return new FairCharge(new Rates(perHour,perDay,perWeek)); //COPY:ALL
	} //COPY:ALL
	public FairCharge dollarPerHourPerDayPerWeek(Money perHour,
	        Money perDay, Money perWeek) {
		return new FairCharge(new Rates(perHour,perDay,perWeek));
	}
} //COPY:ALL
