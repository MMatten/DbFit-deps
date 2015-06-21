package rps.transactionItems;

import rps.Booking;
import rps.Delivery;
import rps.RentalItemType;
import rps.paymentMethod.Money;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

public class ChangeDeliveryTransactionItem extends ClientTransactionItem {

	private RentalItemType hireItemType;
	private int count;
	private MyDate bookingDate;
	private Duration period;
	private Delivery delivery;
	String newAddress;
	String newZone;

	public ChangeDeliveryTransactionItem(ClientTransaction transaction) {
		super(transaction);
	}

	public ChangeDeliveryTransactionItem(ClientTransaction transaction,
			RentalItemType itemType, int count, MyDate date, Duration duration,
			Delivery delivery, String newZone, String newAddress) {
		this(transaction);
		this.hireItemType = itemType;
		this.count = count;
		this.bookingDate = date;
		this.period = duration;
		this.delivery = delivery;
		this.newAddress = newAddress;
		this.newZone = newZone;
	}

	@Override
	public Money getTotalCost() {
		Money oldCost = getRentEz().getDeliveryRate(delivery.city,
				delivery.zone).getDeliveryCost(
				hireItemType.totalRentalCost(period, count));
		Money newCost = getRentEz().getDeliveryRate(delivery.city, newZone)
				.getDeliveryCost(hireItemType.totalRentalCost(period, count));

		return newCost.minus(oldCost);
	}

	@Override
	public void complete() {
		Booking booking = getClient().getBooking(hireItemType, count,
				bookingDate, bookingDate.plus(period), delivery);
		if (booking != null)
			booking.changeDelivery(newZone, newAddress);
	}

}
