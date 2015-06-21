package rent;

import static rent.Generator.*;

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
    public Money rentForWeeks(int count, String rentalItemName, int weeks) {
    	notCovered();
        return clientTransaction.rent(count,getRentalItemType(rentalItemName),
                new Duration(0,0,weeks));
    }
    public Money rentFor(int count, String rentalItemName, Duration duration) {
    	header("Rent for");
    	click("rentals");
    	click(rentalItemName);
    	withAddText("rental count",""+count);
    	withAddText("rental duration",duration.value());
    	clickRadio("rental duration type "+duration.units());
    	click("rental completed");
    	try {
    		Money result = clientTransaction.rent(count,getRentalItemType(rentalItemName),duration);
    		noErrorMessage();
			return result;
    	} catch (RuntimeException e) {
    		errorMessage("Insufficient items");
    		throw e;
    	}
    }	
	public Money rentForWithRestrictionSatisfied(int count, String rentalItemName, Duration duration, String[] restrictIDSatisfied){
    	header("Rent for");
    	click("rentals");
    	click(rentalItemName);
    	withAddText("rental count",""+count);
    	withAddText("rental duration",duration.value());
    	clickRadio("rental duration type "+duration.units());
    	withAddText("rental restrictions",""+restrictIDSatisfied[0]);
    	click("rental completed");
    	try {
    		Money rent = clientTransaction.rent(count,getRentalItemType(rentalItemName),duration, restrictIDSatisfied);
    		noErrorMessage();
    		return rent;
    	} catch (RuntimeException e) {
    		errorMessage("Insufficient items");
    		throw e;
    	}
	}
	public Money rentForByClientOfAge(int count, String rentalItemName, Duration duration, int clientAge){
       	notCovered();
		return clientTransaction.rent(count,getRentalItemType(rentalItemName),duration, clientAge);
	}	
    public boolean dropRentFor(int count, String rentalItemName, Duration duration)  {
       	header("Drop item in transaction");
       	click("rental "+rentalItemName);
       	click("drop");
        return clientTransaction.dropRent(count,getRentalItemType(rentalItemName),duration);
    }	
    public boolean undropRentFor(int count, String rentalItemName, Duration duration)  {
       	header("Undrop item in transaction");
       	click("rental "+rentalItemName);
       	click("undrop");
        return clientTransaction.unDropRent(count,getRentalItemType(rentalItemName),duration);
    }		
    public Money totalIsDollar() {
       	notCovered();
        return clientTransaction.getTotal();
    }
    public boolean payWithCashDollar(Money amount) {
    	header("Pay with cash");
    	clickRadio("pay with cash");
    	withAddText("cash amount",""+amount);
    	click("pay");
        boolean payWithCash = clientTransaction.payWithCash(amount);
        ok(payWithCash,"Incorrect amount");
		return payWithCash;
    }
	public boolean payWithPointsDollar(BonusPoints amount){
	   	header("Pay with points");
    	clickRadio("pay with points");
    	withAddText("cash amount",""+amount);
    	click("pay");
        boolean pay = clientTransaction.payWithBonusPoint(amount);
        ok(pay,"Incorrect amount");
		return pay;
	}
	public boolean payWithAccountDollar(Money amount){
	   	header("Pay on account");
    	clickRadio("pay on account");
    	withAddText("cash amount",""+amount);
    	click("pay");
        boolean pay = clientTransaction.payOnAccount(amount);
        ok(pay,"Incorrect amount");
		return pay;
	}
	public boolean payWithCreditCardDollarCardTypeExpiresNumber(Money amount, String cardType, String expireDate, String cardNo) throws ParseException, InvalidCreditCardException{
    	header("Pay with credit card");
    	clickRadio("pay with credit card");
    	withAddText("credit card cardNo",""+cardNo);
    	withAddText("credit card type",""+cardType);
    	withAddText("credit card expire date",""+expireDate);
    	withAddText("credit cash amount",""+amount);
    	click("pay");
		boolean payWithCreditCard = clientTransaction.payWithCreditCard(amount, cardType, expireDate, cardNo);
        ok(payWithCreditCard,"Incorrect amount");
		return payWithCreditCard;
	}
    public boolean payWithVoucherDollar(Money amount) {
    	header("Pay with voucher");
    	clickRadio("pay with voucher");
    	withAddText("cash amount",""+amount);
    	click("pay");
        boolean pay = clientTransaction.payWithVoucher(new Voucher(amount));
        ok(pay,"Incorrect amount");
		return pay;
    }
    public boolean payWithVoucherDollarThatExpiresAfter(Money amount, MyDate expiryDate) {
    	header("Pay with voucher");
    	clickRadio("pay with voucher");
    	withAddText("cash amount",""+amount);
    	click("pay");
        boolean pay = clientTransaction.payWithVoucher(new Voucher(amount, expiryDate));
        ok(pay,"Incorrect amount");
		return pay;
    }
    public Money returnItems(int count, String name) {
        return returnItemsCostToFix(count,name,new Money());
    }
    public Money returnItemsCostToFix(int count, String rentalItemName, Money cost) {
    	header("Return items");
    	click("current rentals");
    	click("return "+rentalItemName);
    	withAddText("return count",""+count);
    	if (!cost.isZero())
    		withAddText("cost to fix",""+cost);
    	click("return it");
    	try {
    		Money resultingCost = clientTransaction.returnItems(cost,count,getRentalItemType(rentalItemName));
    		noErrorMessage();
    		return resultingCost;
    	} catch (RuntimeException e) {
    		errorMessage("Incorrect return");
    		throw e;
    	}
    }
    private RentalItemType getRentalItemType(String name) {
       return rentEz.getRentalItemType(name);
    }
    public Money bookOnFor(int count, String hireItemType,
    		MyDate date, Duration duration) throws Exception {
    	header("Book for");
    	click("rental items");
    	click(hireItemType);
    	withAddText("booking count",""+count);
    	withAddText("booking date",""+date);
    	withAddText("booking duration",duration.value());
    	clickRadio("booking duration type "+duration.units());
    	click("booking completed");
    	try {
    		Money cost = clientTransaction.book(count, getRentalItemType(hireItemType), date, duration);
    		noErrorMessage();
    		textOfIs("last cost",""+cost);
    		return cost;
    	} catch (Exception e) {
    		errorMessage("Insufficient items");
    		throw e;
    	}
    }
    public boolean acceptBookingOfForForHoursDaysWeeks(int count,
            String hireItemType, MyDate bookingDate, int hours, int days, int weeks) {
       	notCovered();
        return clientTransaction.acceptBooking(getRentalItemType(hireItemType), count, 
                bookingDate, new Duration(hours, days, weeks));
    }
    public boolean refundCashDollar(Money cashAmount) {
    	header("Refund");
    	clickRadio("refund");
    	withAddText("amount",""+cashAmount);
    	click("refund");
        boolean refundCash = clientTransaction.refundCash(cashAmount);
        ok(refundCash,"Too much");
		return refundCash;
    }
	public boolean refundAccountDollar(Money accountAmount){
    	header("Refund");
    	clickRadio("refund to account");
    	withAddText("amount",""+accountAmount);
    	click("refund");
		boolean refundAccountDollar = clientTransaction.refundAccountDollar(accountAmount);
        ok(refundAccountDollar,"Too much");
		return refundAccountDollar;
	}
    public Money changePeriodOfForForHoursDaysWeeksToHoursDaysWeeks(int count,
            String hireItemType, MyDate bookingDate, int hours, int days, int weeks,
            int newHours, int newDays, int newWeeks) {
       	notCovered();
        return clientTransaction.changePeriodOfBooking(count, getRentalItemType(hireItemType), 
                bookingDate, new Duration(hours,
                        days, weeks), new Duration(newHours, newDays, newWeeks));
    }
    public Money cancelBookingOfForForHoursDaysWeeks(int count,
            String hireItemType, MyDate bookingDate, int hours, int days, int weeks) {
       	notCovered();
        return clientTransaction.cancelBooking(count, getRentalItemType(hireItemType), bookingDate, new Duration(hours,
                days, weeks));
    }
    public Money buy(int count, String type) throws Exception {
    	header("Buy sales item");
    	click("Buy sales item");
    	click("sales type"+type);
       	withAddText("count of sales items",""+count);
       	click("buy");
       	try {
       		Money cost = clientTransaction.buy(count, rentEz.getSalesItemType(type));
       		noErrorMessage();
       		textOfIs("last cost",""+cost);
       		return cost;
       	} catch (Exception e) {
       		errorMessage("Insufficient items");
       		throw e;
       	}
    }
    public boolean payOnAccountDollar(Money amount) {
    	header("Pay on Account");
    	clickRadio("payment type on account");
    	withAddText("payment amount",""+amount);
    	click("pay on account");
        boolean payOnAccount = clientTransaction.payOnAccount(amount);
        ok(payOnAccount,"Too much");
		return payOnAccount;
    }
    public boolean cancelTransaction() {
    	click("cancel transaction");
        boolean cancel = clientTransaction.cancel();
        ok(cancel,"Unable to cancel");
		return cancel;
    }
    public boolean completeTransaction() {
    	click("complete transaction");
        boolean complete = clientTransaction.complete();
        ok(complete,"Outstanding cash");
		return complete;
    }
    public Money fillTemplateForPeopleFor(String templateName, int numPeople, Duration duration ) throws RpsException {
    	header("Make booking from template");
    	click("booking from template");
    	click("template "+templateName);
    	titleIs("Template "+templateName);
    	withAddText("number of people",""+numPeople);
    	withAddText("booking duration",duration.value());
    	clickRadio("booking duration type "+duration.units());
    	click("make booking");
    	Template template = rentEz.getTemplate(templateName);
    	Money amount = template.fillTemplate(numPeople,duration);
    	if(!clientTransaction.rentTemplate(numPeople, template, duration))
    		throw new RpsException("Not enough items in store to complete template");
    	return amount;
    }
	public Money cancelBookingOfOnFor(int count, String rentalItemName, MyDate bookingDate, Duration duration){
    	header("Cancel booking for");
    	click("bookings");
    	click(rentalItemName);
    	click("booking cancelled");
    	try {
    		Money cost = clientTransaction.cancelBooking(count, getRentalItemType(rentalItemName), bookingDate, duration);
    		noErrorMessage();
    		textOfIs("last cost",""+cost);
    		return cost;
    	} catch (RuntimeException e) {
    		errorMessage("Insufficient items");
    		throw e;
    	}
	}
    public Money changePeriodOfForWithDurationOfTo(int count, String rentalItemName, MyDate bookingDate, Duration oldDuration, Duration newDuration) {
    	header("Change booking");
    	click("bookings");
    	click(rentalItemName);
    	withAddText("booking count",""+count);
    	withAddText("booking date",""+bookingDate);
    	withAddText("booking duration",oldDuration.value());
    	clickRadio("booking duration type "+oldDuration.units());
    	withAddText("booking duration",oldDuration.value());
    	clickRadio("booking duration type "+oldDuration.units());
    	withAddText("booking duration",newDuration.value());
    	clickRadio("booking duration type "+newDuration.units());
    	click("update booking");
    	try {
    		Money cost = clientTransaction.changePeriodOfBooking(count, getRentalItemType(rentalItemName), bookingDate, oldDuration, newDuration);
    		noErrorMessage();
    		textOfIs("last cost",""+cost);
    		return cost;
    	} catch (RuntimeException e) {
    		errorMessage("Insufficient items");
    		throw e;
    	}
    }
    public boolean acceptBookingOfForFor(int count, String rentalItemName, MyDate bookingDate, Duration duration) {
    	header("Accept booking");
    	click("bookings");
    	click(rentalItemName);
    	withAddText("booking count",""+count);
    	withAddText("booking date",""+bookingDate);
    	withAddText("booking duration",duration.value());
    	clickRadio("booking duration type "+duration.units());
    	click("select booking");
    	click("accept booking");
        boolean acceptBooking = clientTransaction.acceptBooking(getRentalItemType(rentalItemName), count, bookingDate, duration);
        ok(acceptBooking,"Invalid booking");
		return acceptBooking;
    }
    public boolean modifyAmountOnRentForTo(int oldItemCount, String rentalItemName, Duration duration, int newItemCount){
       	header("Modify Rental");
    	click("rental "+rentalItemName);
    	withAddText("rental count",""+newItemCount);
        return clientTransaction.modifyRentalDetails(oldItemCount, getRentalItemType(rentalItemName), duration, newItemCount, duration);
    }
    public boolean modifyDurationOnRentForTo(int itemCount, String rentalItemName, Duration oldDuration, Duration newDuration){
       	header("Modify Rental");
    	click("rental "+rentalItemName);
    	withAddText("rental duration",newDuration.value());
    	clickRadio("rental duration type "+newDuration.units());
    	click("modify rental");
        boolean modifyRentalDetails = clientTransaction.modifyRentalDetails(itemCount, getRentalItemType(rentalItemName), oldDuration, itemCount, newDuration);
        ok(modifyRentalDetails,"Invalid change");
        return modifyRentalDetails;
    }
	public Money fillBookTemplateForPeopleOnFor(String templateName, int numPeople, MyDate date, Duration duration) throws Exception{
    	header("Fill Template");
    	click("fill template with booking");
    	withAddText("template name",""+templateName);
    	withAddText("template number of people",""+numPeople);
    	withAddText("booking date",""+date);
    	withAddText("booking duration",duration.value());
    	clickRadio("booking duration type "+duration.units());
    	click("create booking from template");
	    Template template = rentEz.getTemplate(templateName);
	    try {
	    	Money amount = template.fillTemplate(numPeople, duration);
	    	if(!clientTransaction.bookTemplate(numPeople, template, date, duration)) {
	    		errorMessage("Unable to make booking");
	    		throw new Exception("Could not complete template");
	    	}
	    	noErrorMessage();
	    	textOfIs("last cost",""+amount);
	    	return amount;
	    } catch (RuntimeException e) {
	    	errorMessage("Unable to make booking");
	    	throw e;
	    }
	}	
    public boolean purchaseVoucherDollar(float amount) {
    	header("Purchase voucher");
    	click("Purchase voucher");
    	withAddText("voucher amount",""+amount);
    	click("add voucher");
       return clientTransaction.purchaseVoucher(amount);
    }
	public Money fillRentTemplateForPeopleFor(String templateName,int numPeople,Duration duration) throws Exception{
    	header("Fill Template");
    	click("fill template with rent");
    	withAddText("template name",""+templateName);
    	withAddText("template number of people",""+numPeople);
    	withAddText("rental duration",duration.value());
    	clickRadio("rental duration type "+duration.units());
    	click("create rental from template");
		Template template = rentEz.getTemplate(templateName);
		try {
			Money amount = template.fillTemplate(numPeople,duration);
			if(!clientTransaction.rentTemplate(numPeople,template,duration)) {
				errorMessage("Unable to make rental");
				throw new Exception("Could not complete template");
			}
			noErrorMessage();
			textOfIs("last cost",""+amount);
			return amount;
		} catch (RuntimeException e) {
			errorMessage("Unable to make rental");
			throw e;
		}
	}
	public boolean cancelDeliveryCityZoneAddressItemItemCountFor(MyDate date, String city, String zone, String address, String hireItemType, int count, Duration duration) {
    	header("Cancel delivery");
    	click("deliveries");
    	click(hireItemType);
    	click("delivery cancelled");
		RentalItemType type = getRentalItemType(hireItemType);
		return clientTransaction.cancelDelivery(date, city, zone, address, type, count, duration);
	}
	public boolean changeDeliveryOnItemItemCountForCityZoneDeliveryAddressToDeliveryAddress(MyDate date, String hireItemType, int count, Duration duration, String city, String zone, String address, String newAddress) {
    	header("Change delivery");
    	click("deliveries");
    	click(hireItemType);
    	withAddText("address",newAddress);
    	click("change completed");
		RentalItemType type = getRentalItemType(hireItemType);
		return clientTransaction.changeDelivery(date, city, zone, address, type, count, duration, zone, newAddress);
	}
	public boolean changeDeliveryOnItemItemCountForCityZoneDeliveryAddressToZoneToDeliveryAddress(MyDate date, String hireItemType, int count, Duration duration, String city, String zone, String address, String newZone, String newAddress) {
    	header("Change delivery");
    	click("deliveries");
    	click(hireItemType);
    	withAddText("address",newAddress);
    	withAddText("zone",newZone);
    	click("change completed");
		RentalItemType type = getRentalItemType(hireItemType);
		return clientTransaction.changeDelivery(date, city, zone, address, type, count, duration, newZone, newAddress);
	}
}
