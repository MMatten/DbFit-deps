package rps.test;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import rps.ItemRestriction;
import rps.Rates;
import rps.RentalItem;
import rps.RentalItemType;
import rps.paymentMethod.Money;
import rps.time.Duration;
import rps.time.MyDate;

public class TestRentalItemType extends TestCase {

	RentalItemType item1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		item1 = new RentalItemType("test", 2, new Rates(new Money(2),
				new Money(4), new Money(8)), new Money(12), 6);
		item1.addRentalItem("Test", new MyDate(new Date(0)), 1);
	}

	public void testCanRentWithClientAge() {
		assertTrue(item1.canRentWithClientAge(8));
		assertTrue(item1.canRentWithClientAge(6));
		assertFalse(item1.canRentWithClientAge(2));
	}

	public void testGetName() {
		assertEquals(item1.getName(), "test");
	}

	/*
	 * Class under test for Money getBond()
	 */
	public void testGetBond() {

		assertEquals(item1.getDeposit(), new Money(12));
	}

	/*
	 * Class under test for Money getBond(int)
	 */
	public void testGetBondint() {
		assertEquals(item1.getBond(4), new Money(48));
	}

	public void testGetRates() {
		assertEquals(item1.getRates(), new Rates(new Money(2), new Money(4),
				new Money(8)));
	}

	public void testGetCount() {
		assertEquals(item1.getCount(), 3);
	}

	public void testHasHiresToReturn() {
		assertEquals(item1.hasHiresToReturn(2), false);

		assertEquals(item1.hasHiresToReturn(1), false);
		assertEquals(item1.hasHiresToReturn(0), true);

	}

	public void testGetHiresToReturn() {
		assertEquals(item1.getHiresToReturn(2).size(), 0);
	}

	public void testCalculateRefundOnReturn() {
		assertEquals(item1.calculateRefundOnReturn(new MyDate(new Date(1)), 2),
				new Money(0.00));
	}
	public void testGetFreeCount() {
		assertEquals(item1.getFreeCount(), 3);
	}
	public void testReserveFor() {
		//
	}
	public void testRemoveForRepair() {
		item1.removeForRepair(2);
		int i = 0;
		for (Iterator<RentalItem> it = item1.getIdentifedHireItems(); it.hasNext(); i++) {
			RentalItem item = it.next();
			if (i < 2)
				assertTrue(item.isBeingRepaired());
			else
				assertFalse(item.isBeingRepaired());
		}
	}
	/*
	 * Class under test for boolean returnFromRepair(MyDate, int)
	 */
	public void testReturnFromRepairMyDateint() {
		//
	}
	public void testForMaintenance() {
		Set<RentalItem> list = new HashSet<RentalItem>();
		item1.forMaintenance(list, new MyDate(new Date(10000000)));
	}
	public void testCheckNoOfItemsInRepair() {
		assertEquals(item1.checkNoOfItemsInRepair(2), false);
		assertEquals(item1.checkNoOfItemsInRepair(0), true);
		item1.removeForRepair(2);
		assertEquals(item1.checkNoOfItemsInRepair(2), true);
	}
	public void testHasIdentifiedItem() {
		assertTrue(item1.hasIdentifiedItem("Test"));
		assertTrue(item1.hasIdentifiedItem("test1"));
		assertTrue(item1.hasIdentifiedItem("test0"));
		assertFalse(item1.hasIdentifiedItem("tes2t"));

	}
	public void testIdentifiedItemIsInMaintenance() {
		assertFalse(item1.identifiedItemIsInMaintenance("Test"));
		item1.removeForRepair(1);
		assertTrue(item1.identifiedItemIsInMaintenance("test1"));
		assertFalse(item1.identifiedItemIsInMaintenance("Test"));
		item1.removeForRepair(3);
		assertTrue(item1.identifiedItemIsInMaintenance("Test"));
	}

	/*
	 * Class under test for void returnFromRepair(MyDate, String)
	 */
	public void testReturnFromRepairMyDateString() {
		int i = 0;
		for (Iterator<RentalItem> it = item1.getIdentifedHireItems(); it.hasNext(); i++) {
			RentalItem item = it.next();
			assertFalse(item.isBeingRepaired());
		}
		item1.returnFromRepair(new MyDate(new Date(1)), "test1");
		i = 0;
		for (Iterator<RentalItem> it = item1.getIdentifedHireItems(); it.hasNext(); i++) {
			RentalItem item = it.next();
			assertFalse(item.isBeingRepaired());
		}

		item1.removeForRepair(1);
		i = 0;
		for (Iterator<RentalItem> it = item1.getIdentifedHireItems(); it.hasNext(); i++) {
			RentalItem item = it.next();
			if (i == 0)
				assertTrue(item.isBeingRepaired());
			else
				assertFalse(item.isBeingRepaired());
		}

		item1.returnFromRepair(new MyDate(new Date(1)), "test1");
		i = 0;
		for (Iterator<RentalItem> it = item1.getIdentifedHireItems(); it.hasNext(); i++) {
			RentalItem item = it.next();
			assertFalse(item.isBeingRepaired());
		}
	}

	/*
	 * Class under test for Money totalRentalCost(Duration)
	 */
	public void testTotalRentalCostDuration() {
		assertEquals(item1.totalRentalCost(new Duration(2, 1, 0)), new Money(
				0.20));
	}

	/*
	 * Class under test for Money totalRentalCost(Duration, int)
	 */
	public void testTotalRentalCostDurationint() {
		assertEquals(item1.totalRentalCost(new Duration(2, 1, 0), 2),
				new Money(0.40));
		assertEquals(item1.totalRentalCost(new Duration(2, 1, 0), 1),
				new Money(0.20));
		assertEquals(item1.totalRentalCost(new Duration(2, 1, 0), 0),
				new Money(0.00));
	}

	/*
	 * Class under test for String toString()
	 */
	public void testToString() {
		assertEquals(item1.toString(), "test");
	}
	public void testCanRentWithSatisfiedRestriction() {
		item1.addItemRestriction(new ItemRestriction(0, "TEST"));
		assertTrue(item1.canRentWithSatisfiedRestriction(new String[] { "0" }));
		item1.addItemRestriction(new ItemRestriction(1, "TEST2"));
		assertFalse(item1.canRentWithSatisfiedRestriction(new String[] { "0" }));
		assertTrue(item1.canRentWithSatisfiedRestriction(new String[] { "0",
				"1" }));
	}
	public void testEquals() {
		RentalItemType item2 = new RentalItemType("test", 2, new Rates(
				new Money(2), new Money(4), new Money(8)), new Money(12), 6);
		RentalItemType item3 = new RentalItemType("testing", 2, new Rates(
				new Money(2), new Money(4), new Money(8)), new Money(12), 6);
		RentalItemType item4 = new RentalItemType("test", 3, new Rates(
				new Money(2), new Money(4), new Money(8)), new Money(12), 6);
		RentalItemType item5 = new RentalItemType("test", 2, new Rates(
				new Money(1), new Money(3), new Money(5)), new Money(12), 6);
		RentalItemType item6 = new RentalItemType("test", 2, new Rates(
				new Money(2), new Money(4), new Money(8)), new Money(10), 6);
		RentalItemType item7 = new RentalItemType("test", 2, new Rates(
				new Money(2), new Money(4), new Money(8)), new Money(12), 10);
		assertEquals(item1, item2);
		assertNotSame(item1, item3);
		assertNotSame(item1, item4);
		assertNotSame(item1, item5);
		assertNotSame(item1, item6);
		assertNotSame(item1, item7);
	}
}
