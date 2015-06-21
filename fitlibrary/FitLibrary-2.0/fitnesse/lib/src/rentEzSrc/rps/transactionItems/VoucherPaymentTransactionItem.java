package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.paymentMethod.Voucher;
import rps.person.Client;
import rps.transaction.Transaction;

public class VoucherPaymentTransactionItem extends TransactionItem {

    private Voucher amount;
    @SuppressWarnings("unused")
	private Client client;
    private Money payment;

    public VoucherPaymentTransactionItem(Transaction transaction, Voucher amount, Money payment, Client client) {
        super(transaction);
        this.amount = amount;
        this.client = client;
        this.payment = payment;
    }
    @Override
	public Money getTotalCost() {
        return new Money(Math.min(amount.getValueInDouble(), payment.getValueInDouble())).negate();
    }
    @Override
	public void complete() {
    	//
    }

}
