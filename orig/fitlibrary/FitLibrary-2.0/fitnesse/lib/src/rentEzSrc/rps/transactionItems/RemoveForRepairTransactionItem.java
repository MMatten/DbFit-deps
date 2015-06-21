package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.transaction.AdminTransaction;

public class RemoveForRepairTransactionItem extends TransactionItem {
	private String type;
	private int count;

	public RemoveForRepairTransactionItem(AdminTransaction transaction,
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
		if (!((AdminTransaction) getTransaction()).getRentEz().removeForRepair(type,
				count))
		    throw new RuntimeException("Could not return " + count + " " + type
				+ " from repair");
	}
}