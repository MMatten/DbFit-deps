package rps.transactionItems;

import rps.Booking;
import rps.RentalItemType;
import rps.Reservation;
import rps.paymentMethod.Money;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

public class ChangePeriodOfBookingTransactionItem extends ClientTransactionItem {
	private RentalItemType hireItemType;
	private int count;
	private MyDate bookingDate;
	private Duration originalPeriod;
	private Duration newPeriod;
	private Booking booking;
	private Reservation reservation;

	public ChangePeriodOfBookingTransactionItem(ClientTransaction transaction,
			RentalItemType hireItemType, int count, MyDate bookingDate,
			Duration period, Duration newPeriod) {
		super(transaction);
		this.hireItemType = hireItemType;
		this.count = count;
		this.bookingDate = bookingDate;
		this.originalPeriod = period;
		this.newPeriod = newPeriod;
		booking = getClient().getBooking(hireItemType, count,
				bookingDate, originalPeriod.dateAfter(bookingDate));
		if (originalPeriod.lessThan(newPeriod)) {
			reservation = booking.reserveForExtendedPeriod(newPeriod);
		}
	}
	public Money getTotalCost() {
		return hireItemType.totalRentalCost(newPeriod.subtract(originalPeriod),count);
	}
	public void complete() {
		if (originalPeriod.lessThan(newPeriod)) {
			booking.extendInto(reservation);
		} else {
			booking.shrinkTo(newPeriod);
		}
	}
}