/*
 * @author Rick Mugridge on Nov 8, 2004
 * Released under the terms of the GNU General Public License version 2 or later.
 *
 */
package rent;

import rps.RentEz;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.ClientTransaction;

/**
 *
 */
public class Refunder { //COPY:ALL
    private RentEz rentEz; //COPY:ALL
    private StaffMember staff; //COPY:ALL
    private Client client; //COPY:ALL
    private String rentalItemName; //COPY:ALL
    //COPY:ALL
    public Refunder(RentEz rentEz, StaffMember staff,  //COPY:ALL
            Client client, String rentalItemName) { //COPY:ALL
        this.rentEz = rentEz; //COPY:ALL
        this.staff = staff; //COPY:ALL
        this.client = client; //COPY:ALL
        this.rentalItemName = rentalItemName; //COPY:ALL
    } //COPY:ALL
    public Money refundPaidTimeActualTime(Duration paidDuration, //COPY:ALL
            @SuppressWarnings("unused") Duration actual) { //COPY:ALL
        final int count = 1; //COPY:ALL
        final MyDate startDate = new MyDate(); //COPY:ALL
        final MyDate endDate = paidDuration.dateAfter(startDate); //COPY:ALL
        //COPY:ALL
        // Use a transaction to rent the item for the period //COPY:ALL
        TransActionAdapter transAction = new TransActionAdapter(rentEz, //COPY:ALL
                new ClientTransaction(rentEz,startDate,staff,client)); //COPY:ALL
        Money cost = transAction.rentFor(count,rentalItemName,paidDuration); //COPY:ALL
        transAction.payWithCashDollar(cost); //COPY:ALL
        transAction.completeTransaction(); //COPY:ALL
        //COPY:ALL
        // Use a transaction to return the item after the required delay //COPY:ALL
        TransActionAdapter finalTransAction = new TransActionAdapter(rentEz, //COPY:ALL
                new ClientTransaction(rentEz,endDate,staff,client)); //COPY:ALL
        Money refund = finalTransAction.returnItems(count,rentalItemName); //COPY:ALL
        finalTransAction.refundCashDollar(refund); //COPY:ALL
        finalTransAction.completeTransaction(); //COPY:ALL
        return refund; //COPY:ALL
    } //COPY:ALL
} //COPY:ALL
