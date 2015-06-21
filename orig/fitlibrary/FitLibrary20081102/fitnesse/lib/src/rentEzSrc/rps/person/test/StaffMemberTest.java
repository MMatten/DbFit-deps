package rps.person.test;

import java.util.Date;

import junit.framework.TestCase;
import rps.Booking;
import rps.Rates;
import rps.Rental;
import rps.RentalItemType;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.Transaction;

public class StaffMemberTest extends TestCase {
	Client person;
	StaffMember staff;
	Rates rates;
	RentalItemType itemType;
	Rental rant;
	Booking booking;
    Transaction transaction; 
	
	protected void setUp() throws Exception {
		super.setUp();
		staff = new StaffMember(null, "Admin", "1223-4565", 0.12, 0.14);
		person = new Client(staff, "Customer","123" );	
		rates = new Rates(new Money(0.05), new Money(0.23), new Money(0.36));
		itemType = new RentalItemType("Truck", 3, rates, new Money(0));
	}

	public void testTransactionComplete() {
		assertFalse(staff.transactionComplete());
	}

	public void testStartTransaction() {

		transaction = new Transaction(null, new MyDate(new Date(1)), staff);
		assertTrue(staff.startTransaction(transaction));
		
	}

	public void testResumeClientTransaction() {
		
	}

	public void testResumeAdminTransaction() {
	}

	public void testGetDiscountRate() {
		assertEquals(staff.getDiscountRate(), 0.14);
	}

	public void testGetCommissionEarnings() {
		assertEquals(staff.getCommissionEarnings(), new Money(0.0));
	}

	/*
	 * Class under test for boolean equals(Object)
	 */
	public void testEqualsObject() {
		assertTrue(staff.equals(staff));
	}


	public void testGetName() {
		assertEquals(staff.getName(), "Admin");
	}

	public void testGetPhone() {
		assertEquals(staff.getPhone(), "1223-4565");
	}

	public void testAddRental() {
		assertEquals(staff.getRentals().size(), 0);
		staff.addRental(new Rental(itemType, 2, new MyDate(new Date(2)), new Duration(0, 1, 0), person, staff));
		assertEquals(staff.getRentals().size(), 2);
	}

	public void testAddBooking() {
		//assertEquals(staff.getBookings().size(), 0);
		///staff.addBooking(new Booking(itemType, 0, ), person, staff));
		//assertEquals(staff.getBookings().size(), 1);
	}

	public void testGetCreator() {
		assertEquals(staff.getCreator(), null);
	}

	/*
	 * Class under test for String toString()
	 */
	public void testToString() {
		assertEquals(staff.toString(), "Admin");
	}

	public void testRemoveRental() {
	}

	public void testRemoveBooking() {
	}

	public void testGetRentals() {
	}

	public void testGetBookings() {
	}

	public void testGetBooking() {
	}

}
