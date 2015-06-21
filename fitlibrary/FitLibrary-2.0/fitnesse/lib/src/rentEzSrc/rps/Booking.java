/*
 * Created on May 12, 2004
 *  
 */
package rps;

import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;

public class Booking extends Use {
	private Client client;
	private Delivery delivery;

	public Booking(Reservation reservation, Client client, StaffMember staffMember, Delivery delivery) {
		super(reservation, client, staffMember);
		this.client = client;
		staffMember.addBooking(this);
		client.addBooking(this);
		this.delivery = delivery;
	}

	public Booking(Reservation reservation, Client client, StaffMember staffMember) {
		this(reservation, client, staffMember, null);
	}
	@Override
	public Client getClient() {
		return client;
	}
	public void accept(MyDate acceptDate) {
		client.removeBooking(this);
		getStaffMember().removeBooking(this);
		int count = getReservation().getCount();
		getReservation().removeAll();
		MyDate startDate;
		if (acceptDate.before(getReservation().getStartDate())) {
			startDate = acceptDate;
		} else {
			startDate = getReservation().getStartDate();
		}
		Reservation reservation = new Reservation(
				getReservation().getHireItemType(), count, startDate, getReservation()
						.getPeriod());
		new Rental(reservation, client, getStaffMember());
	}

	public void cancel() {
		client.removeBooking(this);
		getStaffMember().removeBooking(this);
		getReservation().removeAll();
	}

	public Reservation reserveForExtendedPeriod(Duration newPeriod) {
		Duration extension = newPeriod.subtract(getReservation().getPeriod());
		return new Reservation(getReservation().getHireItemType(),
				getReservation().getCount(), getReservation().getDueDate(),
				extension);
	}

	public void extendInto(Reservation extension) {
		Duration totalPeriod = getReservation().getPeriod().add(
				extension.getPeriod());
		int count = getReservation().getCount();
		getReservation().removeAll();
		extension.removeAll();
		setReservation(new Reservation(
				getReservation().getHireItemType(), count, getReservation()
						.getStartDate(), totalPeriod));
	}

	public void shrinkTo(Duration newPeriod) {
		int count = getReservation().getCount();
		getReservation().removeAll();
		setReservation(new Reservation(
				getReservation().getHireItemType(), count, getReservation()
						.getStartDate(), newPeriod));
	}
	public Delivery getDelivery() {
		return delivery;
	}

	public void cancelDelivery() {
		delivery = null;
	}

	public void changeDelivery(String newZone, String newAddress) {
		if(delivery != null) {
			delivery.zone = newZone;
			delivery.deliveryAddress = newAddress;
		}
	}
	public String getItem() {
		return getHireItemType().getName();
	}
}