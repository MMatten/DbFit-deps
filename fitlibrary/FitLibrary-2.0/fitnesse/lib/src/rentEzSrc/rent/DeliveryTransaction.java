package rent;

import static rent.Generator.*;
import rps.RentEz;
import rps.paymentMethod.Money;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

public class DeliveryTransaction {
    private ClientTransaction clientTransaction;
    private RentEz rentEz;
    private String city;
    private String zone;
    private String address;
    
    public DeliveryTransaction(RentEz rentEz, ClientTransaction clientTransaction, @SuppressWarnings("unused") MyDate date, @SuppressWarnings("unused") Duration duration) {
        this.rentEz = rentEz;
        this.clientTransaction = clientTransaction;
	    this.city = null;
	    this.zone = null;
	    this.address = null;

    }
	public DeliveryTransaction(RentEz rentEz,ClientTransaction clientTransaction){
		this.rentEz = rentEz;
		this.clientTransaction = clientTransaction;
	}
	public DeliveryTransaction(RentEz rentEz, ClientTransaction clientTransaction, String city, String zone, String address) {
        this.rentEz = rentEz;
        this.clientTransaction = clientTransaction;
	    this.city = city;
	    this.zone = zone;
	    this.address = address;
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
    public boolean completeTransaction() {
    	click("complete transaction");
        boolean complete = clientTransaction.complete();
        ok(complete,"Outstanding cash");
		return complete;
    }
	public Money bookOnFor(int count,String type,MyDate date,Duration duration){
    	header("Book delivery for");
    	click("rental items");
    	click(type);
    	withAddText("booking count",""+count);
    	withAddText("booking date",""+date);
    	withAddText("booking duration",duration.value());
    	clickRadio("booking duration type "+duration.units());
    	click("booking completed");
    	try {
    		Money cost;
    		if(city == null)
    			cost = clientTransaction.book(count,rentEz.getRentalItemType(type),date,duration, rentEz.getDeliveryRate(clientTransaction.getClient()));
    		else 
    			cost = clientTransaction.book(count,rentEz.getRentalItemType(type),date,duration, rentEz.getDeliveryRate(city, zone), address);
    		noErrorMessage();
    		textOfIs("last cost",""+cost);
    		return cost;
    	} catch (RuntimeException e) {
    		errorMessage("Insufficient items");
    		throw e;
    	}
	}
	public Money rentFor(int count,String rentalItemName,Duration duration){
    	header("Rent for");
    	click("rentals");
    	click(rentalItemName);
    	withAddText("rental count",""+count);
    	withAddText("rental duration",duration.value());
    	clickRadio("rental duration type "+duration.units());
    	click("rental completed");
    	try {
    		Money result = clientTransaction.rent(count,rentEz.getRentalItemType(rentalItemName),duration);
    		noErrorMessage();
			return result;
    	} catch (RuntimeException e) {
    		errorMessage("Insufficient items");
    		throw e;
    	}
	}
}
