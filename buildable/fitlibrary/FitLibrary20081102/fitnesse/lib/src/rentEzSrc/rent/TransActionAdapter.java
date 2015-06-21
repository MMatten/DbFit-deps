package rent;

import java.text.ParseException;

import rps.RentEz;
import rps.RentalItemType;
import rps.Template;
import rps.exception.RpsException;
import rps.paymentMethod.BonusPoints;
import rps.paymentMethod.Money;
import rps.paymentMethod.Voucher;
import rps.paymentMethod.creditCardLib.InvalidCreditCardException;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

public class TransActionAdapter {
    private ClientTransaction clientTransaction;
    private RentEz rentEz;
    
    public TransActionAdapter(RentEz rentEz, ClientTransaction clientTransaction) {
        this.rentEz = rentEz;
        this.clientTransaction = clientTransaction;
    }
    public Money rentForWeeks(int count, String rentalItemName,
            int weeks) {
        return clientTransaction.rent(count,getRentalItemType(rentalItemName),
                new Duration(0,0,weeks));
    }
    public Money rentFor(int count, String rentalItemName, Duration duration) {
        return clientTransaction.rent(count,getRentalItemType(rentalItemName),duration);
    }	
	public Money rentForByClientOfAge(int count, String rentalItemName, Duration duration, int clientAge){
		return clientTransaction.rent(count,getRentalItemType(rentalItemName),duration, clientAge);
	}	
    public boolean dropRentFor(int count, String rentalItemName, Duration duration)  {
        return clientTransaction.dropRent(count,getRentalItemType(rentalItemName),duration);
    }	
    public boolean undropRentFor(int count, String rentalItemName, Duration duration)  {
        return clientTransaction.unDropRent(count,getRentalItemType(rentalItemName),duration);
    }		
    public Money totalIsDollar() {
        return clientTransaction.getTotal();
    }
    public boolean payWithCashDollar(Money amount) {
        return clientTransaction.payWithCash(amount);
    }
	public boolean payWithPointsDollar(BonusPoints amount){
		return clientTransaction.payWithBonusPoint(amount);
	}
	public boolean payWithAccountDollar(Money amount){
		return clientTransaction.payOnAccount(amount);
	}
    public Money returnItems(int count, String name) {
        return returnItemsCostToFix(count,name,new Money());
    }
    public Money returnItemsCostToFix(int count, String rentalItemName, Money cost) {
        return clientTransaction.returnItems(cost,count,getRentalItemType(rentalItemName));
    }
    private RentalItemType getRentalItemType(String name) {
        return rentEz.getRentalItemType(name);
    }
    public Money bookOnFor(int count, String hireItemType,
            MyDate date, Duration duration) throws Exception {
        return clientTransaction.book(count, getRentalItemType(hireItemType), date, duration);
    }
    public boolean acceptBookingOfForForHoursDaysWeeks(int count,
            String hireItemType, MyDate bookingDate, int hours, int days, int weeks) {
        return clientTransaction.acceptBooking(getRentalItemType(hireItemType), count, 
                bookingDate, new Duration(hours, days, weeks));
    }
    public boolean refundCashDollar(Money cashAmount) {
        return clientTransaction.refundCash(cashAmount);
    }
	public boolean refundAccountDollar(Money accountAmount){
		return clientTransaction.refundAccountDollar(accountAmount);
	}
    public Money changePeriodOfForForHoursDaysWeeksToHoursDaysWeeks(int count,
            String hireItemType, MyDate bookingDate, int hours, int days, int weeks,
            int newHours, int newDays, int newWeeks) {
        return clientTransaction.changePeriodOfBooking(count, getRentalItemType(hireItemType), 
                bookingDate, new Duration(hours,
                        days, weeks), new Duration(newHours, newDays, newWeeks));
    }
    public Money cancelBookingOfForForHoursDaysWeeks(int count,
            String hireItemType, MyDate bookingDate, int hours, int days, int weeks) {
        return clientTransaction.cancelBooking(count, getRentalItemType(hireItemType), bookingDate, new Duration(hours,
                days, weeks));
    }
    public Money buy(int count, String type) throws Exception {
        return clientTransaction.buy(count, rentEz.getSalesItemType(type));
    }
    public boolean payOnAccountDollar(Money amount) {
        return clientTransaction.payOnAccount(amount);
    }
    public boolean cancelTransaction() {
        return clientTransaction.cancel();
    }
    public boolean completeTransaction() {
        return clientTransaction.complete();
    }
    public Money fillTemplateForPeopleFor(String templateName, int numPeople, Duration numDays ) throws RpsException {
        Template template = rentEz.getTemplate(templateName);
        Money amount = template.fillTemplate(numPeople,numDays);
        if(!clientTransaction.rentTemplate(numPeople, template, numDays))
            throw new RpsException("Not enough items in store to complete template");
        return amount;
    }
	// jpe001
	public Money rentForWithRestrictionSatisfied(int count, String rentalItemName, Duration duration, String[] restrictIDSatisfied){
		return clientTransaction.rent(count,getRentalItemType(rentalItemName),duration, restrictIDSatisfied);
	}
	public Money cancelBookingOfOnFor(int count, String rentalItemName, MyDate bookingDate, Duration duration){
		return clientTransaction.cancelBooking(count, getRentalItemType(rentalItemName), bookingDate, duration);
	}
    public Money changePeriodOfForWithDurationOfTo(int count, String rentalItemName, MyDate bookingDate, Duration oldDuration, Duration newDuration) {
        return clientTransaction.changePeriodOfBooking(count, getRentalItemType(rentalItemName), bookingDate, oldDuration, newDuration);
    }
    public boolean acceptBookingOfForFor(int count, String rentalItemName, MyDate bookingDate, Duration duration) {
        return clientTransaction.acceptBooking(getRentalItemType(rentalItemName), count, bookingDate, duration);
    }
    public boolean payWithVoucherDollar(Money amount) {
        return clientTransaction.payWithVoucher(new Voucher(amount));
    }
    public boolean payWithVoucherDollarThatExpiresAfter(Money amount, MyDate expiryDate) {
        return clientTransaction.payWithVoucher(new Voucher(amount, expiryDate));
    }
    public boolean modifyAmountOnRentForTo(int oldItemCount, String rentalItemName, Duration duration, int newItemCount){
        return clientTransaction.modifyRentalDetails(oldItemCount, getRentalItemType(rentalItemName), duration, newItemCount, duration);
    }
    public boolean modifyDurationOnRentForTo(int itemCount, String rentalItemName, Duration oldDuration, Duration newDuration){
        return clientTransaction.modifyRentalDetails(itemCount, getRentalItemType(rentalItemName), oldDuration, itemCount, newDuration);
    }
	public boolean payWithCreditCardDollarCardTypeExpiresNumber(Money amount, String cardType, String expireDate, String cardNo) throws ParseException, InvalidCreditCardException{
		return clientTransaction.payWithCreditCard(amount, cardType, expireDate, cardNo);
	}
	public Money fillBookTemplateForPeopleOnFor(String templateName, int numPeople, MyDate date, String stringDuration) throws Exception{
	    Template template = rentEz.getTemplate(templateName);
		Duration duration = Duration.parse(stringDuration);
		Money amount = template.fillTemplate(numPeople, duration);
		if(!clientTransaction.bookTemplate(numPeople, template, date, duration))
			throw new Exception("Could not complete template");
		return amount;
	}	
    public boolean purchaseVoucherDollar(float amount) {
        return clientTransaction.purchaseVoucher(amount);
    }
	public Money fillRentTemplateForPeopleFor(String templateName,int numPeople,String stringDuration) throws Exception{
		Template template = rentEz.getTemplate(templateName);
		Duration duration = Duration.parse(stringDuration);
		Money amount = template.fillTemplate(numPeople,duration);
		if(!clientTransaction.rentTemplate(numPeople,template,duration))
			throw new Exception("Could not complete template");
		return amount;
	}
	public boolean cancelDeliveryCityZoneAddressItemItemCountFor(MyDate date, String city, String zone, String address, String hireItemType, int count, Duration duration) {
		RentalItemType type = getRentalItemType(hireItemType);
		return clientTransaction.cancelDelivery(date, city, zone, address, type, count, duration);
	}
	public boolean changeDeliveryOnItemItemCountForCityZoneDeliveryAddressToDeliveryAddress(MyDate date, String hireItemType, int count, Duration duration, String city, String zone, String address, String newAddress) {
		RentalItemType type = getRentalItemType(hireItemType);
		return clientTransaction.changeDelivery(date, city, zone, address, type, count, duration, zone, newAddress);
	}
	public boolean changeDeliveryOnItemItemCountForCityZoneDeliveryAddressToZoneToDeliveryAddress(MyDate date, String hireItemType, int count, Duration duration, String city, String zone, String address, String newZone, String newAddress) {
		RentalItemType type = getRentalItemType(hireItemType);
		return clientTransaction.changeDelivery(date, city, zone, address, type, count, duration, newZone, newAddress);
	}
}
