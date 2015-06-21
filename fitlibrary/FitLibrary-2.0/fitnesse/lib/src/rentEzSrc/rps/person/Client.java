package rps.person;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import rps.Delivery;
import rps.Rental;
import rps.RentalItemType;
import rps.paymentMethod.BonusPoints;
import rps.paymentMethod.Money;

public class Client extends Person {
	private Money amountOwing = new Money();
	private BonusPoints bonusPoints = new BonusPoints();
	private String city;
	private String zone;
	private String address;
	private String email;

	public Client() {
		//
	}
	public Client(StaffMember creator, String name, String phoneNumber) {
		this(creator, name, phoneNumber, "", "", "");
	}

	public Client(StaffMember creator, String name, String phoneNumber,
			String city, String zone, String deliveryAddr) {
		super(creator, name, phoneNumber);
		this.city = city;
		this.zone = zone;
		this.address = deliveryAddr;
	}

	public Client(StaffMember member, String name, String phoneNumber,
			String city, String zone, String deliveryAddr, String email) {
		this(member, name, phoneNumber, city, zone, deliveryAddr);
		this.email = email;
	}

	public void returnHires(RentalItemType hireItemType, int countInitial) {
		int count = countInitial;
		Set<Rental> toBeReturned = new TreeSet<Rental>();
		for (Rental rental : getRentals())
			if (rental.getHireItemType() == hireItemType)
				toBeReturned.add(rental);
		// Separate loop to avoid problems with concurrent access to list
		// through Iterator:
		for (Rental rental : toBeReturned)
			count -= rental.returnRental(count);
	}

	public Money getAmountOwing() {
		return amountOwing;
	}

	public void payOnAccount(Money amount) {
		amountOwing = amountOwing.plus(amount);
	}

	public void refundToAccount(Money amount) {
		amountOwing = amountOwing.minus(amount);
	}

	public void clearCurrentTransaction() {
		currentTransaction = null;
	}

	public BonusPoints getBonusPoints() {
		return this.bonusPoints;
	}

	public void topupBonusPoints(BonusPoints points) {
		bonusPoints = bonusPoints.plus(points);
	}

	public void reduceBonusPoints(BonusPoints points) {
		this.bonusPoints = this.bonusPoints.minus(points);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Client) {
			Client otherClient = (Client) other;
			return name.equals(otherClient.name)
					&& phoneNumber.equals(otherClient.phoneNumber)
					&& creator.equals(otherClient.creator);
		}
		return false;
	}
	public String getCity() {
		return city;
	}
	public String getZone() {
		return zone;
	}
	public String getAddress() {
		return address;
	}
	public String getEmail() {
		return email;
	}
	public List<Delivery> getDeliveries() {
		return deliveries;
	}
	public void addDelivery(Delivery delivery) {
		deliveries.add(delivery);
	}
	
	//a static method to validate emails
	// could possibly be put in another class
	//akoz002
	public static boolean isValidEmail(String email) {
		return Pattern.matches("[\\w[.]]*[@][^@][\\w]*[.][^.][\\w]*[.][^.][\\w]*", email)
				|| Pattern.matches("[\\w[.]]*[@][^@][\\w]*[.][^.][\\w]*", email);
	}
	@Override
	public String toString() {
		return name;
	}
}