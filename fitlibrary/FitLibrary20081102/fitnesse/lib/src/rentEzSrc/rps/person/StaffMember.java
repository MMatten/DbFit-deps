package rps.person;

import rps.paymentMethod.Money;
import rps.transaction.AdminTransaction;
import rps.transaction.ClientTransaction;
import rps.transaction.Transaction;

public class StaffMember extends Person {
	private double commissionRate;
	private double discountRate;
	private Money commissionEarnings = new Money(0.0);

	public StaffMember() {
	}
	public StaffMember(StaffMember creator, String name, String phoneNumber) {
		super(creator, name, phoneNumber);
		commissionEarnings = new Money(0);
	}
	public StaffMember(StaffMember creator, String name, String phoneNumber,
			double commissionRate) {
		super(creator, name, phoneNumber);
		this.commissionRate = commissionRate;
		commissionEarnings = new Money(0);
	}
	public StaffMember(StaffMember creator, String name, String phoneNumber,
			double commissionRate, double discountRate) {
		super(creator, name, phoneNumber);
		this.commissionRate = commissionRate;
		this.discountRate = discountRate;
		commissionEarnings = new Money(0);
	}
	public boolean startTransaction(Transaction transaction) {
		currentTransaction = transaction;
		return true;
	}
	public boolean resumeClientTransaction(ClientTransaction transaction) {
		currentTransaction = transaction;
		return true;
	}
	public boolean resumeAdminTransaction(AdminTransaction transaction) {
		currentTransaction = transaction;
		return true;
	}

	public double getDiscountRate() {
		return discountRate;
	}

	public boolean transactionComplete() {
		if (currentTransaction instanceof ClientTransaction) {
			ClientTransaction transaction = (ClientTransaction) currentTransaction;
			if (!transaction.isStaffTransaction()) {
				Money paymentTotal = transaction.getPaymentTotal();
				Money commission = paymentTotal.times((commissionRate / 100)
						* -1.0);
				commissionEarnings = commissionEarnings.plus(commission);
			}
		}
		return super.transactionComplete();
	}

	public Money getCommissionEarnings() {
		return commissionEarnings;
	}

	public boolean equals(Object other) {
		if (other instanceof StaffMember) {
			StaffMember otherStaffMember = (StaffMember) other;
			if (commissionEarnings.equals(otherStaffMember.commissionEarnings))
				if ((currentTransaction == null && otherStaffMember.currentTransaction == null)
						|| currentTransaction.equals(otherStaffMember.currentTransaction))
					if (commissionRate == otherStaffMember.commissionRate)
						if (discountRate == otherStaffMember.discountRate)
							if ((creator == null && otherStaffMember.creator == null)
									|| creator.equals(otherStaffMember.creator))
								if (name.equals(otherStaffMember.name))
									if (phoneNumber
											.equals(otherStaffMember.phoneNumber))
										return true;
		}
		return false;
	}
}
