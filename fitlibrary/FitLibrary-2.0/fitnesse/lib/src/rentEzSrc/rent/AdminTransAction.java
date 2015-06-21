package rent;

import static rent.Generator.*;

import rps.BuyItemType;
import rps.Rates;
import rps.RentEz;
import rps.RentalItemType;
import rps.Template;
import rps.exception.DuplicateException;
import rps.exception.MissingException;
import rps.paymentMethod.BonusPoints;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.MyDate;
import rps.transaction.AdminTransaction;
import fitlibrary.traverse.DomainAdapter;

public class AdminTransAction implements DomainAdapter {
    private RentEz rentEz;
	private AdminTransaction transaction;
    private String templateName;
	
	public AdminTransAction(AdminTransaction transaction, RentEz rentEz) throws Exception {
	    this.rentEz = rentEz;
		this.transaction = transaction;
	}
	public Object getSystemUnderTest() {
		return transaction;
	}
	public boolean addOfTypeCostingSlashHourSlashDaySlashWeekBond(int count,String type, Money hourly, Money daily, Money weekly, Money bond){
    	header("Admin Add rental items");
    	withAddText("rental item name",type);
    	withAddText("rental item count",""+count);
    	withAddText("rental item hourly rate",""+hourly);
    	withAddText("rental item daily rate",""+daily);
    	withAddText("rental item weekly rate",""+weekly);
    	withAddText("rental item deposit",""+bond);
    	click("add rental item");
		Rates rates = new Rates(hourly,daily,weekly);
		return transaction.addRentalItemType(count, type, rates, bond);
	}
	public boolean completeTransaction() throws Exception{
    	click("complete admin transaction");
		return transaction.complete();
	}
	public boolean addIdentifiedOfTypeLastMaintainedPeriodOfMonths(String id, 
	        String type, MyDate lastMaintainedDate, double monthsBtwMaintenance){
    	header("Admin Add identified rental items");
    	withAddText("identified rental item id",""+id);
    	withAddText("identified rental item name",type);
    	withAddText("identified rental item last mainained date",""+lastMaintainedDate);
    	withAddText("identified rental item months between",""+monthsBtwMaintenance);
    	click("add identified rental item");
		return transaction.addIdentifiedRentalItem(id,type,lastMaintainedDate,monthsBtwMaintenance);
	}
	public boolean abortTransaction(){
	   	notCovered();
		return transaction.abort();
	}
	public String[] requiringMaintenance() {		
    	header("Maintenance required");
    	click("maintenance required");
        String[] items = rentEz.getItemsNeedingRepair(rentEz.getTime());
        for (int i = 0; i < items.length; i++)
        	elementExists("maintenance "+items[i]);
		return items;
	}
	public boolean maintenanceComplete(String itemIdentifier) {
    	header("Maintenance complete");
    	withAddText("identified rental item name",itemIdentifier);
    	click("maintenance completed");
		RentalItemType rentalItemTypeFor = rentEz.rentalItemTypeFor(itemIdentifier);
        if(rentalItemTypeFor == null)
            return false;
        return transaction.maintenanceComplete(rentalItemTypeFor, itemIdentifier);
	}
	public boolean topupPointsForClient(double points, String clientName){
    	header("Admin TopUps for Client");
    	click("TopUps for Client");
    	click("client "+clientName);
    	withAddText("client points",""+points);
    	click("update");
		try {
			rentEz.getClient(clientName).topupBonusPoints(new BonusPoints(points));
			return true;
		} catch (MissingException e) {
			e.printStackTrace();
			return false;
		}
	}
    public boolean alterTemplate(String templateName2) {
    	click("templates");
    	click("template "+templateName2);
    	titleIs("template "+templateName2);
    	Template template = rentEz.getTemplate(templateName2);
        if(template != null )
            this.templateName = templateName2;
        return (template != null);
    }
    public boolean oneForPeople (String itemName, float count){
    	withAddText("template item",""+itemName);
    	withAddText("template number of people",""+count);
       	click("alter template");
       return transaction.alterTemplateItem(templateName,itemName,count);// template.addItem(rentEz.getRentalItemType(itemName), numPeople);
    }
    public boolean delete(String itemName){
    	withAddText("template item",""+itemName);
       	click("delete template");
        return transaction.deleteTemplateItem(templateName, itemName);
    }
	public boolean createClientWithPhoneNumberInCityInZoneAtAddress(String name,String phoneNumber,String city,String zone,String deliveryAddr) throws DuplicateException{
    	header("Admin Create Client");
    	withAddText("client name",name);
    	withAddText("client phone",phoneNumber);
    	withAddText("client city",city);
    	withAddText("client zone",zone);
    	withAddText("client deliveryAddr",deliveryAddr);
    	click("add client");
    	noErrorMessage();
    	return transaction.addClient(new Client(new StaffMember(null,"admin","xxxxxxx"),name,phoneNumber,city,zone,deliveryAddr));
	}
	public boolean createClientWithPhoneNumberWithEmailInCityInZoneAtAddress(String name,String phoneNumber,String email,String city,String zone,String deliveryAddr) throws DuplicateException{
    	header("Admin Create Client");
    	withAddText("client name",name);
    	withAddText("client phone",phoneNumber);
    	withAddText("client city",city);
    	withAddText("client zone",zone);
    	withAddText("client deliveryAddr",deliveryAddr);
    	withAddText("client email",email);
    	click("add client");
    	noErrorMessage();
		return transaction.addClient(new Client(new StaffMember(null,"admin","xxxxxxx"),name,phoneNumber,city,zone,deliveryAddr,email));
	}
	public boolean deleteClient(String name){
    	header("Admin Create Client");
    	withAddText("client name",name);
    	click("delete client");
    	noErrorMessage();
		return transaction.removeClient(name);
	}
	public boolean modifyClientSetPhoneNumberInCityInZoneAtAddress(String name,String phoneNumber,String city,String zone,String deliveryAddr) throws DuplicateException{
    	header("Admin Modify Client");
    	click("Find Client");
    	click("client "+name);
    	withAddText("client name",name);
    	withAddText("client phone",phoneNumber);
    	withAddText("client city",city);
    	withAddText("client zone",zone);
    	withAddText("client deliveryAddr",deliveryAddr);
    	click("modify client");
    	noErrorMessage();
		return transaction.removeClient(name) && transaction.addClient(new Client(new StaffMember(null,"admin","xxxxxxx"),name,phoneNumber,city,zone,deliveryAddr));
	}
	public boolean addDeliveryCityZoneFlatRateDeliveryRatePercent(String city, String zone, double flatrate, double deliveryrate) {
    	header("Admin Add Delivery City");
    	click("Add Delivery City");
    	withAddText("delivery city",city);
    	withAddText("delivery city zone",zone);
    	withAddText("delivery city flat rate",""+flatrate);
    	withAddText("delivery city delivery rate",""+deliveryrate);
    	click("add city");
    	noErrorMessage();
		return transaction.createDeliveryZone(city, zone, flatrate, deliveryrate);
	}
	public boolean removeDeliveryCityZone(String city, String zone) {
    	header("Admin Remove Delivery City");
    	click("Remove Delivery City");
    	click("delivery city "+city);
    	click("remove city");
    	noErrorMessage();
		return transaction.deleteDeliveryZone(city, zone);
	}
	public boolean changeDeliveryCityZoneFlatRateDeliveryRatePercentToFlatRate(String city, String zone, double flatrate, double deliveryrate, double newFlatrate) {
    	header("Admin Change Delivery City");
    	click("Change Delivery City");
    	click("delivery city "+city);
    	withAddText("delivery city zone",zone);
    	withAddText("delivery city flat rate",""+flatrate);
    	withAddText("delivery city delivery rate",""+deliveryrate);
    	click("update city");
    	noErrorMessage();
		return transaction.changeDeliveryZone(city, zone, flatrate, deliveryrate, newFlatrate);
	}
	public boolean addSalesItemOfTypeCostingEach(int count,String itemName,Money cost){
    	header("Admin Add sales items");
    	withAddText("sales item name",itemName);
    	withAddText("sales item count",""+count);
    	withAddText("sales item cost",""+cost);
    	click("add sales item");
		try {
			rentEz.createBuyItemType(itemName,count,cost);
		} catch (DuplicateException e) {
			return false;
		}
		return true;
	}
	public boolean addToSalesItemOfType(int count, String itemName){
    	header("Admin Add to sales item");
    	click("sales item "+itemName);
    	withAddText("sales item count",""+count);
    	click("update sales item");
		BuyItemType temp = rentEz.getBuyItem(itemName);
		if(temp==null){
			return false;
		}
		temp.add(count);
		return true;
	}
}
