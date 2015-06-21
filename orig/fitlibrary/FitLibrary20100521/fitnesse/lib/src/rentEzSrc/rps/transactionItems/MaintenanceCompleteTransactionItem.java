package rps.transactionItems;

import rps.RentalItemType;
import rps.paymentMethod.Money;
import rps.transaction.Transaction;

public class MaintenanceCompleteTransactionItem extends TransactionItem {
	private RentalItemType hireItemType;
	private String itemIdentifier;

	public MaintenanceCompleteTransactionItem(Transaction transaction,
			RentalItemType type, String itemIdentifier) throws Exception {
		super(transaction);
		this.hireItemType = type;
		this.itemIdentifier = itemIdentifier;
		if (!hireItemType.identifiedItemIsInMaintenance(itemIdentifier)) {
		    throw new RuntimeException(
				hireItemType.getName() + " " + itemIdentifier
						+ " is not currently being maintained"); }
	}
	public Money getTotalCost() {
		return new Money();
	}
	public void complete() {
		hireItemType.returnFromRepair(getTransaction().getStartDate(),
				itemIdentifier);
	}
}