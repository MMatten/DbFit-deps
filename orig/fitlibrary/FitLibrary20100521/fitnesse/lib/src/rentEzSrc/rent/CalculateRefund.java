package rent;
import fit.Fixture;

/*
 * @author Rick Mugridge 21/07/2004
 * Released under the terms of the GNU General Public License version 2 or later.
 */

/**
 * 
 */
public class CalculateRefund extends fitlibrary.DoFixture {
	public Fixture rateDollarSlashHourSlashDaySlashWeek(double perHour, double perDay, double perWeek) {
		return new Refund(perHour,perDay,perWeek);
	}
	public class Refund extends fit.ColumnFixture {
		public int hours1, days1, weeks1;
		public int hours2, days2, weeks2;
		private FairChargeWithColumn charge1, charge2;
		
		public Refund(double perHour, double perDay, double perWeek) {
			charge1 = new FairChargeWithColumn(perHour,perDay,perWeek);
			charge2 = new FairChargeWithColumn(perHour,perDay,perWeek);
		}
		public double refund() {
			return charge1.refund(hours1,days1,weeks1) - charge2.refund(hours2,days2,weeks2);
		}
	}
}
