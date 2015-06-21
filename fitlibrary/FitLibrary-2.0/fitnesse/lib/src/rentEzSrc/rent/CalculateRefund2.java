package rent;
import rps.Rates;
import rps.paymentMethod.Money;
import rps.time.Duration;
import fit.Fixture;

/*
 * @author Rick Mugridge 21/07/2004
 * Released under the terms of the GNU General Public License version 2 or later.
 */

/**
 * 
 */
public class CalculateRefund2 extends fitlibrary.DoFixture {
	public Fixture refundDollarPerHourPerDayPerWeek(Rates rates) {
		return new Refund(rates);
	}
	public Fixture refundDollarPerHourPerDayPerWeek(Money hourly, Money daily, Money weekly) {
		return new Refund(new Rates(hourly,daily,weekly));
	}
	public class Refund extends fitlibrary.CalculateFixture {
		private Rates rates;
		
		public Refund(Rates rates) {
			this.rates = rates;
		}
		public Money refundPaidTimeActualTime(Duration paid, Duration actual) {
			return rates.costForPeriod(paid).minus(rates.costForPeriod(actual));
		}
		@SuppressWarnings("unchecked")
		@Override
		public Object parse(String s, Class type) throws Exception {
			if (type == Duration.class)
			    return Duration.parse(s);
			return super.parse(s, type);
		}
	}
}
