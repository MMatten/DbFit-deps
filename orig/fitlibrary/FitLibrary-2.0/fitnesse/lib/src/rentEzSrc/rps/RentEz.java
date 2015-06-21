/*
 * Created on May 7, 2004
 *
 */
package rps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rps.exception.DuplicateException;
import rps.exception.MissingException;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.Clock;
import rps.time.MyDate;
import rps.time.SystemClock;
import rps.transaction.AdminTransaction;
import rps.transaction.ClientTransaction;


/**
 * The main application code
 */
public class RentEz {
	private Clock clock;
	private Map<String,StaffMember> staffMembers = new Hashtable<String,StaffMember>();
	private Map<String,Client> clients = new Hashtable<String,Client>();
	private Map<String,RentalItemType> rentalItemTypes = new Hashtable<String,RentalItemType>();
	private Map<String,BuyItemType> buyItemTypes = new Hashtable<String,BuyItemType>();
    private Map<String,Template> rentalTemplates = new Hashtable<String,Template>();
	private Map<String,ItemRestriction> itemRestrictions = new Hashtable<String,ItemRestriction>();
	private Map<String,ClientTransaction> pendingClientTransactions = new Hashtable<String,ClientTransaction>();
	private Map<String,AdminTransaction> pendingAdminTransactions = new Hashtable<String,AdminTransaction>();
    private List<DeliveryRate> deliveryRates = new ArrayList<DeliveryRate>();

    public RentEz(Clock clock)  {
	    this.clock = clock;
	    initialise();
	}
	public RentEz()  {
	    clock = new SystemClock();
		initialise();
	}
	private void initialise()  {
		createStaffMember(null, "Admin", "");
	}
	public MyDate getTime() {
		return clock.getTime();
	}
	public void createStaffMember(StaffMember creator, String name, String phone) {
		addStaffMember(new StaffMember(creator, name, phone));
	}
	public void createStaffMember(StaffMember creator, String name, String phone, double commission) {
		addStaffMember(new StaffMember(creator, name, phone, commission));
	}	
	public void createStaffMember(StaffMember creator, String name, String phone, double commission, double discountRate) {
		addStaffMember(new StaffMember(creator, name, phone, commission, discountRate));
	}	
	public void createClient(StaffMember creator, String name, String phone) throws DuplicateException {
		addClient(new Client(creator, name, phone));
	}
    public void createClient(StaffMember creator, String name, String phone, String city, String zone, String deliveryAddr) throws DuplicateException {
        addClient(new Client(creator, name, phone, city, zone, deliveryAddr));
    }
	public DeliveryRate getDeliveryRate(String city, String zone) {
        for (DeliveryRate rate : deliveryRates) {
            if(rate.getCity().equals(city) && rate.getZone().equals(zone))
                return rate;
        }
        return null;
    }
	public DeliveryRate getDeliveryRate(Client client) {
        for (DeliveryRate rate : deliveryRates) {
            if(rate.getCity().equals(client.getCity()) && rate.getZone().equals(client.getZone()))
                return rate;
        }
        return null;
    }
	private DeliveryRate getDeliveryRate(String city, String zone, double flatrate, double deliveryrate) {
        for (DeliveryRate rate : deliveryRates) {
            if(rate.getCity().equals(city) && rate.getZone().equals(zone) && rate.getDeliveryRateFlatFee() == flatrate && rate.getDeliveryRatePercent() == deliveryrate)
                return rate;
        }
        return null;
    }
    private void addStaffMember(StaffMember staffMember) {
		if(staffMembers.containsKey(staffMember.getName())) {
			throw new RuntimeException("Staff Member " + staffMember.getName() + " already exists");
		}
		staffMembers.put(staffMember.getName(), staffMember);
	}
	public StaffMember getStaffMember(String staffName) throws MissingException {
		StaffMember staff = staffMembers.get(staffName);
		if (staff == null)
		    throw new MissingException("Unknown staff member: "+staffName);
       return staff;
	}
	public boolean addClient(Client client) throws DuplicateException {
		if(clients.containsKey(client.getName())) {
			throw new DuplicateException("Client " + client.getName() + " already exists");
		}
		clients.put(client.getName(), client);
		return true;
	}
	public void createRentalItemType(String name, int initialCount, Rates rates, Money bond) {
		addRentalItemType(new RentalItemType(name, initialCount, rates, bond));
	}
	public void createRentalItemType(String name, int initialCount, Rates rates, Money bond, int minAge) {
		addRentalItemType(new RentalItemType(name, initialCount, rates, bond, minAge));
	}
	public void createBuyItemType(String name, int initialHash, Money sellingPrice) throws DuplicateException {
		addBuyItemType(new BuyItemType(name, initialHash, sellingPrice));
	}
	private void addBuyItemType(BuyItemType type) throws DuplicateException {
		if(buyItemTypes.containsKey(type.getName())) {
			throw new DuplicateException("BuyItemType " + type.getName() + " already exists");
		}
		buyItemTypes.put(type.getName(), type);		
	}
	private void addRentalItemType(RentalItemType type) {
		if(rentalItemTypes.containsKey(type.getName())) {
			throw new RuntimeException("HireItemType " + type.getName() + " already exists");
		}
		rentalItemTypes.put(type.getName(), type);
	}
	public Map<String,RentalItemType> getRentalItemTypes(){
		return rentalItemTypes;
	}
	public void setRentalItemTypes(List<RentalItemType> rentalItemTypes) {
		this.rentalItemTypes.clear();
		for (RentalItemType rental : rentalItemTypes)
			addRentalItemType(rental);
	}
	public Set<Client> getClients(){
		return new HashSet<Client>(clients.values());
	}
	public void setClients(List<Client> theClients) {
		for (Client client : theClients)
			clients.put(client.getName(),client);
	}
	public Collection<StaffMember> getStaffMembers(){
		return staffMembers.values();
	}
	public void setStaffMembers(List<StaffMember> theStaff) {
		for (StaffMember staff : theStaff)
			staffMembers.put(staff.getName(),staff);
	}
	public Collection<BuyItemType> getBuyItems() {
		return buyItemTypes.values();
	}
	public Collection<BuyItemType> getBuyItemList(){
		return buyItemTypes.values();
	}
	public Client getClient(String clientName) throws MissingException {
		Client client = clients.get(clientName);
		if (client == null)
		    throw new MissingException("Unknown client: "+clientName);
        return client;
	}
	public AdminTransaction beginAdminTransaction(String member) throws Exception {
		StaffMember staffMember = getStaffMember(member);
        AdminTransaction transaction =  new AdminTransaction(this,clock.getTime(),staffMember);
		pendingAdminTransactions.put(member, transaction);
		return transaction;
	}
	public RentalItemType getRentalItemType(String hireItemTypeName) {
		RentalItemType rentalItemType = rentalItemTypes.get(hireItemTypeName);
        if (rentalItemType == null)
            throw new RuntimeException("Unknown rental: "+hireItemTypeName);
        return rentalItemType;
	}
    public ClientTransaction beginClientTransaction(String clientName, 
            String staffName, MyDate startDate, boolean deliver) throws MissingException {
        ClientTransaction transaction =  new ClientTransaction(this,startDate,getStaffMember(staffName),getClient(clientName), deliver);
        pendingClientTransactions.put(clientName, transaction);
        return transaction;
    }
    public ClientTransaction beginTransactionForClientStaff(String clientName, 
			String staffName) throws MissingException {
    	return beginClientTransaction(clientName,staffName);
    }
	public ClientTransaction beginClientTransaction(String clientName, 
			String staffName) throws MissingException {
		return beginClientTransaction(clientName, 
				staffName, clock.getTime());
	}
	
