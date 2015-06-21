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

	@Override
	public Money getTotalCost() {
		return accountAmount;
	}

	@Override
	public void complete() {
		client.refundToAccount(accountAmount);
	}

}
