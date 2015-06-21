package rps.transactionItems;

import rps.DeliveryRate;
import rps.paymentMethod.Money;
import rps.transaction.AdminTransaction;
import rps.transaction.Transaction;

public class AddDeliveryRateTransactionItem extends TransactionItem {
	private String city;
	private String zone;
	private double flatrate;
	private double deliveryrate;
	
	protected AddDeliveryRateTransactionItem(Transaction transaction) {
		super(transaction);
	}

	public AddDeliveryRateTransactionItem(AdminTransaction transaction, String city, String zone, double flatrate, double deliveryrate) {
		this(transaction);
		this.city = city;
		this.zone = zone;
		this.flatrate = flatrate;
		this.deliveryrate = deliveryrate;
	}

	@Override
	public Money getTotalCost() {
		return new Money(0);
	}

	@Override
	public void complete() {
		getRentEz().createDeliveryZone(new DeliveryRate(city, zone, flatrate, deliveryrate));
	}

}
