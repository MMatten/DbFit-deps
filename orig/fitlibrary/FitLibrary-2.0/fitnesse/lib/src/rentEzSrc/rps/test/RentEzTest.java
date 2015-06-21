package rps.test;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;
import rps.BuyItemType;
import rps.Rates;
import rps.RentEz;
import rps.RentalItemType;
import rps.exception.DuplicateException;
import rps.exception.MissingException;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.SystemClock;

public class RentEzTest extends TestCase {
	
	RentEz rent;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rent = new RentEz();
	}
	
	public void testGetTime() {
		SystemClock clock = new SystemClock();
		assertEquals(rent.getTime(),clock.getTime());
	}

	/*
	 * Class under test for void createStaffMember(StaffMember, String, String)
	 */
	@SuppressWarnings("null")
	public void testCreateStaffMemberStaffMemberStringString() {
		assertEquals("There should only be the admin staff member created",1,rent.getStaffMembers().size());
		rent.createStaffMember(null,"Alex","1548916");
		assertEquals(2,rent.getStaffMembers().size());
		try {
			rent.createStaffMember(rent.getStaffMember("Alex"),"Jon","5564861");
		} catch (MissingException e) {
			fail(e.toString());
		}
		try{
			rent.createStaffMember(null,"Alex","1548916");
			fail("Creating a duplicate staff member should cause a runtime exception");
		}catch(RuntimeException e){
			//
		}
		StaffMember jon = null;
		try {
			jon = rent.getStaffMember("Jon");
			assertEquals(jon.getCreator(),rent.getStaffMember("Alex"));
		} catch (MissingException e) {
			fail(e.toString());
		}
		assertEquals(jon.getPhone(),"5564861");
		assertEquals(jon.getName(),"Jon");
	}

	/*
	 * Class under test for void createStaffMember(StaffMember, String, String, float)
	 */
	public void testCreateStaffMemberStaffMemberStringStringfloat() {
		rent.createStaffMember(null,"Alex","1548916",5.62);
		StaffMember alex = null;
		try {
			 alex = rent.getStaffMember("Alex");
		} catch (MissingException e) {
			fail(e.toString());
		}
		assertEquals(alex,new StaffMember(null,"Alex","1548916",5.62));	
	}

	/*
	 * Class under test for void createStaffMember(StaffMember, String, String, float, float)
	 */
	public void testCreateStaffMemberStaffMemberStringStringfloatfloat() {
		rent.createStaffMember(null,"Jon","6648161",5.44,165.2);
		StaffMember jon = null;
		try {
			jon = rent.getStaffMember("Jon");
		} catch (MissingException e) {
			fail(e.toString());
		}
		assertEquals(jon,new StaffMember(null,"Jon","6648161",5.44,165.2));
	}

	public void testCreateClient() {
		StaffMember jon = new StaffMember(null,"Jon","6648161");
		try {
			rent.createClient(jon,"Test Client","4446846");
		} catch (DuplicateException e) {
			fail(e.toString());
		}
		try{
			rent.createClient(jon,"Test Client","4446846");
			fail("Creating a duplicate client should throw a duplicate exception");
		}catch(DuplicateException e){
			//
		}
		Client testClient = null;
		try{
			testClient = rent.getClient("Test Client");
		}catch(MissingException e){
			fail(e.toString());
		}
		assertEquals(testClient,new Client(jon,"Test Client","4446846"));
	}

	public void testGetStaffMember() {
		try {
			rent.getStaffMember("No Staff");
			fail("Getting a staff member that doesn't exist should throw a missing exception");
		} catch (MissingException e) {
			//
		}
	}

	public void testCreateRentalItemTypeStringintRatesMoney() {
		rent.createRentalItemType("Bike",1,new Rates(new Money(100),new Money(2400),new Money(16800)),new Money(1000));
		RentalItemType bike = rent.getRentalItemType("Bike");
		assertEquals(bike,new RentalItemType("Bike",1,new Rates(new Money(100),new Money(2400),new Money(16800)),new Money(1000)));
	}

	public void testCreateRentalItemTypeStringintRatesMoneyint() {
		rent.createRentalItemType("Bike",1,new Rates(new Money(100),new Money(2400),new Money(16800)),new Money(1000),10);
		RentalItemType bike = rent.getRentalItemType("Bike");
		assertEquals(bike,new RentalItemType("Bike",1,new Rates(new Money(100),new Money(2400),new Money(16800)),new Money(1000),10));
	}

	public void testCreateBuyItemType() {
		try {
			rent.createBuyItemType("Item",1234,new Money(1000));
			rent.createBuyItemType("Item2",1235,new Money(1100));
		} catch (DuplicateException e) {
			fail(e.toString());
		}
		try {
			rent.createBuyItemType("Item",1234,new Money(1000));
			fail("Creating a duplicate BuyItemType should throw a duplicate exception");
		} catch (DuplicateException e) {
			//
		}
		Collection<BuyItemType> items = rent.getBuyItems();
		Iterator<BuyItemType> it = items.iterator();
		assertEquals(it.next(),new BuyItemType("Item",1234,new Money(1000)));
		assertEquals(it.next(),new BuyItemType("Item2",1235,new Money(1100)));
		assertFalse(it.hasNext());
	}

	@SuppressWarnings("unchecked")
	public void testGetRentalItemTypes() {
		rent.createRentalItemType("Bike",1,new Rates(new Money(100),new Money(2400),new Money(16800)),new Money(1000),10);
		rent.createRentalItemType("Bicycle",1,new Rates(new Money(100),new Money(2400),new Money(16800)),new Money(1000),10);
		Collection temp = rent.getRentalItemTypes().values();
		assertEquals(temp.size(),2);
		assertTrue(temp.contains(new RentalItemType("Bike",1,new Rates(new Money(100),new Money(2400),new Money(16800)),new Money(1000),10)));
		assertTrue(temp.contains(new RentalItemType("Bicycle",1,new Rates(new Money(100),new Money(2400),new Money(16800)),new Money(1000),10)));
	}
}
