package rps;

import rps.Reservation;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.MyDate;

@SuppressWarnings("unchecked")
public abstract class Use implements Comparable {
	private final Client client;
	private final StaffMember staffMember;
	private Reservation reservation;
	
	protected Use(Reservation reservation, Client client, StaffMember staffMember) {
		this.reservation = reservation;
		this.client = client;
		this.staffMember = staffMember;
	}
	public int getCount() {
 		return reservation.getCount();
	}
	public Client getClient() {
		return client;
	}
	public StaffMember getStaffMember() {
		return staffMember;
	}
	public MyDate getStartDate() {
		return reservation.getStartDate();
	}
	public MyDate getEndDate() {
		return reservation.getDueDate();
	}
	public RentalItemType getHireItemType() {
		return reservation.getHireItemType();
	}
    public String getRentalItem() {
       return getHireItemType().getName();
    }
	public Reservation getReservation() {
		return reservation;
	}
	protected void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
	public int compareTo(Object obj) {
		Use other = (Use)obj;
		int compareToStart = getStartDate().compareTo(other.getStartDate());
		if (compareToStart == 0)
			return getEndDate().compareTo(other.getEndDate());
		return compareToStart;
	}
}
