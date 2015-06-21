package rps.test;

import junit.framework.TestCase;
import rps.Rates;
import rps.paymentMethod.Money;
import rps.time.Duration;

public class TestRates extends TestCase {
	private Rates rateOne;
	private Rates rateTwo;
	private Rates rateThree;
	private Rates rateHourCheaper;

	protected void setUp() throws Exception {
		super.setUp();
		rateOne = new Rates(new Money(12), new Money(142), new Money(646));
		rateTwo = new Rates(rateOne);
		rateThree = new Rates(new Money(100), new Money(100), new Money(500));
		rateHourCheaper = new Rates(new Money(50), new Money(1400), new Money(2302030));
		
	}
	
	public void testGetHourly() {

		assertEquals(new Money(12), rateOne.getHourly());
		assertEquals(new Money(12), rateTwo.getHourly());		
	}
	
	public void testGetDaily() {
		assertEquals(new Money(142), rateOne.getDaily());
		assertEquals(new Money(142), rateTwo.getDaily());
	}



	public void testGetWeekly() {
		assertEquals(new Money(646), rateOne.getWeekly());
		assertEquals(new Money(646), rateTwo.getWeekly());		
	}

	public void testCostForPeriod() {
		assertEquals(new Money(38.76), rateTwo.costForPeriod(new Duration(12, 4, 5)));	
		assertEquals(new Money(), rateTwo.costForPeriod(new Duration(0, 0, 0)));
		assertEquals(new Money(0.72), rateTwo.costForPeriod(new Duration(6, 0, 0)));
		assertEquals(new Money(6.46), rateTwo.costForPeriod(new Duration(0, 8, 0)));
		assertEquals(new Money(45.22), rateTwo.costForPeriod(new Duration(0, 0, 7)));

		assertEquals(new Money(300), rateThree.costForPeriod(rateThree.fairDuration(new Duration(0, 3, 0))));	

	}

	/*
	 * Class under test for Duration fairDuration(Date, Date)
	 */
	public void testFairDurationDateDate() {
		//assertEquals(new Duration(0, 0, 1), rateTwo.fairDuration(new Date(Calendar.WEEK_OF_YEAR), new Date(Calendar.WEEK_OF_YEAR + 1)));		
		
	}

	/*
	 * Class under test for Duration fairDuration(Duration)
	 */
	public void testFairDurationDuration() {
		assertEquals(new Duration(0, 0, 1), rateTwo.fairDuration(new Duration(0, 10, 0)));	
		assertEquals(new Duration(0, 3, 0), rateThree.fairDuration(new Duration(0, 3, 0)));	
		assertEquals(new Duration(0, 3, 0), rateThree.fairDuration(new Duration(0, 3, 0)));	
		assertEquals(new Duration(96, 0, 0), rateHourCheaper.fairDuration(new Duration(0, 4, 0)));	
	}

}
