package rps.transaction;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rent.Rental;
import rps.BuyItemType;
import rps.Delivery;
import rps.DeliveryRate;
import rps.RentEz;
import rps.RentalItemType;
import rps.Template;
import rps.exception.MissingException;
import rps.paymentMethod.BonusPoints;
import rps.paymentMethod.CreditCard;
import rps.paymentMethod.Money;
import rps.paymentMethod.Voucher;
import rps.paymentMethod.creditCardLib.InvalidCreditCardException;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transactionItems.AcceptBookingTransactionItem;
import rps.transactionItems.AccountPaymentTransactionItem;
import rps.transactionItems.BonusPointPaymentTransactionItem;
import rps.transactionItems.BookTransactionItem;
import rps.transactionItems.CancelBookingTransactionitem;
import rps.transactionItems.CancelDeliveryTransactionItem;
import rps.transactionItems.CashPaymentTransactionItem;
import rps.transactionItems.ChangeDeliveryTransactionItem;
import rps.transactionItems.ChangePeriodOfBookingTransactionItem;
import rps.transactionItems.CreditCardPaymentTransactionItem;
import rps.transactionItems.RefundAccountDollarTransactionItem;
import rps.transactionItems.RefundCashTransactionItem;
import rps.transactionItems.RentalTransactionItem;
import rps.transactionItems.ReturnItemsTransactionItem;
import rps.transactionItems.SalesPaymentTransactionItem;
import rps.transactionItems.TransactionItem;
import rps.transactionItems.VoucherPaymentTransactionItem;

public class ClientTransaction extends Transaction {
	private Client client;

	private List<TransactionItem> droppedTransactionItems = new ArrayList<TransactionItem>();

	public ClientTransaction(RentEz phs, MyDate startDate,
			StaffMember staffMember, Client client, boolean deliver) {
		super(phs, startDate, staffMember);

		if (!(client.startTransaction(this)))
			throw new RuntimeException(
					"Client must not have a pending transaction");
		this.client = client;
		if (isStaffHireFromThemselves()) {
			throw new RuntimeException("Staff Hires are not allowed");
		}
	}

	public ClientTransaction(RentEz phs, MyDate startDate,
			StaffMember staffMember, Client client) {
		this(phs, startDate, staffMember, client, false);
	}

	@Override
	public boolean complete() {
		// if(deliver) {
		// addTransactionItem(new DeliveryTransactionItem(this,
		// getRentEz().getDeliveryRate(client.getCity(),client.getZone())));
		// }
		if (isStaffTransaction()) {
			double discountRate;
			try {
				discountRate = getRentEz().getStaffMember(client.getName())
						.getDiscountRate();
			} catch (MissingException e) {
				discountRate = 0;
			}
			Money total = getSubTotal();
			Money discount = total.times((discountRate / 100) * -1.0);
			giveStaffDiscount(discount);
		}
		boolean completed = super.complete() && client.transactionComplete();
		if (completed) {
			getRentEz().removePendingClientTransaction(getClient().getName());
		}

		// if(deliver&&completed) {
		// // client.addDelivery(new Delivery());
		// }

		return completed;
	}
	
	public Money rent(List<Rental> rentals) {
		return new Money();
	}

	public Money rentFor(int count, RentalItemType rentalItemType, Duration duration) {
		return rent(count,rentalItemType,duration);
	}
	public Money rent(int count, RentalItemType rentalItemType, Duration duration) {
		return rent(count, rentalItemType, duration,
				RentalItemType.CLIENT_AUTO_PASS_MIN_AGE);
	}

	public Money rent(int count, RentalItemType hireItemType,
			Duration duration, int clientAge) {
		if (!hireItemType.canRentWithClientAge(clientAge)) {
			throw new RuntimeException(
					"Transaction rejected - client younger than minimum age");
		}
		return rent(count, hireItemType, duration, new String[] { "" });
	}

	public Money rent(int count, RentalItemType hireItemType,
			Duration duration, String[] restrictIDSatisfied) {
		if (!hireItemType.canRentWithSatisfiedRestriction(restrictIDSatisfied)) {
			throw new RuntimeException(
					"Transaction rejected - Restriction not met");
		}
		TransactionItem item = new RentalTransactionItem(this, count,
				hireItemType, duration);
		addTransactionItem(item);
		return item.getTotalCost();
	}

	public Money book(int count, RentalItemType itemType, MyDate startDate,
			Duration period) {
		return book(count, itemType, startDate, period, null, client.getAddress());
	}
	public Money book(int count, RentalItemType itemType, MyDate startDate,
			Duration period, DeliveryRate deliveryRate) {
		return book(count, itemType, startDate, period, deliveryRate, client.getAddress());
	}

	public Money book(int count, RentalItemType itemType, MyDate startDate,
			Duration period, DeliveryRate deliveryRate, String address) {
		TransactionItem item = new BookTransactionItem(this, count, itemType,
				startDate, period, deliveryRate, address);
		addTransactionItem(item);
		return item.getTotalCost();
	}

