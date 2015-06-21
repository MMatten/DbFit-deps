package rps.transactionItems;

import rps.Booking;
import rps.RentalItemType;
import rps.paymentMethod.Money;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

public class AcceptBookingTransactionItem extends ClientTransactionItem {
	private RentalItemType hireItemType;
	private int count;
	private MyDate bookingDate;
	private Duration period;
	private Booking booking;

	public AcceptBookingTransactionItem(ClientTransaction transaction,
			RentalItemType hireItemType, int count, MyDate bookingDate, Duration period) {
		super(transaction);
		this.hireItemType = hireItemType;
		this.count = count;
		this.bookingDate = bookingDate;
		this.period = period;
		booking = getClient().getBooking(hireItemType, count,
				bookingDate, period.dateAfter(bookingDate));
		if (booking == null)
		    throw new RuntimeException("No such booking");
	}
	public Money getTotalCost() {
		return new Money();
	}
	public void complete() {
		booking.accept(getStartDate());
	}
}