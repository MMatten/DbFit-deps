package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.paymentMethod.Voucher;
import rps.person.Client;
import rps.time.MyDate;
import rps.transaction.Transaction;

public class VoucherPaymentTransactionItem extends TransactionItem {

    private Voucher amount;
    private Client client;
    private Money payment;

    public VoucherPaymentTransactionItem(Transaction transaction, Voucher amount, Money payment, Client client, MyDate expiryDate) {
        super(transaction);
        this.amount = amount;
        this.client = client;
        this.payment = payment;
    }
    public VoucherPaymentTransactionItem(Transaction transaction, Voucher amount, Money payment, Client client) {
        this(transaction, amount, payment, client, null);
        // if expiry date is null, voucher never expires
    }
    public Money getTotalCost() {
        return new Money(Math.min(amount.getValueInDouble(), payment.getValueInDouble())).negate();
    }
    public void complete() {
    }

}
