package rent;

import fitlibrary.CalculateFixture;
import rps.Rates;
import rps.time.Duration;

/*
 * @author Rick Mugridge 21/07/2004
 * Released under the terms of the GNU General Public License version 2 or later.
 */

/**
 * 
 */
public class FairCharge extends CalculateFixture { //COPY:ALL
	private Rates rates; //COPY:ALL
	 //COPY:ALL
	public FairCharge(Rates rates) { //COPY:ALL
		this.rates = rates; //COPY:ALL
	} //COPY:ALL
	public Duration fairlyMinusChargedDurationDuration(Duration duration) { //COPY:ALL
		return rates.fairDuration(duration); //COPY:ALL
	} //COPY:ALL
} //COPY:ALL
