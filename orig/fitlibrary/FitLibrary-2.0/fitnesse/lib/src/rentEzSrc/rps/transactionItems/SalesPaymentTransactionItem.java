package rps.transactionItems;

import rps.BuyItemType;
import rps.paymentMethod.Money;
import rps.transaction.Transaction;

public class SalesPaymentTransactionItem extends TransactionItem {
	private int count;
	private BuyItemType buyItemType;
	
	public SalesPaymentTransactionItem(Transaction transaction, int count, BuyItemType buyItemType) {		
		super(transaction);
		this.count = count;
		this.buyItemType = buyItemType;
		if (count > buyItemType.getCount()) {
			throw new RuntimeException("Required " + count + " but only " + buyItemType.getCount() + " left.");
		}
	}
	@Override
	public Money getTotalCost() {
		return buyItemType.getSellingPrice(count);
	}
	@Override
	public void complete() {
		if(!( buyItemType.sale(count))) {
			throw new RuntimeException("Could not complete sales payment");
		}
	}
}
