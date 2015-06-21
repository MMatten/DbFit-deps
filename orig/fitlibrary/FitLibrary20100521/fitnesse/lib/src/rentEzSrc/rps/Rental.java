package rps;

import java.util.Iterator;
import rps.Reservation;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;

public class Rental extends Use {
	public Rental(Reservation reservation, Client client, StaffMember staffMember) {
		super(reservation, client, staffMember);
		reservation.hireItems(this, client, staffMember);
		staffMember.addRental(this);
		client.addRental(this);
	}
    public Rental(RentalItemType hireItemType, int count, MyDate startDate, Duration duration, Client client, StaffMember staff) {
        this(new Reservation(hireItemType,count,startDate,duration), client, staff);
    }
	public int returnRental(int count) {
		int returnCount = 0;
		for (Iterator it = getReservation().getHireItems(); it.hasNext()
				&& count > 0;) {
			RentalItem rentalItem = (RentalItem)it.next();
			rentalItem.returnHire();
			it.remove();
			count--;
			returnCount++;
		}
		if (getCount() == 0) {
			getClient().removeRental(this);
			getStaffMember().removeRental(this);
		}
		return returnCount;
	}
	public String toString() {
		return "Rental["+getReservation()+","+getClient()+"]";
	}
}