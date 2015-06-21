package rps.person.test;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import rps.Booking;
import rps.Rates;
import rps.RentalItemType;
import rps.Reservation;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;

public class TestAddBooking extends TestCase {
    Client client;

    @Override
	protected void setUp() throws Exception {
        super.setUp();
        Money m1 = new Money(0.05);
        Money m2 = new Money(0.45);
        Money m3 = new Money(2.00);
        RentalItemType cup = new RentalItemType("cup",10,new Rates(m1,m2,m3),new Money(),0);
        RentalItemType balloon = new RentalItemType("balloon",5,new Rates(m1,m2,m3),new Money(),0);

        StaffMember staff = new StaffMember(null,"Bob", "23423423");
        client = new Client(staff,"john", "0223432342");
        MyDate date = new MyDate(new Date(1));
        Duration duration = new Duration(0,1,0);

        client.addBooking(new Booking(new Reservation(cup,2,date,duration), client, staff));
        client.addBooking(new Booking(new Reservation(balloon,2,date,duration), client, staff));
        client.addBooking(new Booking(new Reservation(balloon,1,date,duration), client, staff));
        client.addBooking(new Booking(new Reservation(cup,4,date,duration), client, staff));
    }

    public void testAddBooking() {
        List<Booking> bookings = client.getBookings();
        assertEquals(2, bookings.size());
        Booking b = bookings.get(0);
        assertEquals( b.getCount(), 6 );
        assertEquals( b.getHireItemType().getName(), "cup" );
        b = bookings.get(1);
        assertEquals( b.getCount(), 3 );
        assertEquals( b.getHireItemType().getName(), "balloon" );
    }

}
