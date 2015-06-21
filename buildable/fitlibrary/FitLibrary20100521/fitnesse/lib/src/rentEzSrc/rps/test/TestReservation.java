package rps.test;

import java.util.Date;
import java.util.Iterator;

import rps.Booking;
import rps.Rates;
import rps.Rental;
import rps.RentalItem;
import rps.RentalItemType;
import rps.Reservation;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;
import junit.framework.TestCase;

public class TestReservation extends TestCase {

	
	Booking booking;
	Client person;
	RentalItemType cup;
	MyDate date;

	StaffMember staff;

	Reservation reservation;
	
	protected void setUp() throws Exception {
		super.setUp();
		Rates rates;
		rps.paymentMethod.Money m1 = new rps.paymentMethod.Money(0.05);
		rps.paymentMethod.Money m2 = new rps.paymentMethod.Money(0.45);
		rps.paymentMethod.Money m3 = new rps.paymentMethod.Money(2.00);
		cup = new RentalItemType("cup", 10,
				new Rates(m1, m2, m3), new rps.paymentMethod.Money(), 0);
		date = new MyDate(new Date(2));
		Duration duration = new Duration(0, 1, 0);

		staff = new StaffMember(null, "Admin", "");
		person = new Client(staff, "Customer", "123");
		reservation = new Reservation(cup, 2, date, duration);
		booking = new Booking(reservation, person, staff);
	}

	/*
	 * Class under test for String toString()
	 */
	public void testToString() {
		assertEquals(reservation.toString(), "Reservation[cup from 1970/01/01 12:00 for 1 day]");
	}

	public void testGetPeriod() {
		assertEquals(reservation.getPeriod(), new Duration(0, 1, 0));
		
	}

	public void testGetCount() {
		assertEquals(reservation.getCount(), 2);
	}

	public void testGetDueDate() {
		assertEquals(reservation.getDueDate(), new MyDate(new Date(2)).plus(new Duration(0, 1, 0)));
	}

	public void testGetStartDate() {
		assertEquals(reservation.getStartDate(), new MyDate(new Date(2)));

	}

	public void testGetHireItemType() {
		assertEquals(reservation.getHireItemType(), cup);
	}

	public void testHireItems() {

		Rental rental = new Rental(cup,1, new MyDate(new Date(2)), new Duration(0, 1, 0), person, staff);
		reservation.hireItems(rental, person, staff);
		
		for (Iterator it = reservation.getHireItems(); it.hasNext(); ) {
			RentalItem hireItem = (RentalItem)it.next();
			assertEquals(hireItem.getCurrentRental(), rental);
		}		
	}

	public void testRemoveItem() {
		assertEquals(reservation.getCount(), 2);
		//Uses the largest numbers
		reservation.removeItem("cup9");
		assertEquals(reservation.getCount(), 1);

	}

	public void testRemoveAll() {
		assertEquals(reservation.getCount(), 2);
		reservation.removeAll();
		assertEquals(reservation.getCount(), 0);		
	}

	public void testTotalRentalCost() {
		assertEquals(reservation.totalRentalCost(), new Money(0.90));
	}

}
