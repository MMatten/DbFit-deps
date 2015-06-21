package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.person.Client;
import rps.transaction.Transaction;

public class RefundAccountDollarTransactionItem extends TransactionItem {
	private Money accountAmount;
	private Client client;
	
	public RefundAccountDollarTransactionItem(Transaction transaction, Client client, Money accountAmount) {
		super(transaction);
		this.accountAmount = accountAmount;
		this.client = client;
	}

	public Money getTotalCost() {
		return accountAmount;
	}

	public void complete() {
		client.refundToAccount(accountAmount);
	}

}
