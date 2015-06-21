package rent;
/*
 * @author Rick Mugridge 21/07/2004
 * Released under the terms of the GNU General Public License version 2 or later.
 */

/**
 * 
 */
public class FairChargeWithColumn extends fit.ColumnFixture {
	private double perHour, perDay, perWeek;
	public int hours, days, weeks;

	public FairChargeWithColumn(double perHour, double perDay, double perWeek) {
		this.perHour = perHour;
		this.perDay = perDay;
		this.perWeek = perWeek;
	}
	public int hours() {
		reconstitute();
		return hours;
	}
	public int days() {
		reconstitute();
		return days;
	}
	public int weeks() {
		reconstitute();
		return weeks;
	}
	public double charge() {
		reconstitute();
		return hours * perHour + days * perDay + weeks * perWeek;
	}
	private void reconstitute() {
		if (hours * perHour > perDay) {
			hours = 0;
			days++;
		}
		if (days * perDay > perWeek) {
			days = 0;
			weeks++;
		}
	}
	public double refund(int hours2, int days2, int weeks2) {
		this.hours = hours2;
		this.days = days2;
		this.weeks = weeks2;
		return charge();
	}
}
