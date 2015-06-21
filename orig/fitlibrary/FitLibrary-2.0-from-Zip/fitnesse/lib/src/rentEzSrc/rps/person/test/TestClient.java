package rps.person.test;

import java.util.Date;

import junit.framework.TestCase;
import rps.Booking;
import rps.Rates;
import rps.RentEz;
import rps.Rental;
import rps.RentalItemType;
import rps.Reservation;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.Transaction;

public class TestClient extends TestCase {
	Client person;
	StaffMember staff;
	Rates rates;
	RentalItemType itemType;
	Rental rant;
	Booking booking;
    Transaction transaction; 
	
	@Override
	public void setUp() {
		staff = new StaffMember(null, "Admin", "");
		person = new Client(staff, "Customer","123" );	
		rates = new Rates(new Money(0.05), new Money(0.23), new Money(0.36));
		itemType = new RentalItemType("Truck", 3, rates, new Money(0));
	}
	public void testGetInfo() {
		assertEquals("Customer", person.getName());
		assertEquals("123", person.getPhone());
		assertEquals(staff, person.getCreator());
	}
	public void testRental(){
		assertEquals(0, person.getRentals().size()); //Rantal added to Client when rant is created
		rant = new Rental(itemType, 3, new MyDate(), new Duration(3,3,3), person, staff);
		assertEquals(1, person.getRentals().size()); 
		person.addRental(rant);
		assertEquals(2, person.getRentals().size());
		person.removeRental(rant);
		assertEquals(1, person.getRentals().size());
	
	}
	@SuppressWarnings("deprecation")
	public void testBooking(){
		assertEquals(0, person.getBookings().size());
		booking = new Booking(new Reservation(itemType, 1, new MyDate(new Date("2023/08/18 11:04")), new Duration(1,0,0)), person, staff);
		assertEquals(1, person.getBookings().size()); 
		person.addBooking(booking);
		assertEquals(1, person.getBookings().size());
		person.removeBooking(booking);
		assertEquals(0, person.getBookings().size());		
	}
	@SuppressWarnings("deprecation")
	public void testTransaction(){
		transaction = new Transaction(new RentEz(), new MyDate(new Date("2023/08/18 11:04")),staff);
		assertFalse(person.transactionComplete());
		assertTrue(person.startTransaction(transaction));
		assertFalse(person.startTransaction(transaction));
		assertTrue(person.transactionComplete());
		assertFalse(person.transactionComplete());		
	}
	public void testReturnRental(){
		rant = new Rental(itemType, 3, new MyDate(), new Duration(3,3,3), person, staff);
		person.returnHires(itemType, 2);
		assertEquals(1, person.getRentals().get(0).getCount());
		person.returnHires(itemType, 2);
		assertEquals(0, person.getRentals().size());
	}
	public void testPayOnAccount(){
		person.payOnAccount(new Money(500));
		assertEquals(new Money(500), person.getAmountOwing());
	}
}
