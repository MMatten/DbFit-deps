package rent;

import static rent.Generator.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rps.Booking;
import rps.Delivery;
import rps.Rates;
import rps.RentEz;
import rps.Rental;
import rps.RentalItem;
import rps.RentalItemType;
import rps.exception.MissingException;
import rps.exception.RpsException;
import rps.paymentMethod.BonusPoints;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Duration;
import rps.time.MyDate;
import rps.transaction.AdminTransaction;
import rps.transaction.ClientTransaction;
import fit.Fixture;
import fitlibrary.CalculateFixture;
import fitlibrary.DoFixture;
import fitlibrary.SetFixture;
import fitlibrary.SetUpFixture;
import fitlibrary.SubsetFixture;

public class StartApplication extends DoFixture {
    private final MockClock mockClock = new MockClock();
    protected final RentEz rentEz = new RentEz(mockClock);
    private SetUpFixture setUp = new SetUpFixture(new GeneralSetUp(rentEz));
    
    public StartApplication() {
    	start();
    	act("!-rent.RentEzWeb-!");
	}
    public void tearDown() {
    	writeTo(getRuntimeContext().getCurrentPageName());
    }
    
    public boolean rentDotStartApplication() {
    	throw new RuntimeException();
    }
    public SetUpFixture setup() {
    	header("Entry");
    	click("enter inventory staff clients");
        return setUp;
    }
	public Fixture applyRestrictions(){
	   	notCovered();
		return setup();
	}
    public Fixture enterStaff() {
       	notCovered();
       return setup();
    }
    public Set<StaffMember> staffList() {
       	notCovered();
        Set<StaffMember> staffMembers = new HashSet<StaffMember>();
        for (StaffMember staff : rentEz.getStaffMembers()) {
            if (!(staff.getName().equals("Admin")))
                staffMembers.add(staff);
        }
        return staffMembers;
    }
    public Fixture enterClients() {
       	notCovered();
        return setup();
    }
    public Set<Client> clientList() {
    	header("Current clients");
    	click("current clients");
    	checkTable(this,"current clients table");
        return rentEz.getClients();
    }
    public ClientAdapter client(String clientName) throws MissingException {
       	notCovered();
        return new ClientAdapter(getClient(clientName));
    }
    public Fixture enterRentalItemTypes() {
       	notCovered();
        return setup();
    }
    public Set<RentalItemType> rentalItemList() {
    	header("Check remaining rental items in inventory");
    	click("show inventory");
    	checkTable(this,"fullInventory");
       return rentalItemListPrivate();
    }
	public Set<RentalItemType> rentalItem(){
		return rentalItemList();
	}
    public Set<RentalItemType> rentalItemListPrivate() {
       return new HashSet<RentalItemType>(rentEz.getRentalItemTypes().values());
    }
    public SetFixture salesItemList(){
    	header("Show sales inventory");
    	click("show sales inventory");
    	checkTable(this,"SalesInventory");
		return new SetFixture(rentEz.getBuyItems());
	}
    public SubsetFixture rentalItemSubset() {
    	header("Check remaining rental items in inventory");
    	click("show inventory");
    	checkTableSubset(this,"fullInventory");
        return new SubsetFixture(rentalItemListPrivate());
    }
    private List<CommissionAdapter> mapCommissionToAdapter() {
        List<CommissionAdapter> commission = new ArrayList<CommissionAdapter>();
		for (StaffMember staffMember :  rentEz.getStaffMembers()){
			if (staffMember.getName() == "Admin")
				continue;
			commission.add(new CommissionAdapter(staffMember));
        }
        return commission;
    }	
    public SubsetFixture identifiedRentalItemSubset() {
    	header("Check remaining identified rental items in inventory");
    	click("show identified inventory");
    	checkTableSubset(this,"fullInventory");
       return new SubsetFixture(rentEz.getAllIdentifedHireItems());
    }
    public Fixture enterBuyItemTypes() {
       	notCovered();
        return setup();
    }
    public List<Rental> rentalsOfClient(String clientName) throws MissingException {
    	header("Current rentals for client");
    	click("current rentals");
    	withAddText("client name",clientName);
    	click("show rentals");
    	checkTable(this,"current rentals table");
    	return getClient(clientName).getRentals();
    }
    public TransActionAdapter beginTransactionForClientStaff(String clientName,
            String staffMemberName) throws MissingException {
    	transactionGen(staffMemberName,clientName);
        ClientTransaction transaction =
            rentEz.beginClientTransaction(clientName,staffMemberName);
        return new TransActionAdapter(rentEz,transaction);
    }
    protected void transactionGen(String staffMemberName, String clientName) {
    	header("Transaction for client");
    	loginStaff(staffMemberName);
    	selectClient(clientName);
    }
	private void selectClient(String clientName) {
		withAddText("client name",clientName);
    	click("client select");
    	titleIs("Client: "+clientName);
	}
	private void loginStaff(String staffMemberName) {
		click("login button");
    	titleIs("staff login");
    	withAddText("staff name",staffMemberName);
    	click("staff login");
	}
    public TransActionAdapter resumeTransactionForClient(String clientName) {
    	header("Resume transaction for client");
    	loginStaff("Admin");
    	selectClient(clientName);
        ClientTransaction transaction = rentEz.resumeClientTransaction(clientName);
        return new TransActionAdapter(rentEz,transaction);
    }	
	
