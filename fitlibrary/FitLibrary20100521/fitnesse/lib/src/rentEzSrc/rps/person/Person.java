package rps.person;

import java.util.ArrayList;
import java.util.List;

import rps.Booking;
import rps.Delivery;
import rps.Rental;
import rps.RentalItemType;
import rps.time.MyDate;
import rps.transaction.Transaction;

public class Person {
	protected String name;
	protected String phoneNumber;
	protected StaffMember creator;
	protected List<Rental> rentals = new ArrayList<Rental>();
	protected List<Booking> bookings = new ArrayList<Booking>();
	protected List<Delivery> deliveries = new ArrayList<Delivery>();
	protected Transaction currentTransaction;
	
	public Person() {
		//
	}
	public Person(StaffMember creator, String name, String phoneNumber) {
		this.creator = creator;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	public boolean transactionComplete() {
		if(currentTransaction == null)
			return false;
		currentTransaction = null;
		return true;
	}
	public String getName() {
		return name;
	}
	public String getPhone() {
		return phoneNumber;
	}	
	public void addRental(Rental rental) {
		rentals.add(rental);
	}
    public void addBooking(Booking booking) {
        for(Booking b : bookings) {
            if( b.compareTo(booking)==0 && b.getHireItemType().equals(booking.getHireItemType()) ) {
                b.getReservation().addRentalItems( booking.getReservation() );
//                if(booking.getDelivery() != null)
//                	deliveries.add(booking.getDelivery());
                return;
            }
        }
		bookings.add(booking);
//        if(booking.getDelivery() != null)
//        	deliveries.add(booking.getDelivery());
	}
	public StaffMember getCreator() {
		return creator;
	}
	public boolean startTransaction(Transaction transaction) {
		if(currentTransaction == null) {
			currentTransaction = transaction;
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return name;
	}
	public void removeRental(Rental hire) {
		rentals.remove(hire);
	}	
	public void removeBooking(Booking booking) {
		if(booking.getDelivery() != null) {
			deliveries.remove(booking.getDelivery());
		}
		bookings.remove(booking);
	}	
	public List<Rental> getRentals(){
		return rentals;
	}
	public List<Booking> getBookings(){
		return bookings;
	}
	public List<Booking> getBookings(String city, String zone, String address, MyDate date) {
		List<Booking> out = new ArrayList<Booking>();
		for(Booking booking : bookings) {
			Delivery del = booking.getDelivery();
			if(del.city.equals(city) && del.zone.equals(zone) && del.deliveryAddress.equals(address) && del.date.equals(date.toString()) )
				out.add(booking);
		}
		return out;
	}
	public Booking getBooking(RentalItemType hireItemType, int count, MyDate bookingDate, MyDate dueDate) {
		for (Booking booking : bookings) {
			if (booking.getStartDate().equals(bookingDate) &&
				booking.getEndDate().equals(dueDate) &&
				booking.getHireItemType() == hireItemType) {
				return booking;
			}
		}
		return null;
	}
	public Booking getBooking(RentalItemType hireItemType, int count, MyDate bookingDate, MyDate dueDate, Delivery delivery) {
		for (Booking booking : bookings) {
			if (booking.getStartDate().equals(bookingDate) &&
				booking.getEndDate().equals(dueDate) &&
				booking.getHireItemType() == hireItemType &&
				booking.getDelivery().equals(delivery)) {
				return booking;
			}
		}
		return null;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public void setName(String name) {
		this.name = name;
	}
}
