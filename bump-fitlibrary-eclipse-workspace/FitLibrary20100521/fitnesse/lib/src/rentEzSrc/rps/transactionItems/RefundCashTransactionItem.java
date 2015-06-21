package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.transaction.Transaction;

public class RefundCashTransactionItem extends TransactionItem {
	private Money cashAmount;

	public RefundCashTransactionItem(Transaction transaction, Money cashAmount) {
		super(transaction);
		this.cashAmount = cashAmount;
	}
	public Money getTotalCost() {
		return cashAmount;
	}
	public void complete() {
	}
}