package rent;

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
		Rates rates = new Rates(hourly,daily,weekly);
		return transaction.addRentalItemType(count, type, rates, bond);
	}
	public boolean completeTransaction() throws Exception{
		return transaction.complete();
	}
	public boolean addIdentifiedOfTypeLastMaintainedPeriodOfMonths(String id, 
	        String type, MyDate lastMaintainedDate, double monthsBtwMaintenance){
		return transaction.addIdentifiedRentalItem(id,type,lastMaintainedDate,monthsBtwMaintenance);
	}
	public boolean abortTransaction(){
		return transaction.abort();
	}
	public String[] requiringMaintenance() {		
        return rentEz.getItemsNeedingRepair(rentEz.getTime());
	}
	public boolean maintenanceComplete(String itemIdentifier) {
		RentalItemType rentalItemTypeFor = rentEz.rentalItemTypeFor(itemIdentifier);
        if(rentalItemTypeFor == null)
            return false;
        return transaction.maintenanceComplete(rentalItemTypeFor, itemIdentifier);
	}
	public boolean topupPointsForClient(double points, String clientName){
		try {
			rentEz.getClient(clientName).topupBonusPoints(new BonusPoints(points));
			return true;
		} catch (MissingException e) {
			e.printStackTrace();
			return false;
		}
	}
    public boolean alterTemplate(String templateName) {
        Template template = rentEz.getTemplate(templateName);
        if(template != null )
            this.templateName = templateName;
        return (template != null);
    }
    public boolean oneForPeople (String itemName, float count){
        return transaction.alterTemplateItem(templateName,itemName,count);// template.addItem(rentEz.getRentalItemType(itemName), numPeople);
    }
    public boolean delete(String itemName){
        return transaction.deleteTemplateItem(templateName, itemName);
    }
	public boolean createClientWithPhoneNumberInCityInZoneAtAddress(String name,String phoneNumber,String city,String zone,String deliveryAddr) throws DuplicateException{
		return transaction.addClient(new Client(new StaffMember(null,"admin","xxxxxxx"),name,phoneNumber,city,zone,deliveryAddr));
	}
	public boolean createClientWithPhoneNumberWithEmailInCityInZoneAtAddress(String name,String phoneNumber,String email,String city,String zone,String deliveryAddr) throws DuplicateException{
		return transaction.addClient(new Client(new StaffMember(null,"admin","xxxxxxx"),name,phoneNumber,city,zone,deliveryAddr,email));
	}
	public boolean deleteClient(String name){
		return transaction.removeClient(name);
	}
	public boolean modifyClientSetPhoneNumberInCityInZoneAtAddress(String name,String phoneNumber,String city,String zone,String deliveryAddr) throws DuplicateException{
		return transaction.removeClient(name) && transaction.addClient(new Client(new StaffMember(null,"admin","xxxxxxx"),name,phoneNumber,city,zone,deliveryAddr));
	}
	public boolean addDeliveryCityZoneFlatRateDeliveryRatePercent(String city, String zone, double flatrate, double deliveryrate) {
		return transaction.createDeliveryZone(city, zone, flatrate, deliveryrate);
	}
	public boolean removeDeliveryCityZone(String city, String zone) {
		return transaction.deleteDeliveryZone(city, zone);
	}
	public boolean changeDeliveryCityZoneFlatRateDeliveryRatePercentToFlatRate(String city, String zone, double flatrate, double deliveryrate, double newFlatrate) {
		return transaction.changeDeliveryZone(city, zone, flatrate, deliveryrate, newFlatrate);
	}

	public boolean addSalesItemOfTypeCostingEach(int count,String itemName,Money cost){
		try {
			rentEz.createBuyItemType(itemName,count,cost);
		} catch (DuplicateException e) {
			return false;
		}
		return true;
	}
	
	public boolean addToSalesItemOfType(int count, String itemName){
		BuyItemType temp = rentEz.getBuyItem(itemName);
		if(temp==null){
			return false;
		}
		temp.add(count);
		return true;
	}
}
