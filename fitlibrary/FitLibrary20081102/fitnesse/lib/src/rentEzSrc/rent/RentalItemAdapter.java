/*
 * @author Rick Mugridge on Sep 26, 2004
 * Released under the terms of the GNU General Public License version 2 or later.
 *
 */
package rent;

import rps.RentalItemType;
import rps.paymentMethod.Money;

/**
 *
 */
public class RentalItemAdapter {
    private RentalItemType hireItemType;
	
	public RentalItemAdapter(RentalItemType hireItemType) {
	    this.hireItemType = hireItemType;
	}
    public Money getBond() {
        return hireItemType.getDeposit();
    }
    public Money getDailyRate() {
        return hireItemType.getRates().getDaily();
    }
    public int getFreeHash() {
        return hireItemType.getFreeCount();
    }
    public int getFreeCount() {
        return hireItemType.getFreeCount();
    }
    public Money getHourlyRate() {
        return hireItemType.getRates().getHourly();
    }
    public String getName() {
        return hireItemType.getName();
    }
    public Money getWeeklyRate() {
        return hireItemType.getRates().getWeekly();
    }
}
