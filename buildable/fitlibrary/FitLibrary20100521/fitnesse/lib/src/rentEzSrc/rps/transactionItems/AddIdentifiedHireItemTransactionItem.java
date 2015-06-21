package rps.transactionItems;

import rps.paymentMethod.Money;
import rps.time.MyDate;
import rps.transaction.AdminTransaction;

public class AddIdentifiedHireItemTransactionItem extends TransactionItem {
	private String id;
	private String type;
	private MyDate lastMaintainedDate;
	private double monthsBtwMaintenance;

	public AddIdentifiedHireItemTransactionItem(AdminTransaction transaction,
			String id, String type, MyDate lastMaintainedDate,
			double monthsBtwMaintenance) {
		super(transaction);
		this.id = id;
		this.type = type;
		this.lastMaintainedDate = lastMaintainedDate;
		this.monthsBtwMaintenance = monthsBtwMaintenance;
	}
	public Money getTotalCost() {
		return new Money();
	}
	public void complete() {
		getRentEz().getRentalItemType(type).addRentalItem(id,
				lastMaintainedDate, monthsBtwMaintenance);
	}
	public String getType() {
		return type;
	}
	public String getId() {
		return id;
	}
}