	public boolean payWithCash(Money amount) {
		// if (getPaymentTotal().equals(amount) || getTotal().equals(amount)) {
		TransactionItem item = new CashPaymentTransactionItem(this, amount);
		addTransactionItem(item);
		this.client.topupBonusPoints(BonusPoints.bonusPointCalculation(amount));
		return true;
		// }
		// return false;
	}

	public boolean payWithBonusPoint(BonusPoints amount) {
		if (!checkIfVoucherUsed()
				&& client.getBonusPoints().getValueInDouble() >= amount
						.getValueInDouble()) {
			TransactionItem item = new BonusPointPaymentTransactionItem(this,
					amount, this.client);
			addTransactionItem(item);
			return true;
		}
		return false;
	}

	private boolean checkIfPointsUsed() {
		for (Iterator<TransactionItem> iter = getTransactionItems(); iter
				.hasNext();) {
			TransactionItem ti = iter.next();
			if (ti instanceof BonusPointPaymentTransactionItem) {
				return true;
			}
		}
		return false;
	}

	private boolean checkIfVoucherUsed() {
		for (Iterator<TransactionItem> iter = getTransactionItems(); iter
				.hasNext();) {
			TransactionItem ti = iter.next();
			if (ti instanceof VoucherPaymentTransactionItem) {
				return true;
			}
		}
		return false;
	}

	public boolean payWithVoucher(Voucher amount) {
		if ( ((amount.getExpiryDate() == null || getRentEz().getTime().before(
				amount.getExpiryDate())) && !checkIfPointsUsed()) ) {
			TransactionItem item = new VoucherPaymentTransactionItem(this,
					amount, getTotal(), client, amount.getExpiryDate());
			addTransactionItem(item);
			return true;
		}
		return false;
	}

	public Money returnItems(Money fixCost, int count,
			RentalItemType rentalItemType) {
		ReturnItemsTransactionItem item = new ReturnItemsTransactionItem(
				fixCost, this, count, rentalItemType);
		addTransactionItem(item);
		return item.getTotalCost();
	}

	public boolean refundCash(Money cashAmount) {
		TransactionItem item = new RefundCashTransactionItem(this, cashAmount);
		addTransactionItem(item);
		this.client.reduceBonusPoints(BonusPoints
				.bonusPointCalculation(cashAmount));
		return true;
	}

	public boolean giveStaffDiscount(Money discount) {
		if (isStaffTransaction()) {
			TransactionItem item = new RefundCashTransactionItem(this, discount);
			addTransactionItem(item);
			return true;
		}
		return false;
	}

	public boolean refundAccountDollar(Money accountAmount) {
		TransactionItem item = new RefundAccountDollarTransactionItem(this,
				this.client, accountAmount);
		addTransactionItem(item);
		this.client.reduceBonusPoints(BonusPoints
				.bonusPointCalculation(accountAmount));
		return true;
	}

	public Client getClient() {
		return client;
	}

	public boolean acceptBooking(RentalItemType hireItemType, int count,
			MyDate bookingDate, Duration period) {
		TransactionItem item = new AcceptBookingTransactionItem(this,
				hireItemType, count, bookingDate, period);
		addTransactionItem(item);
		return true;
	}

	public Money changePeriodOfBooking(int count, RentalItemType hireItemType,
			MyDate bookingDate, Duration period, Duration newPeriod) {
		TransactionItem item = new ChangePeriodOfBookingTransactionItem(this,
				hireItemType, count, bookingDate, period, newPeriod);
		addTransactionItem(item);
		return item.getTotalCost();
	}

	public Money cancelBooking(int count, RentalItemType hireItemType,
			MyDate bookingDate, Duration period) {
		TransactionItem item = new CancelBookingTransactionitem(this,
				hireItemType, count, bookingDate, period);
		addTransactionItem(item);
		return item.getTotalCost();
	}

	public Money buy(int count, BuyItemType type) {
		TransactionItem item = new SalesPaymentTransactionItem(this, count,
				type);
		addTransactionItem(item);
		return item.getTotalCost();
	}

	public boolean cancel() {
		if (!super.cancel()) {
			return false;
		}
		client.clearCurrentTransaction();
		removeLastTransactionItem();
		return true;
	}

	public boolean payOnAccount(Money amount) {
		TransactionItem item = new AccountPaymentTransactionItem(this, amount);
		addTransactionItem(item);
		return true;
	}

	public boolean dropRent(int count, RentalItemType hireItemType,
			Duration duration) {
		RentalTransactionItem item = null;
		for (Iterator it = getTransactionItems(); it.hasNext();) {
			item = (RentalTransactionItem) it.next();
			if (item.isSame(count, hireItemType, duration))
				break;
			else
				item = null;

		}
		if (item != null) {
			addDroppedTransactionItem(item);
			removeTransactionItem(item);
		} else
			return false;

		return true;
	}

