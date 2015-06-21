/*
 * @author Rick Mugridge 4/10/2004
 * Released under the terms of the GNU General Public License version 2 or later.
 */
package rps;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;

/**
 * A immutable record of a Reservation of rental items for a period
 */
public class Reservation {
	private final RentalItemType hireItemType;
	private final MyDate startDate;
	private final Duration duration;
	private final Hashtable<String,RentalItem> reservedItems = new Hashtable<String,RentalItem>();
	
	public Reservation(RentalItemType itemType, int count, MyDate startDate, Duration period) {
		this.hireItemType = itemType;
		this.startDate = startDate;
		this.duration = period;
		MyDate dueDate = period.dateAfter(startDate);
		List<RentalItem> itemsToBeReserved = itemType.reserveFor(this,count,startDate,dueDate);
		for (RentalItem item : itemsToBeReserved)
			reservedItems.put(item.getIdentifier(), item);
	}
    @Override
	public String toString() {
		return "Reservation["+hireItemType+" from "+startDate+ " for "+duration+"]";
	}
	public Duration getPeriod() {
		return duration;
	}
	public int getCount() {
		return reservedItems.size();
	}
	public MyDate getDueDate() {
		return duration.dateAfter(startDate);
	}
	public MyDate getStartDate() {
		return startDate;
	}
	public RentalItemType getHireItemType() {
		return hireItemType;
	}
	public void hireItems(Rental hire, Client client, StaffMember staffMember) {
		for (Iterator it = reservedItems.values().iterator(); it.hasNext(); ) {
			RentalItem hireItem = (RentalItem)it.next();
			hireItem.doHire(hire, this, client, staffMember);
		}
	}
	public void removeItem(String name) {
		reservedItems.remove(name);
	}
	public Iterator getHireItems() {
		return reservedItems.values().iterator();
	}
	public void removeAll() {
		for (Iterator it = getHireItems(); it.hasNext(); ) {
			RentalItem item = (RentalItem)it.next();
			item.removeReservation(this);
			it.remove();
		}
	}
	public Money totalRentalCost() {
		return hireItemType.totalRentalCost(duration,reservedItems.size());
	}
    public void addRentalItems(Reservation res) {
        reservedItems.putAll(res.reservedItems);
    }
}
