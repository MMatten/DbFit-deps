/*
 * @author Rick Mugridge 4/10/2004
 * Released under the terms of the GNU General Public License version 2 or later.
 */
package rps;

import java.util.Enumeration;
import java.util.Hashtable;

import rps.person.Client;
import rps.person.StaffMember;
import rps.time.MyDate;

/**
 * 
 */
/**
 * 
 * Contains all information relevant to one RentalItem:
 * - Name of the unique item
 * - If it is currently hired out:
 *  - The date of the start of the hire
 *  - The due date for the hire
 * - All bookings
 * HireItem is responsible for determining whether it can be hired
 * or booked for a certain time period
 *
 */
public class RentalItem {
	private final RentalItemType rentalItemType;
	private boolean beingRepaired = false;
	/**
	 * Becomes non-null when this HireItem is being hired out
	 */
	private Rental currentRental = null;
	private final String identifier;
	private Hashtable<MyDate,Reservation> reservations = new Hashtable<MyDate,Reservation>();
	private MyDate lastMaintained = null;
	private double monthsBetweenMaintenance;
	
	public RentalItem(RentalItemType hireItemType, String name) {
		this.rentalItemType = hireItemType;
		this.identifier = name;
	}
	public RentalItem(RentalItemType hireItemType, String name, MyDate lastMaintained, double monthsBetweenMaintenance) {
		this.rentalItemType = hireItemType;
		this.identifier = name;
		this.lastMaintained = lastMaintained;
		this.monthsBetweenMaintenance = monthsBetweenMaintenance;
	}
	public void removeReservation(Reservation reservation) {
		reservations.remove(reservation.getStartDate());
	}
	public void doHire(Rental hire, Reservation reservation, @SuppressWarnings("unused") Client client, @SuppressWarnings("unused") StaffMember staffMember) {
		reservations.remove(reservation.getStartDate());
		currentRental = hire;
	}
	public String getHireItemTypeName() {
		return rentalItemType.getName();
	}
	/**
	 * Returns true if this HireItem can be hired or booked for the given period
	 * Adds a pending hire/booking if this item can be hired/booked
	 */
	public boolean reserveFor(Reservation reservation, MyDate startDate, MyDate dueDate) {
		// Later this method will check for booking clashes in the
		// same way it checks pending hires

		if(currentRental != null && startDate.before(currentRental.getEndDate())) {
			return false;
		}
		for(Enumeration<MyDate> pendingHireEnum = reservations.keys(); pendingHireEnum.hasMoreElements(); ) {

			MyDate pendingHireStartDate = pendingHireEnum.nextElement();
			MyDate pendingHireDueDate = reservations.get(pendingHireStartDate).getDueDate();
			// Using !after instead of before, and !before instead of after to
			// allow hires to start exactly where the last one left off
			if(!startDate.after(pendingHireDueDate) && !startDate.before(pendingHireStartDate)) {
				return false;
			}
			if(!dueDate.after(pendingHireDueDate) && !dueDate.before(pendingHireStartDate)) {
				return false;
			}
		}
		reservations.put(startDate, reservation);
		return true;
	}
	
	public void unReserve(MyDate startDate) {
		reservations.remove(startDate);
	}
	
	public Rental getCurrentRental() {
		return currentRental;
	}
	public String getIdentifier() {
		return identifier;
	}
	public boolean hasHireToReturn() {
		return currentRental != null;
	}
	/**
	 * Only to be called from Hire
	 *
	 */
	public void returnHire() {
		currentRental = null;
	}
	public void repair() {
		beingRepaired = true;
	}
	public void completeRepair(MyDate date) {
		beingRepaired = false;
		lastMaintained = date;
	}
	public boolean isFree() {
		return currentRental == null && !beingRepaired;
	}

	public boolean needsMaintenance(MyDate date) {
		if (lastMaintained == null){
			return false;
		}
		MyDate time = lastMaintained.afterMonths((int)monthsBetweenMaintenance);
		if (!date.before(time)){
			beingRepaired = true;
			return true;
		}
		return false;
	}

	public MyDate getLastMaintained() {
		return lastMaintained;
	}

	public boolean hasMaintenanceDate() {
		return lastMaintained != null;
	}
	public boolean isBeingRepaired() {
		return beingRepaired;
	}
	public MyDate getStartDate() {
		return getCurrentRental().getStartDate();
	}
	public MyDate getEndDate() {
		return getCurrentRental().getEndDate();
	}
}