	public ClientTransaction beginClientTransaction(String clientName, 
			String staffName, MyDate startDate) throws MissingException {
        return beginClientTransaction(clientName, 
                staffName, startDate, false);
	}
	
	public boolean containsHireItemTypeAnywhere(String type){
		boolean contains = rentalItemTypes.containsKey(type);
		if (contains == false){
			for (Iterator<AdminTransaction> it = getPendingAdminTransactions(); it.hasNext(); ){
				AdminTransaction transaction = it.next();
				contains = transaction.checkHireItemExistInTrans(type);
				if (contains == true)
					break;
			}
		}
		return contains;
	}

	public boolean containsHireItemType(String type) {
		return rentalItemTypes.containsKey(type);
	}

	public BuyItemType getSalesItemType(String buyItemType) throws Exception{		
		if (!buyItemTypes.containsKey(buyItemType)) {
			throw new Exception("No such" + buyItemType + "exists" );
		}
		return buyItemTypes.get(buyItemType);
	}

	public boolean removeForRepair(String type, int count) {
		return getRentalItemType(type).removeForRepair( count);
	}

	public boolean returnFromRepair(MyDate date, String type, int count) {
		return getRentalItemType(type).returnFromRepair(date, count);
	}

	//check for any maintenance needed, and remove those items for maintenance
	//in the real application this should be called automatically somehow, 
	//at the moment it is called when the test fixtures set the mock clock
	public Set<RentalItem> forMaintenance() {
		Set<RentalItem> itemsForRepair = new HashSet<RentalItem>();
		for (Iterator<RentalItemType> it = rentalItemTypes.values().iterator();it.hasNext();){
			RentalItemType hireItemType = it.next();
			hireItemType.forMaintenance(itemsForRepair, clock.getTime());			
		}
		return itemsForRepair;
	}

