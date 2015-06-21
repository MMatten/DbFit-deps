package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.transaction.Transaction;

public class CreditCardPaymentTransactionItem extends TransactionItem {
    private Money amount;
    
    public CreditCardPaymentTransactionItem(Transaction transaction, Money amount) {
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
