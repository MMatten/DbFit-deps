package rent;

import static rent.Generator.*;

import rps.DeliveryRate;
import rps.Rates;
import rps.RentEz;
import rps.exception.MissingException;
import rps.paymentMethod.Money;
import rps.person.StaffMember;

public class GeneralSetUp {
    private RentEz rentEz;

    public GeneralSetUp(RentEz phs) {
        this.rentEz = phs;
    }
    /** Staff member set up */
    public void namePhone(String name, String phone) throws Exception {
        rentEz.createStaffMember(admin(), name, phone);
    }
    public void staffNamePhone(String name, String phone) throws Exception {
    	withAddText("staff name",name);
    	withAddText("staff phone",phone);
    	click("add staff");
        rentEz.createStaffMember(admin(), name, phone);
    }
    public void staffNamePhoneCommissionPercent(String name, String phone, float commissionPercent) throws Exception {
    	withAddText("staff name",name);
    	withAddText("staff phone",phone);
    	withAddText("staff commission %",""+commissionPercent);
    	click("add staff");
        rentEz.createStaffMember(admin(), name, phone, commissionPercent);
    }	
    public void staffNamePhoneCommissionPercentDiscountPercent(String name, String phone, float commissionPercent, float discountRate) throws Exception {
    	withAddText("staff name",name);
    	withAddText("staff phone",phone);
    	withAddText("staff commission %",""+commissionPercent);
    	withAddText("staff discount rate",""+discountRate);
    	click("add staff");
        rentEz.createStaffMember(admin(), name, phone, commissionPercent, discountRate);
    }	
    /** Client set up */
    public void namePhoneAccountLimit(String name, String phone, @SuppressWarnings("unused") Money accountLimit) throws Exception {
    	clientNamePhone(name, phone);
    }
    public void clientNamePhoneAccountLimit(String name, String phone, @SuppressWarnings("unused") Money accountLimit) throws Exception {
    	clientNamePhone(name, phone);
    }
    public void clientNamePhone(String name, String phone) throws Exception {
    	withAddText("client name",name);
    	withAddText("client phone",phone);
    	click("add client");
        rentEz.createClient(admin(), name, phone);
    }
    /** Rental item type setup */
    public void nameInitialHashHourlyRateDailyRateWeeklyRateBond(
            String name, int initialHash, 
            Money hourlyRate, Money dailyRate, Money weeklyRate,
            Money bond) throws Exception {
        rentEz.createRentalItemType(name,initialHash,
                new Rates(hourlyRate,dailyRate,weeklyRate),bond);
    }
    public void rentalItemNameCountDollarSlashHourDollarSlashDayDollarSlashWeekDeposit(
            String name, int initialHash,
            Money hourlyRate, Money dailyRate, Money weeklyRate,
            Money bond) throws Exception {
    	withAddText("rental item name",name);
    	withAddText("rental item count",""+initialHash);
    	withAddText("rental item hourly rate",""+hourlyRate);
    	withAddText("rental item daily rate",""+dailyRate);
    	withAddText("rental item weekly rate",""+weeklyRate);
    	withAddText("rental item deposit",""+bond);
    	click("add rental item");
        rentEz.createRentalItemType(name,initialHash,
                new Rates(hourlyRate,dailyRate,weeklyRate),bond);
    }
    /** Buy item type set up */
    public void nameInitialHashSellingPrice(String name, 
            int initialHash, Money sellingPrice) throws Exception {
        rentEz.createBuyItemType(name, initialHash, sellingPrice);
    }
    // ...  //COPY:ALL
    private StaffMember admin() throws MissingException { //COPY:ALL
        return rentEz.getStaffMember("Admin"); //COPY:ALL
    } //COPY:ALL
	//vpre006 RentalRestrictions setup method
	public void rentalItemNameCountDollarSlashHourDollarSlashDayDollarSlashWeekDepositMinimumAge(
			String name, int initialHash, Money hourlyRate, Money dailyRate,
			Money weeklyRate, Money bond, int minAge) throws Exception {
		rentEz.createRentalItemType(name, initialHash, new Rates(hourlyRate,
				dailyRate, weeklyRate), bond, minAge);
	}
	//jpe001
	public void restrictionIdConstraint(int restrictID, String constraint){
		rentEz.createItemRestriction(restrictID, constraint);
	}
	public void rentalItemNameRestrictionId(String rentalItemName, String restrictID){
		rentEz.applyRestrictionToItem(restrictID, rentalItemName);
	}
	//Sales Goods Setup method
	public void salesItemNameCountSellingPrice(String salesItemName, int count, Money sellingPrice){
		try {
			rentEz.createBuyItemType(salesItemName, count, sellingPrice);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public void cityZoneDeliveryRateFlatFeeDeliveryRatePercent(String city, String zone, double flatFee, double deliveryRate) {
    	DeliveryRate a = new DeliveryRate(city, zone, flatFee, deliveryRate);
        rentEz.createDeliveryZone(a);
    }
    public void clientNamePhoneCityZoneDeliveryAddress(String name, String phone, String city, String zone, String deliveryAddr) throws Exception {
        rentEz.createClient(admin(), name, phone, city, zone, deliveryAddr);
    }
}