    public AdminTransAction resumeAdminTransactionFor(String staffName) throws Exception {
    	loginStaff(staffName);
        AdminTransaction transaction = rentEz.resumeAdminTransaction(staffName);
        return new AdminTransAction(transaction,rentEz);
    }	
    public AdminTransAction beginAdminTransaction(String staffMemberName) throws Exception {
    	header("Transaction for admininstration");
    	loginStaff(staffMemberName);
        return new AdminTransAction(rentEz.beginAdminTransaction(staffMemberName),rentEz);
    }
    public boolean timeIsNow(MyDate time) {
    	header("Set date/time");
    	withAddText("current date time",""+time);
    	click("set date time");
        mockClock.setTime(time);
		//check for any maintenance needed, and remove those items for maintenance
		//in the real application this should be called automatically somehow
		rentEz.forMaintenance();
        return true;
    }
    public Set<RentalItem> forMaintenanceList() {
       	notCovered();
        return rentEz.forMaintenance();
    }
    public SetFixture clientBookingList(String clientName) throws MissingException {
    	header("Current bookings for client");
    	click("current bookings");
    	withAddText("client name",clientName);
    	click("show bookings");
    	checkTable(this,"current bookings table");
        return new SetFixture(getClient(clientName).getBookings());
    }
    public SubsetFixture salesGoodsSubset() {
    	header("Current Sales");
    	click("show current sales");
    	checkTableSubset(this,"current bookings table");
        return new SubsetFixture(rentEz.getBuyItems());
    }
    public CalculateFixture calculateChargeFairly() {
       	notCovered();
    	return calculateFairCharges();
    }
	public CalculateFixture calculateFairCharges(){
	   	notCovered();
		return calculateChargeFairly(new Money(),new Money(),new Money());
	}
    public CalculateFixture calculateChargeFairlyPerHourPerDay(Money perHour, Money perDay) {
       	notCovered();
        return calculateChargeFairly(perHour,perDay,new Money());
    }
    public CalculateFixture calculateChargeFairlyPerDayPerWeek(Money perDay, Money perWeek) {
       	notCovered();
        return calculateChargeFairly(new Money(),perDay,perWeek);
    }
    private CalculateFixture calculateChargeFairly(Money perHour, Money perDay, Money perWeek) {
       	notCovered();
        return calculate(new CalculateChargeFairly(
        		new Rates(perHour,perDay,perWeek)));
    }
    public SetUpFixture givenRentals(String name) throws Exception {
        return new SetUpFixture(new SetUpRentals(name));
    }
    public class SetUpRentals {
        private String clientName;
		
