package rps.transactionItems;

import rps.Booking;
import rps.RentalItemType;
import rps.paymentMethod.Money;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

public class CancelBookingTransactionitem extends ClientTransactionItem {

	private RentalItemType hireItemType;
	private int count;
	@SuppressWarnings("unused")
	private MyDate bookingDate;
	private Duration period;
	private Booking booking;

	public CancelBookingTransactionitem(ClientTransaction transaction,
			RentalItemType hireItemType, int count, MyDate bookingDate,
			Duration period) {
		super(transaction);
		this.hireItemType = hireItemType;
		this.count = count;
		this.bookingDate = bookingDate;
		this.period = period;
		booking = getClient().getBooking(hireItemType, count, bookingDate,
				period.dateAfter(bookingDate));
	}

	@Override
	public Money getTotalCost() {
		if (booking.getDelivery() == null)
			return hireItemType.totalRentalCost(period, -count);
		return hireItemType.totalRentalCost(period, -count).minus(
				getRentEz().getDeliveryRate(booking.getDelivery().city,
						booking.getDelivery().zone).getDeliveryCost(
								hireItemType.totalRentalCost(period, count)));
	}

	@Override
	public void complete() {
		booking.cancel();
	}
}