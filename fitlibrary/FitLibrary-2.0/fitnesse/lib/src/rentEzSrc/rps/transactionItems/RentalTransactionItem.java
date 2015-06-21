package rps.transactionItems;

import rps.Rental;
import rps.RentalItemType;
import rps.Reservation;
import rps.paymentMethod.Money;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

public class RentalTransactionItem extends ClientTransactionItem {
	private /*final*/ Reservation reservation;

	public RentalTransactionItem(ClientTransaction transaction, int count,
			RentalItemType hireItemType, Duration duration) {
		super(transaction);
		Duration actualDuration = hireItemType.getRates().fairDuration(duration);
		this.reservation = new Reservation(hireItemType, count, getStartDate(), actualDuration);
	}
    public boolean editRentalTransactionItem(int newCount, Duration newDuration){
        int oldCount = reservation.getCount();
        RentalItemType itemType = reservation.getHireItemType();
        MyDate oldStartDate = reservation.getStartDate();
        Duration oldDuration = reservation.getPeriod();
        try{
            this.reservation.removeAll();
            this.reservation = new Reservation(itemType, newCount, oldStartDate, newDuration);
            return true;
        }catch(RuntimeException ex){
            this.reservation = new Reservation(itemType, oldCount, oldStartDate, oldDuration);
            return false;
        }
    }
	@Override
	public Money getTotalCost() {
	    return reservation.totalRentalCost();
	}
	@Override
	public void complete() {
		new Rental(reservation,getClient(),getStaffMember());
	}
	public boolean isSame(int count, RentalItemType hireItemType, Duration duration) {		
		return (reservation.getCount() == count 
		    && (reservation.getHireItemType().getName()).equals(hireItemType.getName())
		    && reservation.getPeriod().equals(duration));
		    
	}
}