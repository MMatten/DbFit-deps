package rps.transactionItems;

import rps.RentEz;
import rps.paymentMethod.Money;
import rps.time.MyDate;
import rps.transaction.Transaction;

/**
 * Contains all information relevant to a single line of a transaction Will be
 * extended by other classes for each type of transaction item (e.g.
 * HireTransactionItem, ReturnTransactionItem, CashPaymentTransactionItem)
 *  
 */
public abstract class TransactionItem {
	private Transaction transaction;

	protected TransactionItem(Transaction transaction) {
		this.transaction = transaction;
	}
	protected Transaction getTransaction() {
		return transaction;
	}
	protected RentEz getRentEz() {
	    return transaction.getRentEz();
	}
    protected MyDate getStartDate() {
        return transaction.getStartDate();
    }

	/**
	 * Gets the total cost for this line. Negative totals indicate a refund, or
	 * payment. e.g. RentalTransactionItem will return a +ve total
	 * ReturnTransactionItem may return a -ve total CashPaymentTransactionItem
	 * will return a -ve total CashRefundTransactionItem will return a +ve total
	 * 
	 * This means that all the totals for all the TransactionItems in a
	 * Transaction can be added together to get 0 to complete a transaction
	 * 
	 */
	public abstract Money getTotalCost();

	/**
	 * Completes this TransactionItem Should ensure that the system is updated to
	 * reflect the Item e.g. HireTransactionItem will actually do the Hire
	 * ReturnItemsTransactionItem will return the Hire
	 *  
	 */
	public abstract void complete();
}