	public Iterator<RentalItem> getAllIdentifedHireItems() {
		List<RentalItem> identifiedHireItems = new ArrayList<RentalItem>();
		for (Iterator<RentalItemType> it = rentalItemTypes.values().iterator();it.hasNext();){
			RentalItemType hireItemType = it.next();
			
			for(Iterator<RentalItem> iter = hireItemType.getIdentifedHireItems(); iter.hasNext();){
				RentalItem hireItem = iter.next();
				if(hireItem.hasMaintenanceDate()) {
					identifiedHireItems.add(hireItem);
				}
			}		
		}
		return identifiedHireItems.iterator();
	}

	public RentalItemType rentalItemTypeFor(String itemIdentifier) {
		for(Iterator<RentalItemType> it = rentalItemTypes.values().iterator(); it.hasNext(); ) {
			RentalItemType hireItemType = it.next();
			if(hireItemType.hasIdentifiedItem(itemIdentifier)) {
				return hireItemType;
			}
		}
		return null;
	}
    public StaffMember getAdminStaff() throws MissingException {
        return getStaffMember("Admin");
    }
    public void removeRentalItemType(String name) {
        rentalItemTypes.remove(name);
    }
	public String[] getItemsNeedingRepair(MyDate date){
		ArrayList<String> items = new ArrayList<String>();
		for (Iterator<RentalItem> it = getAllIdentifedHireItems(); it.hasNext(); ){
			RentalItem item = it.next();
			if (item.needsMaintenance(date))
				items.add(item.getIdentifier());
		}
		String[] itemsString = new String[items.size()];
		items.toArray(itemsString);
		Arrays.sort(itemsString);
		return itemsString;
	}
    public Template addTemplate(String name) {
        Template t = new Template();
        rentalTemplates.put(name, t);
        return t;
    }
    public Template getTemplate(String name) {
        return rentalTemplates.get(name);
    }
	// jpe001
	public void createItemRestriction(int restrictID, String constraint) {
		addItemRestriction(new ItemRestriction(restrictID, constraint));
	}
	private void addItemRestriction(ItemRestriction restrict) {
		if(itemRestrictions.containsKey(restrict.getID())) {
			throw new RuntimeException("ItemRestriction ID " + restrict.getID() + " already exists");
		}
		itemRestrictions.put(restrict.getID(), restrict);
	}
	public Collection<ClientTransaction> getPendingClientTransactions() {
		return pendingClientTransactions.values();
	}
	public ClientTransaction resumeClientTransaction(String clientName) {
		ClientTransaction transaction = pendingClientTransactions.get(clientName);
		if (transaction == null){
			throw new RuntimeException("No Such Transaction");
		}
		if (transaction.getStaffMember().resumeClientTransaction(transaction)){
			return transaction;
		}
		return  null;
	}
	
	public Iterator<AdminTransaction> getPendingAdminTransactions() {
		return pendingAdminTransactions.values().iterator();
	}
	public void removePendingClientTransaction(String clientName) {
		pendingClientTransactions.remove(clientName);
	}
	public AdminTransaction resumeAdminTransaction(String adminName) {
		AdminTransaction transaction = pendingAdminTransactions.get(adminName);
		if (transaction == null){
			throw new RuntimeException("No Such Transaction");
		}
		if (transaction.getStaffMember().resumeAdminTransaction(transaction)){
			return transaction;
		}
		return  null;
	}
	public void removePendingAdminTransaction(String adminName) {
		pendingAdminTransactions.remove(adminName);
	}
	public void applyRestrictionToItem(String restrictID, String rentalItemName) {
		if(!itemRestrictions.containsKey(restrictID)) {
			throw new RuntimeException("ItemRestriction ID " + restrictID + " does not exists");
		}
		if(!rentalItemTypes.containsKey(rentalItemName)) {
			throw new RuntimeException("HireItemType " + rentalItemName + " does not exists");
		}
		RentalItemType itemType = rentalItemTypes.get(rentalItemName);
		ItemRestriction restriction = itemRestrictions.get(restrictID);
		itemType.addItemRestriction(restriction);
		
	}

    public void createDeliveryZone(DeliveryRate rate) {
        deliveryRates.add(rate);
    }

	public boolean removeClient(String name) {
		Client temp = clients.get(name);
		if(temp.getBookings().size()>0)
			return false;
		if(clients.remove(name)!=null)
			return true;
		return false;
	}

	public void editRentalItemType(String name, Rates rates, Money bond) {
		getRentalItemType(name).changeDetails(rates, bond);
	}

	public BuyItemType getBuyItem(String itemName) {
		return buyItemTypes.get(itemName);
	}
	
	public List<DeliveryRate> getDeliveryRates() {
		return deliveryRates;
	}

	public void removeDeliveryRate(String city, String zone) {
		deliveryRates.remove(getDeliveryRate(city,zone));
    }

	public void changeDeliveryRate(String city, String zone, double flatfee, double deliveryrate, double newFlatFee) {
		getDeliveryRate(city,zone,flatfee,deliveryrate).setFlatrate(newFlatFee);
	}	
    
}
