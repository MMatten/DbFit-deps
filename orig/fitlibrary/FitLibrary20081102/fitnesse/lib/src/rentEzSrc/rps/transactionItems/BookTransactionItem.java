package rps.transactionItems;

import rps.Booking;
import rps.Delivery;
import rps.DeliveryRate;
import rps.RentalItemType;
import rps.Reservation;
import rps.paymentMethod.Money;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

public class BookTransactionItem extends ClientTransactionItem {
	private int count;

	private RentalItemType itemType;
	private MyDate startDate;
	private MyDate dueDate;
	private Duration period;
	private Reservation reservation;
	private DeliveryRate deliveryRate;
	private String address;

	public BookTransactionItem(ClientTransaction transaction, int count, RentalItemType itemType, MyDate startDate, Duration period,DeliveryRate deliveryRate, String address) {
		super(transaction);
		this.count = count;
		this.itemType = itemType;
		this.startDate = startDate;
		this.period = period;
		this.reservation = new Reservation(itemType, count, startDate, period);
		this.deliveryRate = deliveryRate;
		if(address == null)
			this.address = getClient().getAddress();
		else
			this.address = address;
	}

	public BookTransactionItem(ClientTransaction transaction, int count, RentalItemType itemType, MyDate startDate, Duration period) throws Exception {
		this(transaction, count, itemType, startDate, period, null, null);
	}

	public Money getTotalCost() {
		if (itemType instanceof RentalItemType) {
			RentalItemType rentalItemType = (RentalItemType) itemType;
			if (deliveryRate == null) {
				return rentalItemType.totalRentalCost(period, count);
			} else {
				return rentalItemType.totalRentalCost(period, count).plus(
						deliveryRate.getDeliveryCost(rentalItemType
								.totalRentalCost(period, count)));
			}
		}
		return new Money();
	}

	public void complete() {
		if (deliveryRate == null)
			new Booking(reservation, getClient(), getStaffMember());
		else {
			Delivery d = new Delivery(startDate, deliveryRate.getCity(),
					deliveryRate.getZone(), address, itemType.getName(), count);
			new Booking(reservation, getClient(), getStaffMember(), d);
			getClient().addDelivery(d);
		}
	}
}
