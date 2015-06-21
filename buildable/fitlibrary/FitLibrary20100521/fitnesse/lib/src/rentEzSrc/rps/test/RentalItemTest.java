package rps.test;

import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;
import rps.Rates;
import rps.Rental;
import rps.RentalItem;
import rps.RentalItemType;
import rps.Reservation;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;

public class RentalItemTest extends TestCase {

	Client person;
	RentalItemType cup;
	MyDate date;

	StaffMember staff;

	Reservation reservation;
	RentalItem item;
	Rental rental;
	
	protected void setUp() throws Exception {
		super.setUp();
		Rates rates;
		rps.paymentMethod.Money m1 = new rps.paymentMethod.Money(0.05);
		rps.paymentMethod.Money m2 = new rps.paymentMethod.Money(0.45);
		rps.paymentMethod.Money m3 = new rps.paymentMethod.Money(2.00);
		cup = new RentalItemType("cup", 10,
				new Rates(m1, m2, m3), new rps.paymentMethod.Money(), 0);
		date = new MyDate(new Date(2));
		Duration duration = new Duration(0, 1, 0);

		staff = new StaffMember(null, "Admin", "");
		person = new Client(staff, "Customer", "123");
		reservation = new Reservation(cup, 2, date, duration);
		rental = new Rental(cup,1, new MyDate(new Date(2)), new Duration(0, 3, 0), person, staff);
		reservation.hireItems(rental, person, staff);
		
		for (Iterator it = reservation.getHireItems(); it.hasNext(); ) {
			item = (RentalItem)it.next();
		}		
	}

	public void testDoHire() {
		item.returnHire();
		item.doHire(rental,reservation, person, staff);
		assertEquals(item.getCurrentRental(), rental);		
	}

	public void testCancelHireAndReturnHire() {
		item.returnHire();
		item.doHire(rental,reservation, person, staff);
		assertTrue(item.getCurrentRental()!= null);
		item.returnHire();
		assertEquals(item.getCurrentRental(), null);
	}

	public void testGetHireItemTypeName() {
		assertEquals(item.getHireItemTypeName(),"cup");
	}

	public void testGetIdentifier() {
		//returns the 1st item in the reservation (current reservation : cup8, cup9)
		assertEquals(item.getIdentifier(),"cup8");
	}

	public void testHasHireToReturn() {	
		item.returnHire();
		assertFalse(item.hasHireToReturn());
		item.doHire(rental,reservation, person, staff);
		assertTrue(item.hasHireToReturn());
	}

	public void testRepairAndIsBeingRepaired() {
		assertFalse(item.isBeingRepaired());
		item.repair();
		assertTrue(item.isBeingRepaired());
	}

	public void testCompleteRepair() {
		assertFalse(item.isBeingRepaired());
		item.repair();
		assertTrue(item.isBeingRepaired());
		item.completeRepair(new MyDate(new Date(1)));
		assertFalse(item.isBeingRepaired());
		assertEquals(new MyDate(new Date(1)),item.getLastMaintained());
	}

	public void testIsFree() {
		item.doHire(rental,reservation, person, staff);
		assertFalse(item.getCurrentRental()==null);
		item.repair();
		assertFalse(item.isFree());
		item.returnHire();
		assertFalse(item.isFree());
		assertTrue(item.isBeingRepaired());
		item.completeRepair(new MyDate(new Date(1)));
		assertFalse(item.isBeingRepaired());
		assertTrue(item.getCurrentRental()==null);
		assertTrue(item.isFree());
	}


	public void testNeedsMaintenance() {
		assertFalse(item.needsMaintenance(new MyDate(new Date(2))));
		item.completeRepair(new MyDate(new Date(2)));
		assertFalse(item.needsMaintenance(new MyDate(new Date(1))));
		assertTrue(item.needsMaintenance(new MyDate(new Date(2))));
	}

	public void testGetLastMaintained() {
		assertEquals(item.getLastMaintained(), null);
		item.completeRepair(new MyDate(new Date(2)));
		assertEquals(item.getLastMaintained(), new MyDate(new Date(2)));
	}

	public void testHasMaintenanceDate() {
		assertFalse(item.hasMaintenanceDate());
		item.completeRepair(new MyDate(new Date(2)));
		assertTrue(item.hasMaintenanceDate());
	}

	public void testGetStartDate() {
		assertEquals(item.getStartDate(), rental.getStartDate());
	}

	public void testGetEndDate() {
		assertEquals(item.getEndDate(), rental.getEndDate());
	}

}