	private void addDroppedTransactionItem(TransactionItem item) {
		droppedTransactionItems.add(item);
	}

	private void removeDroppedTransactionItem(TransactionItem item) {
		droppedTransactionItems.remove(item);
	}

	private Iterator getDroppedTransactionItems() {
		return droppedTransactionItems.iterator();

	}

	public boolean unDropRent(int count, RentalItemType hireItemType,
			Duration duration) {
		RentalTransactionItem item = null;
		for (Iterator it = getDroppedTransactionItems(); it.hasNext();) {
			item = (RentalTransactionItem) it.next();
			if (item.isSame(count, hireItemType, duration))
				break;
			else
				item = null;

		}
		if (item != null) {
			removeDroppedTransactionItem(item);
			addTransactionItem(item);
		} else
			return false;

		return true;

	}

	public boolean rentTemplate(int numPeople, Template template,Duration duration) {
		ArrayList items = template.getItems();
		int rentCount = 0;
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			ArrayList a = (ArrayList) iter.next();
			int numItems = (int) Math.ceil(numPeople / ((Float) a.get(1)));
			try {
				if (a.get(0) instanceof RentalItemType) {
					RentalItemType hireItem = (RentalItemType) a.get(0);
					rent(numItems, hireItem, duration);
					rentCount++;
				}
				else if (a.get(0) instanceof BuyItemType) {
					BuyItemType buyItem = (BuyItemType) a.get(0);
					buy(numItems,buyItem);
				}
			} catch (Exception e) {
				for (int i = 0; i < rentCount; i++) {
					removeLastTransactionItem();
				}
				return false;
			}
		}
		return true;
	}

	public boolean isStaffTransaction() {
		try {
			getRentEz().getStaffMember(client.getName());
		} catch (MissingException e) {
			return false;
		}
		return true;
	}

	public boolean isStaffHireFromThemselves() {
		return client.getName().equals(getStaffMember().getName());
	}

	public boolean modifyRentalDetails(int oldItemCount,
			RentalItemType rentalItemType, Duration duration, int newItemCount,
			Duration newDuration) {
		for (Iterator it = getTransactionItems(); it.hasNext();) {
			Object tempObj = it.next();
			if (tempObj instanceof RentalTransactionItem) {
				RentalTransactionItem tempRentalTransItem = (RentalTransactionItem) tempObj;
				if (tempRentalTransItem.isSame(oldItemCount, rentalItemType,
						duration)) {
					if (tempRentalTransItem.editRentalTransactionItem(
							newItemCount, newDuration)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean payWithCreditCard(Money amount, String cardType,
			String expireDate, String cardNo) throws ParseException,
			InvalidCreditCardException {
		TransactionItem item = new CreditCardPaymentTransactionItem(this,
				amount, new CreditCard(cardType, expireDate, cardNo, rentEz));
		addTransactionItem(item);
		client.topupBonusPoints(BonusPoints.bonusPointCalculation(amount));
		return true;
	}

	public boolean purchaseVoucher(float amount) {
		client.topupBonusPoints(BonusPoints.bonusPointCalculation(new Money(
				amount)));
		return true;
	}

	public boolean bookTemplate(int numPeople, Template template,
			MyDate startDate, Duration duration) {
		ArrayList items = template.getItems();
		int rentCount = 0;
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			ArrayList a = (ArrayList) iter.next();
			int numItems = (int) Math.ceil(numPeople / ((Float) a.get(1)));
			try {
				if (a.get(0) instanceof RentalItemType) {
					RentalItemType hireItem = (RentalItemType) a.get(0);
					book(numItems, hireItem, startDate, duration);
					rentCount++;
				}
				else if (a.get(0) instanceof BuyItemType) {
					BuyItemType buyItem = (BuyItemType) a.get(0);
					buy(numItems,buyItem);
				}
			} catch (Exception e) {
				for (int i = 0; i < rentCount; i++) {
					removeLastTransactionItem();
				}
				return false;
			}
		}
		return true;
	}

	public boolean cancelDelivery(MyDate date, String city, String zone, String address, RentalItemType itemType, int count, Duration duration) {
		TransactionItem transItem;
		try {
			transItem = new CancelDeliveryTransactionItem(this, itemType, count,
					date, duration, new Delivery(date,city,zone,address, itemType.getName(), count));
		} catch (Exception e) {
			return false;
		}
		addTransactionItem(transItem);
		return true;
	}

	public boolean changeDelivery(MyDate date, String city, String zone, String address, RentalItemType itemType, int count, Duration duration, String newZone, String newAddress) {
		TransactionItem transItem = new ChangeDeliveryTransactionItem(this, itemType, count,
					date, duration, new Delivery(date,city,zone,address, itemType.getName(), count), newZone, newAddress);
		addTransactionItem(transItem);
		return true;
	}
	public String getStaff(){
		return getStaffMember().getName();
	}	
	
	public Money getOwing(){
		return getPaymentTotal();
	}	
 

}