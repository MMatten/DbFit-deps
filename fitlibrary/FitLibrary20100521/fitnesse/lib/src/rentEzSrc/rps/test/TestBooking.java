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

public class TestBooking extends TestCase {

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

	public void testGetClient() {
		assertEquals(booking.getClient(), person);
	}


	public void testAccept() {
		assertEquals(reservation.getCount(), 2);
		assertEquals(person.getRentals().size(), 0);
		booking.accept(new MyDate(new Date(3)));
		assertEquals(person.getBookings().indexOf(booking), -1);
		assertEquals(staff.getBookings().indexOf(booking), -1);
		assertEquals(reservation.getCount(), 0);
		assertEquals(person.getRentals().size(), 1);
		Rental rental = (Rental)person.getRentals().get(0);
		assertEquals(rental.getReservation().getStartDate(),new MyDate(new Date(2)));
	}
	
	public void testAccept2() {
		assertEquals(reservation.getCount(), 2);
		assertEquals(person.getRentals().size(), 0);
		booking.accept(new MyDate(new Date(1)));
		assertEquals(person.getBookings().indexOf(booking), -1);
		assertEquals(staff.getBookings().indexOf(booking), -1);
		assertEquals(reservation.getCount(), 0);
		assertEquals(person.getRentals().size(), 1);
		Rental rental = (Rental)person.getRentals().get(0);
		assertEquals(rental.getReservation().getStartDate(),new MyDate(new Date(1)));
	}
	

	public void testCancel() {

		assertEquals(person.getBookings().indexOf(booking), 0);
		assertEquals(staff.getBookings().indexOf(booking), 0);
		assertEquals(reservation.getCount(), 2);
		booking.cancel();
		assertEquals(person.getBookings().indexOf(booking), -1);
		assertEquals(staff.getBookings().indexOf(booking), -1);
		assertEquals(reservation.getCount(), 0);
	}

	public void testReserveForExtendedPeriod() {
		assertEquals(booking.reserveForExtendedPeriod(new Duration(0, 2, 0)).getPeriod(), new Duration(0, 1, 0));
		assertEquals(booking.reserveForExtendedPeriod(new Duration(0, 1, 0)).getPeriod(), new Duration(0, 0, 0));
	}

	public void testExtendInto() {
		booking.extendInto(new Reservation(cup, 2, date, new Duration(0, 2, 0)));
		assertEquals(booking.getReservation().getPeriod(), new Duration(0, 3, 0));
			
	}

	public void testShrinkTo() {
		booking.shrinkTo(new Duration(0, 2, 0));
		assertEquals(booking.getReservation().getPeriod(), new Duration(0, 2, 0));
		booking.shrinkTo(new Duration(0, 10, 0));
		assertEquals(booking.getReservation().getPeriod(), new Duration(0, 10, 0));
		booking.shrinkTo(new Duration(0, 0, 0));
		assertEquals(booking.getReservation().getPeriod(), new Duration(0, 0, 0));

	}

}
