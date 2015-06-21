package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.transaction.Transaction;

public class CashPaymentTransactionItem extends TransactionItem {
	private Money amount;

	public CashPaymentTransactionItem(Transaction transaction, Money amount) {
		super(transaction);
		this.amount = amount;
	}
	@Override
	public Money getTotalCost() {
		return amount.negate();
	}
	@Override
	public void complete() {
		//
	}
}