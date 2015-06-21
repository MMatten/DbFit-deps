package rps.test;

import java.util.Date;

import junit.framework.TestCase;
import rps.Booking;
import rps.Rates;
import rps.RentEz;
import rps.Rental;
import rps.RentalItemType;
import rps.Reservation;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.AdminTransaction;

public class TestRemoveForMaintenance extends TestCase {
    RentEz rentEz;
	
	
	Booking booking;
	Client person;
	RentalItemType cup;
	private MyDate date;
	private MyDate date2;
	StaffMember staff;

	Reservation reservation;
	Rental rental;
	String[] temp;
	Duration duration;
	AdminTransaction trans;

	//this test not finished, doesnt do anything
	protected void setUp() throws Exception {
		super.setUp();
        rentEz = new RentEz();
        rentEz.createStaffMember(null,"John","5551234");
        StaffMember staff = rentEz.getStaffMember("John");
        rentEz.createClient(staff,"Bob","55554354");
        Client client = rentEz.getClient("Bob");
        Money m1 = new Money(0.05);
        Money m2 = new Money(0.45);
        Money m3 = new Money(2.00);
        rentEz.createRentalItemType("cup",10,new Rates(m1,m2,m3),new Money());
        RentalItemType itemType = rentEz.getRentalItemType("cup");
		
		
		date=new MyDate(new Date("2004/05/06 09:00"));
		trans= new AdminTransaction(rentEz,date,staff);
		trans.addIdentifiedRentalItem("10","lespaul",date,2.0);
		trans.addIdentifiedRentalItem("11","strat",date,10.0);
		trans.addIdentifiedRentalItem("12","sg",date,1.0);
		//trans.addRentalItemType()
		date2=new MyDate(new Date("2004/09/06 09:00"));
		temp = rentEz.getItemsNeedingRepair(date2);
		
	}
	
	public void testGetItemsNeedingRepair() {
			//assertEquals(temp[0],"strat");
	}

	public void testRemoveAnyItemsRequiringMaintenance() {
	}

}
