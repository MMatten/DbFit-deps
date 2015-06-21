package rps.transactionItems;

import rps.Rates;
import rps.paymentMethod.Money;
import rps.transaction.AdminTransaction;

public class AddHireItemTypeTransactionItem extends TransactionItem {
	private int count;
	private String type;
	private Rates rates;
	private Money bond;

	public AddHireItemTypeTransactionItem(AdminTransaction transaction,
			int count, String type, Rates rates, Money bond) {
		super(transaction);
		this.count = count;
		this.type = type;
		this.rates = rates;
		this.bond = bond;
	}
	public Money getTotalCost() {
		return new Money(0);
	}
	public void complete() {
		getRentEz().createRentalItemType(type,count, rates, bond);
	}
	public String getType() {
		return type;
	}
	
	public boolean equals(Object other){
		if (!(other instanceof AddHireItemTypeTransactionItem))
			return false;
		AddHireItemTypeTransactionItem otherAddHireType = (AddHireItemTypeTransactionItem) other;
		
		return otherAddHireType.getType().equals(type);
	}
}