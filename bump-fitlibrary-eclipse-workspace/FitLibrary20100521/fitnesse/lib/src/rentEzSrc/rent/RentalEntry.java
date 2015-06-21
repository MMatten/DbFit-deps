/*
 * @author Rick Mugridge on Nov 7, 2004
 * Released under the terms of the GNU General Public License version 2 or later.
 *
 */
package rent;

import rps.RentEz;
import rps.Rental;
import rps.RentalItemType;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;

public class RentalEntry  { //COPY:ALL
    private Client client; //COPY:ALL
    private RentEz rentEz; //COPY:ALL
    private StaffMember staff; //COPY:ALL
    //COPY:ALL
    public RentalEntry(RentEz rentEz, //COPY:ALL
            StaffMember staff, Client client) { //COPY:ALL
        this.rentEz = rentEz; //COPY:ALL
        this.staff = staff; //COPY:ALL
        this.client = client; //COPY:ALL
    } //COPY:ALL
    public void rentalItemCountStartDateEndDate(String rentalItem,//COPY:ALL
            int count, MyDate startDate, MyDate endDate) { //COPY:ALL
        Duration duration = startDate.durationTo(endDate); //COPY:ALL
        RentalItemType hireItemType = rentEz.getRentalItemType(rentalItem); //COPY:ALL
        Rental rental = new Rental(hireItemType,count,startDate, //COPY:ALL
        		duration,client,staff); //COPY:ALL
        client.addRental(rental); //COPY:ALL
    } //COPY:ALL
} //COPY:ALL
