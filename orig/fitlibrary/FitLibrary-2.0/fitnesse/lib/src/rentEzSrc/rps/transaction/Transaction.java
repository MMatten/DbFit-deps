package rps.transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import rps.RentEz;
import rps.paymentMethod.Money;
import rps.person.StaffMember;
import rps.time.MyDate;
import rps.transactionItems.CashPaymentTransactionItem;
import rps.transactionItems.ClientTransactionItem;
import rps.transactionItems.RefundCashTransactionItem;
import rps.transactionItems.TransactionItem;

/**
 * Contains all information relevant to a Transaction:
 * - Client that the transaction was for
 * - StaffMember that completed the transaction
 * - Time that the transaction was started at, and completed
 * - All the transaction items in the transaction
 * Transactions will be completable only when the total has come to 0
 *
 */
public class Transaction {
	protected final RentEz rentEz;
	private final MyDate startDate;
	private final StaffMember staffMember;
	/** TransactionItems in the order they were added to the Transaction:
	 */
	private List<TransactionItem> transactionItems = new ArrayList<TransactionItem>();

	public Transaction(RentEz phs, MyDate startDate, StaffMember staffMember) {
		this.rentEz = phs;
		if (!(staffMember.startTransaction(this))){
			throw new RuntimeException("Staff member must not have a pending transaction");
		}
		this.startDate = startDate;
		this.staffMember = staffMember;
	}
	public Money getTotal() {
	    Money total = new Money(0);
		for (TransactionItem transactionItem : transactionItems)
            total = total.plus(transactionItem.getTotalCost());
		return total;
	}
	public RentEz getRentEz() {
		return rentEz;
	}
	public boolean complete() {
		if (getTotal().isZero()) {
			for(TransactionItem item : transactionItems)
				item.complete();
			if (!staffMember.transactionComplete())
				throw new RuntimeException("Staff member incomplete");
			transactionItems.clear();
			return true;
		}
		throw new RuntimeException("Cash outstanding: "+getTotal());
	}
	protected boolean addTransactionItem(TransactionItem item) {
		if (transactionItems.contains(item))
			return false;
		transactionItems.add(item);
		return true;
	}
	
	protected void removeTransactionItem(TransactionItem item) {
		transactionItems.remove(item);
	}	
	protected void removeLastTransactionItem() {
		transactionItems.remove(transactionItems.size() - 1);
	}
	public StaffMember getStaffMember() {
		return staffMember;
	}
	public MyDate getStartDate() {
		return startDate;
	}
	public Iterator<TransactionItem> getTransactionItems() {
		return transactionItems.iterator();
	}
	public boolean abort(){
		transactionItems.clear();
		return true;
	}
	public boolean cancel() {
		return getPaymentTotal().isZero();
	}
	public Money getPaymentTotal() {
	    Money paymentTotal = new Money(0);
		for(TransactionItem item : transactionItems) {
			if (item instanceof CashPaymentTransactionItem ||
					item instanceof RefundCashTransactionItem) {
				paymentTotal = paymentTotal.plus(item.getTotalCost());
			}
		}
		return paymentTotal;
	}
	
	public Money getSubTotal() {
	    Money paymentTotal = new Money(0);
		for(TransactionItem item : transactionItems) {
			if (item instanceof ClientTransactionItem) {
				paymentTotal = paymentTotal.plus(item.getTotalCost());
			}
		}
		return paymentTotal;
	}
}
