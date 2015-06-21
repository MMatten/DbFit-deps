package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.transaction.ClientTransaction;

public class AccountPaymentTransactionItem extends ClientTransactionItem {
	private Money amount;

	public AccountPaymentTransactionItem(ClientTransaction transaction,
	        Money amount) {
		super(transaction);
		this.amount = amount;
	}
	@Override
	public Money getTotalCost() {
		return amount.negate();
	}
	@Override
	public void complete() {
		getClient().payOnAccount(amount);
	}
}