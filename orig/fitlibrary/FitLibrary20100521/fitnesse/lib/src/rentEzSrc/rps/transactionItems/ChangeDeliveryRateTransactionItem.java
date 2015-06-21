package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.transaction.AdminTransaction;
import rps.transaction.Transaction;

public class ChangeDeliveryRateTransactionItem extends TransactionItem {
	private String city;
	private String zone;
	private double flatrate;
	private double deliveryrate;
	private double newFlatrate;

	protected ChangeDeliveryRateTransactionItem(Transaction transaction) {
		super(transaction);
	}

	public ChangeDeliveryRateTransactionItem(AdminTransaction transaction, String city, String zone, double flatrate, double deliveryrate, double newFlatrate) {
		this(transaction);
		this.city = city;
		this.zone = zone;
		this.flatrate = flatrate;
		this.deliveryrate = deliveryrate;
		this.newFlatrate = newFlatrate;
	}

	@Override
	public Money getTotalCost() {
		return new Money(0);
	}

	@Override
	public void complete() {
		getRentEz().changeDeliveryRate(city, zone, flatrate, deliveryrate, newFlatrate);
	}

}
