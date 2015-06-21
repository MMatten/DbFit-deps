package rps.transactionItems;

import rps.paymentMethod.BonusPoints;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.transaction.Transaction;

public class BonusPointPaymentTransactionItem extends TransactionItem {
	private BonusPoints amount;
	private Client client;

	public BonusPointPaymentTransactionItem(Transaction transaction, BonusPoints amount, Client client) {
		super(transaction);
		this.amount = amount;
		this.client = client;
	}
	public Money getTotalCost() {
		return new Money(amount.getValueInDouble()).negate();
	}
	public void complete() {
		client.reduceBonusPoints(amount);
	}
}