		public SetUpRentals(String clientName) throws Exception {
            this.clientName = clientName;
        }
        public void rentalItemCountStartDateEndDate(String rentalItemName, int count, MyDate start, MyDate end) throws Exception {
        	transactionGen("Admin",clientName);
        	header("Rent for");
        	click("rentals");
        	click(rentalItemName);
        	withAddText("rental count",""+count);
        	clickRadio("rental duration type startEnd");
        	withAddText("rental start",""+start);
        	withAddText("rental end",""+end);
        	click("rental completed");
			ClientTransaction clientTransaction= rentEz.beginClientTransaction(clientName,"Admin",start);
			Duration duration = start.durationTo(end);
            Money hireCost = clientTransaction.rent(count,rentEz.getRentalItemType(rentalItemName),duration);
        	header("Pay with cash");
        	clickRadio("pay with cash");
        	withAddText("cash amount",""+hireCost);
        	click("pay");
	        clientTransaction.payWithCash(hireCost);
            if (!clientTransaction.complete()) {
            	errorMessage("Unable to complete");
                throw new RuntimeException("Unable to complete");
            }
        }
    }
    public SetUpFixture rentalsForClient(String clientName) throws MissingException {
       	notCovered();
       return new SetUpFixture(new RentalEntry(rentEz,makeDummyStaff(),
                rentEz.getClient(clientName)));
    }
    public CalculateFixture refundDollarPerHourPerDayPerWeek(Money perHour,
            Money perDay, Money perWeek) throws Exception {
       	notCovered();
        StaffMember staff = makeDummyStaff();
        Client client = makeDummyClient();
        RentalItemType item = makeDummyRentalItem(
                new Rates(perHour, perDay, perWeek));
        return calculate(new Refunder(rentEz,staff,client,item.getName()));
    }
    private RentalItemType makeDummyRentalItem(Rates rates) {
        final String name = "dummy-rental";
        final int count = 1;
        final Money bond = new Money(0);
        rentEz.removeRentalItemType(name);
        rentEz.createRentalItemType(name,count,rates,bond);
        return rentEz.getRentalItemType(name);
    }
	private Client makeDummyClient() throws RpsException {
		final String name = "dummy-client";
		try {
			return getClient(name);
		} catch (MissingException e) {
			rentEz.createClient(rentEz.getAdminStaff(), name, "phone");
			return getClient(name);
		}
	}
    private StaffMember makeDummyStaff() throws MissingException {
       final String name = "dummy-staff";
        try {
            return rentEz.getStaffMember(name);
        } catch (MissingException e) {
            rentEz.createStaffMember(rentEz.getAdminStaff(),name,"phone");
            return rentEz.getStaffMember(name);
        }
    }
    private Client getClient(String clientName) throws MissingException {
        return rentEz.getClient(clientName);
    }
    public CreateTemplate createTemplate(String name){
       	header("Create Template");
        return new CreateTemplate(rentEz, name);
    }
	public SetFixture transactionsPending(){		
    	header("Check pending transactions");
    	click("pending transactions");
    	checkTable(this,"pending transactions");
		List<ClientTransactionsAdapter> transactions = new ArrayList<ClientTransactionsAdapter>();
		for (ClientTransaction transaction : rentEz.getPendingClientTransactions())
		    transactions.add(new ClientTransactionsAdapter(transaction));
		return new SetFixture(transactions);
	}
    public class ClientTransactionsAdapter {
        private ClientTransaction transaction;
    	
