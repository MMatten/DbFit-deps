package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.transaction.AdminTransaction;
import rps.transaction.Transaction;

public class DeleteDeliveryRateTransactionItem extends TransactionItem {
	private String city;
	private String zone;

	protected DeleteDeliveryRateTransactionItem(Transaction transaction) {
		super(transaction);
	}

	public DeleteDeliveryRateTransactionItem(AdminTransaction transaction, String city, String zone) {
		this(transaction);
		this.city = city;
		this.zone = zone;
	}

	@Override
	public Money getTotalCost() {
		return new Money(0);
	}

	@Override
	public void complete() {
		getRentEz().removeDeliveryRate(city, zone);
	}

}
