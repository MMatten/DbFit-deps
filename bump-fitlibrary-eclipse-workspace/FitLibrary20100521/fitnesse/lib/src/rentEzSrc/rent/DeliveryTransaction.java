package rent;

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
    
    public DeliveryTransaction(RentEz rentEz, ClientTransaction clientTransaction, MyDate date, Duration duration) {
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
        return clientTransaction.payWithCash(amount);
    }
    public boolean completeTransaction() {
        return clientTransaction.complete();
    }
	public Money bookOnFor(int count,String type,MyDate date,String stringDuration){
		Duration duration = Duration.parse(stringDuration);
		if(city == null)
			return clientTransaction.book(count,rentEz.getRentalItemType(type),date,duration, rentEz.getDeliveryRate(clientTransaction.getClient()));
		else
			return clientTransaction.book(count,rentEz.getRentalItemType(type),date,duration, rentEz.getDeliveryRate(city, zone), address);
	}
	public Money rentFor(int count,String type,String stringDuration){
		Duration duration = Duration.parse(stringDuration);
		return clientTransaction.rent(count,rentEz.getRentalItemType(type),duration);
	}
}
