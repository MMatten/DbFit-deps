package rent;

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

    public boolean rentDotStartApplication() {
    	throw new RuntimeException();
    }
    public SetUpFixture setup() {
        return setUp;
    }
	public Fixture applyRestrictions(){
		return setup();
	}
    public Fixture enterStaff() {
        return setup();
    }
    public Set<StaffMember> staffList() {
        Set<StaffMember> staffMembers = new HashSet<StaffMember>();
        for (StaffMember staff : rentEz.getStaffMembers()) {
            if (!(staff.getName().equals("Admin")))
                staffMembers.add(staff);
        }
        return staffMembers;
    }
    public Fixture enterClients() {
        return setup();
    }
    public Set<Client> clientList() {
        return rentEz.getClients();
    }
    public ClientAdapter client(String clientName) throws MissingException {
        return new ClientAdapter(getClient(clientName));
    }
    public Fixture enterRentalItemTypes() {
        return setup();
    }
    public Set<RentalItemType> rentalItemList() {
        return new HashSet<RentalItemType>(rentEz.getRentalItemTypes().values());
    }
    public SetFixture salesItemList(){
		return new SetFixture(rentEz.getBuyItems());
	}
    public SubsetFixture rentalItemSubset() {
        return new SubsetFixture(rentalItemList());
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
        return new SubsetFixture(rentEz.getAllIdentifedHireItems());
    }
    public Fixture enterBuyItemTypes() {
        return setup();
    }
    public List<Rental> rentalsOfClient(String clientName) throws MissingException {
        return getClient(clientName).getRentals();
    }
    public TransActionAdapter beginTransactionForClientStaff(String clientName,
            String staffMemberName) throws MissingException {
        ClientTransaction transaction =
            rentEz.beginClientTransaction(clientName,staffMemberName);
        return new TransActionAdapter(rentEz,transaction);
    }
	
    public TransActionAdapter resumeTransactionForClient(String clientName) {
        ClientTransaction transaction =
            rentEz.resumeClientTransaction(clientName);
        return new TransActionAdapter(rentEz,transaction);
    }	
	
    public AdminTransAction resumeAdminTransactionFor(String staffName) throws Exception {
        AdminTransaction transaction =
            rentEz.resumeAdminTransaction(staffName);
        return new AdminTransAction(transaction,rentEz);
    }	
    public AdminTransAction beginAdminTransaction(String staffMemberName) throws Exception {
        return new AdminTransAction(rentEz.beginAdminTransaction(staffMemberName),rentEz);
    }
    public boolean timeIsNow(MyDate time) {
        mockClock.setTime(time);
		//check for any maintenance needed, and remove those items for maintenance
		//in the real application this should be called automatically somehow
		rentEz.forMaintenance();
        return true;
    }
    public Set<RentalItem> forMaintenanceList() {
        return rentEz.forMaintenance();
    }
    public SetFixture clientBookingList(String clientName) throws MissingException {
        return new SetFixture(getClient(clientName).getBookings());
    }
    public SubsetFixture salesGoodsSubset() {
        return new SubsetFixture(rentEz.getBuyItems());
    }
    public CalculateFixture calculateChargeFairly() {
    	return calculateFairCharges();
    }
	public CalculateFixture calculateFairCharges(){
		return calculateChargeFairly(new Money(),new Money(),new Money());
	}
    public CalculateFixture calculateChargeFairlyPerHourPerDay(Money perHour, Money perDay) {
        return calculateChargeFairly(perHour,perDay,new Money());
    }
    public CalculateFixture calculateChargeFairlyPerDayPerWeek(Money perDay, Money perWeek) {
        return calculateChargeFairly(new Money(),perDay,perWeek);
    }
    private CalculateFixture calculateChargeFairly(Money perHour, Money perDay, Money perWeek) {
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
			ClientTransaction clientTransaction= rentEz.beginClientTransaction(clientName,"Admin",start);
			Duration duration = start.durationTo(end);
            Money hireCost = clientTransaction.rent(count,rentEz.getRentalItemType(rentalItemName),duration);
	        clientTransaction.payWithCash(hireCost);
            if (!clientTransaction.complete())
                throw new RuntimeException("Unable to complete");
        }
    }
    public SetUpFixture rentalsForClient(String clientName) throws MissingException {
        return new SetUpFixture(new RentalEntry(rentEz,makeDummyStaff(),
                rentEz.getClient(clientName)));
    }
    public CalculateFixture refundDollarPerHourPerDayPerWeek(Money perHour,
            Money perDay, Money perWeek) throws Exception {
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
            rentEz.createStaffMember(rentEz.getAdminStaff(),
            		name,"phone");
            return rentEz.getStaffMember(name);
        }
    }
    private Client getClient(String clientName) throws MissingException {
        return rentEz.getClient(clientName);
    }
    public CreateTemplate createTemplate(String name){
        return new CreateTemplate(rentEz, name);
    }
	public Set<RentalItemType> rentalItem(){
		return rentalItemList();
	}
	public SetFixture transactionsPending(){		
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
//    	public Client getClient(){
//    		return transaction.getClient();
//    	}
    	public String getStaff(){
    		return transaction.getStaff();
    	}	
    	public Money getOwing(){
    		return transaction.getOwing();
    	}	
    }
	public SetFixture totalCommission(){		
		return new SetFixture(mapCommissionToAdapter());
	}		
	public double pointBalanceForClient(String clientName) throws MissingException{
		return getClient(clientName).getBonusPoints().getValueInDouble();
	}
	public static class BonusPointCalculation {
		public BonusPoints rentnzDollarActualDollarSpent(Money actual){
			return BonusPoints.bonusPointCalculation(actual);
		}
	}
	public CalculateFixture calculatedDiscount(){
		return calculate(new BonusPointCalculation());
	}
	public Money accountOwingForIs(String clientName) throws MissingException{
		return rentEz.getClient(clientName).getAmountOwing();
	}
	public boolean completeTransaction(){
		// no transaction is started, therefore no transaction is completed
		return false;
	}
	public SubsetFixture rentDotSalesGoodsSubset(){
		return new SubsetFixture(rentEz.getBuyItems());
	}
    public PermittedMixtureOfPayments permittedCombinationOfPayments() {
        return new PermittedMixtureOfPayments(rentEz);
    }
	public CalculateFixture validateCreditCard(){
		return calculate(new CreditCardValidationFixture(rentEz));
	}
	private CalculateFixture calculate(Object object) {
		return new CalculateFixture(object);
	}
	public Fixture validateEmail(){
		return calculate(new EmailValidationFixture());
	}
	public List<Delivery> deliveriesForClient(String clientName) throws MissingException {
		return rentEz.getClient(clientName).getDeliveries();
	}
	public List<Booking> bookingsForClientInDeliveryInCityInZoneInDeliveryAddressOn(String clientName, String city, String zone, String address, MyDate date) throws MissingException {
		return rentEz.getClient(clientName).getBookings(city,zone,address,date);
	}
	public DeliveryTransaction beginDeliveryTransactionForClientStaff(String clientName,String staffName) throws MissingException{
		ClientTransaction transaction = rentEz.beginClientTransaction(clientName,staffName);
		return new DeliveryTransaction(rentEz, transaction);
	}
	public DeliveryTransaction beginDeliveryTransactionForClientCityZoneAddressStaff(String clientName, String city, String zone, String address, String staffName) throws MissingException{
		ClientTransaction transaction = rentEz.beginClientTransaction(clientName,staffName);
		return new DeliveryTransaction(rentEz, transaction, city, zone, address);
	}
	public SetFixture deliveryCostList() {
		return new SetFixture(rentEz.getDeliveryRates());
	}
	public CalculateFixture calculatedDeliveryRate() {
		return new CalculateDeliveryRate(rentEz);
	}
	public String showClient(Client client) { 
		return client.getName();
	}
}
