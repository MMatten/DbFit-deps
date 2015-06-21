package rps.transactionItems;

import rps.RentalItemType;
import rps.paymentMethod.Money;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

public class ReturnItemsTransactionItem extends ClientTransactionItem {
	private int count;
	private Money fixCost;
	private RentalItemType rentalItemType;

	public ReturnItemsTransactionItem(Money fixCost, ClientTransaction transaction,
			int count, RentalItemType rentalItemType) {
		super(transaction);
		this.fixCost = fixCost;
		this.count = count;
		this.rentalItemType = rentalItemType;
		if (!rentalItemType.hasHiresToReturn(count))
		    throw new RuntimeException("There aren't " + count + " items to return");
	}
	public Money getTotalCost() {
		MyDate time = getRentEz().getTime();
        Money calculateRefundOnReturn = rentalItemType.calculateRefundOnReturn(time,count);
        return fixCost.minus(rentalItemType.getBond(count)).minus(calculateRefundOnReturn);
	}
	public void complete() {
		getClient().returnHires(rentalItemType, count);
	}
}