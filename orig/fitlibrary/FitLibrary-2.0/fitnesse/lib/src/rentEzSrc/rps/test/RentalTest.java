package rps.test;

import java.util.Date;

import rps.Booking;
import rps.Rates;

import rps.Rental;
import rps.RentalItemType;
import rps.Reservation;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;
import junit.framework.TestCase;

public class RentalTest extends TestCase {
	
	Booking booking;
	Client person;
	RentalItemType cup;
	MyDate date;

	StaffMember staff;

	Reservation reservation;
	Rental rental;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
		rental = new Rental(cup, 2, date, duration, person, staff);
		
	}

	/*
	 * Class under test for String toString()
	 */
	public void testToString() {
		assertEquals(rental.toString(), "Rental[Reservation[cup from 1970/01/01 12:00 for 1 day],Customer]");
	}

	public void testGetCount() {
		assertEquals(rental.getCount(), 2);
	}

	public void testGetClient() {
		assertEquals(rental.getClient(), person);
	}

	public void testGetStaffMember() {

		assertEquals(rental.getStaffMember(), staff);
	}

	public void testGetStartDate() {

		assertEquals(rental.getStartDate(), new MyDate(new Date(2)));
	}

	public void testGetEndDate() {

		assertEquals(rental.getEndDate(), new MyDate(new Date(2)).plus(new Duration(0, 1, 0)));
	}

	public void testGetHireItemType() {


		assertEquals(rental.getHireItemType(), cup);
	}

	public void testGetRentalItem() {

		assertEquals(rental.getRentalItem(), "cup");
	}

	public void testGetReservation() {
		Reservation reservation2 = rental.getReservation();
		assertEquals(reservation2.getCount(), 2);
		assertEquals(reservation2.toString(), "Reservation[cup from 1970/01/01 12:00 for 1 day]");

	}


	public void testCompareTo() {

		assertEquals(rental.compareTo(rental), 0);
	}

}
