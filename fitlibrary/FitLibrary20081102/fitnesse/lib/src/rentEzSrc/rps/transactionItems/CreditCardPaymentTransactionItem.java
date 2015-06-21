package rps.transactionItems;

import rps.paymentMethod.CreditCard;
import rps.paymentMethod.Money;
import rps.transaction.Transaction;

public class CreditCardPaymentTransactionItem extends TransactionItem {
    private CreditCard card;
    private Money amount;
    
    public CreditCardPaymentTransactionItem(Transaction transaction, Money amount, CreditCard card) {
        super(transaction);
        this.card = card;
        this.amount = amount;
    }

    public Money getTotalCost() {
        return amount.negate();
    }

    public void complete() {

    }

}