    	public ClientTransactionsAdapter(ClientTransaction transaction) {
    	    this.transaction = transaction;
    	}
    	public String getClient(){
    		return transaction.getClient().getName();
    	}
    	public String getStaff(){
    		return transaction.getStaff();
    	}	
    	public Money getOwing(){
    		return transaction.getOwing();
    	}	
    }
	public SetFixture totalCommission(){		
    	header("Check staff commission totals");
    	click("view staff commission totals");
    	checkTableSubset(this,"staffCommissionTotals");
		return new SetFixture(mapCommissionToAdapter());
	}		
	public double pointBalanceForClient(String clientName) throws MissingException{
    	header("Check point balance");
	   	click("select clients");
	   	click("client"+clientName);
		double valueInDouble = getClient(clientName).getBonusPoints().getValueInDouble();
		textOfIs("client points",""+valueInDouble);
		return valueInDouble;
	}
	public static class BonusPointCalculation {
		public BonusPoints rentnzDollarActualDollarSpent(Money actual){
			return BonusPoints.bonusPointCalculation(actual);
		}
	}
	public CalculateFixture calculateDiscount(){
		return calculate(new BonusPointCalculation());
	}
	public Money accountOwingForIs(String clientName) throws MissingException{
	   	header("Check amount owing");
	   	click("select clients");
	   	click("client"+clientName);
		Money amountOwing = rentEz.getClient(clientName).getAmountOwing();
		textOfIs("client owing amount",""+amountOwing);
		return amountOwing;
	}
	public boolean completeTransaction(){
		// no transaction is started, therefore no transaction is completed
	   	notCovered();
		return false;
	}
	public SubsetFixture rentDotSalesGoodsSubset(){
	   	notCovered();
		return new SubsetFixture(rentEz.getBuyItems());
	}
    public PermittedMixtureOfPayments permittedCombinationOfPayments() {
       	notCovered();
       return new PermittedMixtureOfPayments(rentEz);
    }
	public CalculateFixture validateCreditCard(){
	   	notCovered();
		return calculate(new CreditCardValidationFixture(rentEz));
	}
	private CalculateFixture calculate(Object object) {
	   	notCovered();
		return new CalculateFixture(object);
	}
	public Fixture validateEmail(){
	   	notCovered();
		return calculate(new EmailValidationFixture());
	}
	public List<Delivery> deliveriesForClient(String clientName) throws MissingException {
    	header("Delivery for client");
    	click("show deliveries for client");
    	checkTable(this,"Deliveries for client table");
		return rentEz.getClient(clientName).getDeliveries();
	}
	public List<Booking> bookingsForClientInDeliveryInCityInZoneInDeliveryAddressOn(String clientName, String city, String zone, String address, MyDate date) throws MissingException {
	   	notCovered();
		return rentEz.getClient(clientName).getBookings(city,zone,address,date);
	}
	public DeliveryTransaction beginDeliveryTransactionForClientStaff(String clientName,String staffName) throws MissingException{
    	header("Delivery Transaction");
    	click("delivery transaction");
    	loginStaff(staffName);
    	selectClient(clientName);
		ClientTransaction transaction = rentEz.beginClientTransaction(clientName,staffName);
		return new DeliveryTransaction(rentEz, transaction);
	}
	public DeliveryTransaction beginDeliveryTransactionForClientCityZoneAddressStaff(String clientName, String city, String zone, String address, String staffName) throws MissingException{
    	header("Delivery Transaction");
    	click("delivery transaction");
    	loginStaff(staffName);
    	selectClient(clientName);
    	click("add delivery details");
    	withAddText("city",city);
    	withAddText("address",address);
    	withAddText("zone",zone);
    	click("add");
		ClientTransaction transaction = rentEz.beginClientTransaction(clientName,staffName);
		return new DeliveryTransaction(rentEz, transaction, city, zone, address);
	}
	public SetFixture deliveryCostList() {
    	header("Delivery cost list");
    	click("show delivery cost list");
    	checkTable(this,"Delivery cost table");
		return new SetFixture(rentEz.getDeliveryRates());
	}
	public CalculateFixture calculatedDeliveryRate() {
	   	notCovered();
		return new CalculateDeliveryRate(rentEz);
	}
	public String showClient(Client client) { 
	   	notCovered();
		return client.getName();
	}
}
