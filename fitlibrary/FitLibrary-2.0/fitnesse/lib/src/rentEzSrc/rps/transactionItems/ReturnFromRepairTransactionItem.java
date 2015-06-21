package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.transaction.AdminTransaction;

public class ReturnFromRepairTransactionItem extends TransactionItem {
	private String type;
	private int count;

	public ReturnFromRepairTransactionItem(AdminTransaction transaction,
			String type, int count) {
		super(transaction);
		this.type = type;
		this.count = count;
	}
	@Override
	public Money getTotalCost() {
		return new Money();
	}
	@Override
	public void complete() {
		if (!(getRentEz().returnFromRepair(
				getTransaction().getStartDate(), type, count)))
			throw new RuntimeException("Could not return " + count + " " + type + " from repair");
	}
}