package rps;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import rps.paymentMethod.Money;
import rps.time.Duration;
import rps.time.MyDate;

public class RentalItemType implements ItemType {
	private static final int MIN_AGE_NO_LIMIT = 0;
	public static final int CLIENT_AUTO_PASS_MIN_AGE = Integer.MAX_VALUE;
	private String name;
	private Rates rates;
	private Money deposit;
	private int minAge;
	private Hashtable<String, RentalItem> hireItems = new Hashtable<String, RentalItem>();
	/** Contains all rental restrictions this item has
	 */
	private Hashtable<String, ItemRestriction> itemRestrictions = new Hashtable<String, ItemRestriction>();

	public RentalItemType() {
		//
	}
	public RentalItemType(String name, int initialCount, Rates rates, Money bond) {
		this(name, initialCount, rates, bond, MIN_AGE_NO_LIMIT);
	}
	public RentalItemType(String name, int initialCount, Rates rates, Money bond, int minAge) {
		if (name.equals(""))
				throw new RuntimeException("You must supply a name");
		this.name = name;
		this.rates = rates;
		this.deposit = bond;
		this.minAge = minAge;
		setCount(initialCount);
	}
	public boolean canRentWithClientAge(int ageSpecified) {
		if (ageSpecified < this.minAge)
			return false;
		return true;
	}
	private void addRentalItem(String itemName) {
		RentalItem hireItem = new RentalItem(this, itemName);
		hireItems.put(itemName, hireItem);
	}
	public void addRentalItem(String name2, MyDate lastMaintainedDate,
			double monthsBtwMaintenance) {
		RentalItem hireItem = new RentalItem(this, name2, lastMaintainedDate,
				monthsBtwMaintenance);
		hireItems.put(name2, hireItem);
	}
	public String getName() {
		return name;
	}
	public Money getDeposit() {
		return deposit;
	}
	public Money getBond(int count) {
		return getDeposit().times(count);
	}
	public Rates getRates() {
		return rates;
	}
	public int getCount() {
		return hireItems.size();
	}
	public boolean hasHiresToReturn(int countInitial) {
		int count = countInitial;
		for (Iterator<RentalItem> it = hireItems.values().iterator(); it.hasNext()
				&& count > 0;) {
			if (it.next().hasHireToReturn())
				count--;
		}
		return count == 0;
	}
	public List<RentalItem> getHiresToReturn(int count) {
		List<RentalItem> hiresToReturn = new ArrayList<RentalItem>();
		for (int i = 0; i < count; i++) {
			RentalItem earliest = null;
			for (Iterator<RentalItem> it = hireItems.values().iterator(); it.hasNext();) {
				RentalItem hireItem = it.next();
				if (!hiresToReturn.contains(hireItem)
						&& hireItem.hasHireToReturn()) {
					if (earliest == null
							|| earliest.getCurrentRental().getEndDate().after(
									hireItem.getCurrentRental().getEndDate())) {
						earliest = hireItem;
					}
				}
			}
			if (earliest != null)
				hiresToReturn.add(earliest);
			else
				break;
		}
		return hiresToReturn;
	}
	public Money calculateRefundOnReturn(MyDate returnDate, int countInitial) {
		int count = countInitial;
		Money refundAmount = new Money();
		List<RentalItem> hiresToReturn = getHiresToReturn(count);
		for (RentalItem rentalItem : hiresToReturn) {
			if (count == 0)
				break;
			MyDate startDate = rentalItem.getStartDate();
			Duration hiredDuration = startDate.durationTo(rentalItem
					.getEndDate());
			Duration actualDuration = startDate.durationTo(returnDate);
			Money hiredCost = rates.costForPeriod(hiredDuration);
			Money actualCost = rates.costForPeriod(actualDuration);
			refundAmount = refundAmount.plus(hiredCost.minus(actualCost));
			count--;
		}
		return refundAmount;
	}
	public int getFreeCount() {
		int free = 0;
		for (RentalItem hireItem : hireItems.values())
			if (hireItem.isFree())
				free++;
		return free;
	}
	public List<RentalItem> reserveFor(Reservation reservation, int count,
			MyDate startDate, MyDate dueDate) {
		List<RentalItem> pendingHireItems = new ArrayList<RentalItem>();
		for (Iterator<RentalItem> it = hireItems.values().iterator(); it.hasNext()
				&& pendingHireItems.size() < count;) {
			RentalItem item = it.next();
			if (item.reserveFor(reservation, startDate, dueDate))
				pendingHireItems.add(item);
		}
		if (pendingHireItems.size() != count) {
			for (RentalItem item : pendingHireItems)
				item.unReserve(startDate);
			throw new RuntimeException("Cannot reserve " + count + " "
					+ getName() + "'s from " + startDate + " to " + dueDate);
		}
		return pendingHireItems;
	}
	public boolean removeForRepair(int countInitial) {
		int count = countInitial;
		for (Iterator<RentalItem> it = hireItems.values().iterator(); it.hasNext()
				&& count > 0; count--) {
			it.next().repair();
		}
		if (count != 0) {
			return false;
		}
		return true;
	}
	public boolean returnFromRepair(MyDate date, int countInitial) {
		int count = countInitial;
		for (Iterator<RentalItem> it = hireItems.values().iterator(); it.hasNext()
				&& count > 0;) {
			RentalItem hireItem = it.next();
			if (hireItem.isBeingRepaired()) {
				hireItem.completeRepair(date);
				count--;
			}
		}
		return count == 0;
	}
	public void forMaintenance(Set<RentalItem> itemsForRepair, MyDate date) {
		for (RentalItem hireItem : hireItems.values())
			if (hireItem.needsMaintenance(date))
				itemsForRepair.add(hireItem);
	}
	public boolean checkNoOfItemsInRepair(int count) {
		int no = 0;
		for (Iterator<RentalItem> it = hireItems.values().iterator(); it.hasNext();) {
			if (it.next().isBeingRepaired())
				no++;
		}
		return no >= count;
	}
	public boolean hasIdentifiedItem(String id) {
		return hireItems.containsKey(id);
	}
	public Iterator<RentalItem> getIdentifedHireItems() {
		return hireItems.values().iterator();
	}
	public boolean identifiedItemIsInMaintenance(String itemIdentifier) {
		RentalItem item = hireItems.get(itemIdentifier);
		return item != null && item.isBeingRepaired();
	}
	public void returnFromRepair(MyDate date, String itemIdentifier) {
		RentalItem item = hireItems.get(itemIdentifier);
		item.completeRepair(date);
	}
	public Money totalRentalCost(Duration period) {
		return getRates().costForPeriod(period).plus(getDeposit());
	}
	public Money totalRentalCost(Duration period, int count) {
		return getRates().costForPeriod(period).plus(getDeposit()).times(count);
	}
	@Override
	public String toString() {
		return name;
	}
	public void addItemRestriction(ItemRestriction restrict) {
		if (itemRestrictions.containsKey(restrict.getID())) {
			throw new RuntimeException("ItemRestriction ID " + restrict.getID()
					+ " already exists in item " + this.getName());
		}
		itemRestrictions.put(restrict.getID(), restrict);
	}
	@SuppressWarnings("unchecked")
	public boolean canRentWithSatisfiedRestriction(String[] restrictIDSatisfied) {
		Hashtable<String, ItemRestriction> temp = (Hashtable<String, ItemRestriction>) itemRestrictions.clone();
		for (int i = 0; i < restrictIDSatisfied.length; i++) {
			temp.remove(restrictIDSatisfied[i]);
		}
		return temp.isEmpty();
	}
	@Override
	public boolean equals(Object other) {
		if (other instanceof RentalItemType) {
			RentalItemType otherRentalItemType = (RentalItemType) other;
			return deposit.equals(otherRentalItemType.deposit)
					&& name.equals(otherRentalItemType.name)
					&& rates.equals(otherRentalItemType.rates)
					&& minAge == otherRentalItemType.minAge;
		}
		return false;
	}
	public void changeDetails(Rates rates2, Money bond) {
		this.rates = rates2;
		this.deposit = bond;
	}
	public void setCount(int initialCount) {
		for (int count = 0; count < initialCount; count++)
			addRentalItem(name + count);
	}
	public int getMinAge() {
		return minAge;
	}
	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}
	public void setDeposit(Money bond) {
		this.deposit = bond;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setRates(Rates rates) {
		this.rates = rates;
	}
    public Money getBond() {
        return getDeposit();
    }
    public int getFreeHash() {
        return getFreeCount();
    }
    public Money getHourlyRate() {
        return getRates().getHourly();
    }
    public Money getDailyRate() {
        return getRates().getDaily();
    }
    public Money getWeeklyRate() {
        return getRates().getWeekly();
    }

}
