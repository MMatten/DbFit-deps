/*
 * Created on May 19, 2004
 *  
 */
package rps.transaction;

import java.util.Iterator;

import rps.Rates;
import rps.RentEz;
import rps.RentalItemType;
import rps.exception.DuplicateException;
import rps.paymentMethod.Money;
import rps.person.Client;
import rps.person.StaffMember;
import rps.time.MyDate;
import rps.transactionItems.AddDeliveryRateTransactionItem;
import rps.transactionItems.AddHireItemTypeTransactionItem;
import rps.transactionItems.AddIdentifiedHireItemTransactionItem;
import rps.transactionItems.AddToTemplateTransactionItem;
import rps.transactionItems.ChangeDeliveryRateTransactionItem;
import rps.transactionItems.DeleteDeliveryRateTransactionItem;
import rps.transactionItems.DeleteFromTemplateTransactionItem;
import rps.transactionItems.MaintenanceCompleteTransactionItem;
import rps.transactionItems.RemoveForRepairTransactionItem;
import rps.transactionItems.ReturnFromRepairTransactionItem;
import rps.transactionItems.TransactionItem;

public class AdminTransaction extends Transaction {
	public AdminTransaction(RentEz phs, MyDate startDate,
			StaffMember staffMember) throws Exception {
		super(phs, startDate, staffMember);
	}

	public boolean removeForRepair(String type, int count) {
		TransactionItem item = new RemoveForRepairTransactionItem(this, type, count);
        addTransactionItem(item);
		return true;
	}

    public boolean alterTemplateItem(String templateName, String itemName, float count) {
        TransactionItem item = new AddToTemplateTransactionItem(this, getRentEz().getTemplate(templateName), getRentEz().getRentalItemType(itemName), count);
        addTransactionItem(item);
        return true;
    }
    public boolean deleteTemplateItem(String templateName, String itemName) {
        TransactionItem item = new DeleteFromTemplateTransactionItem(this, getRentEz().getTemplate(templateName), getRentEz().getRentalItemType(itemName));
        addTransactionItem(item);
        return true;
    }
	
	@Override
	public boolean complete(){
		 boolean completed = super.complete();
		 if (completed){
			 getRentEz().removePendingClientTransaction(getStaffMember().getName());
		 }
		 return completed;
	}
    
	public boolean returnFromRepair(String type, int count) {
		if (checkItemToReturnFromRepair(type, count)) {
			TransactionItem item = new ReturnFromRepairTransactionItem(this, type,
					count);
            addTransactionItem(item);
			return true;
		}
		return false;
	}

	private boolean checkItemToReturnFromRepair(String type, int count) {
		return getRentEz().getRentalItemType(type).checkNoOfItemsInRepair(count);
	}

    public boolean addRentalItemType(int count, String type, Rates rates, Money bond) {
        if (getRentEz().containsHireItemTypeAnywhere(type))
            return false;
        TransactionItem item = new AddHireItemTypeTransactionItem(this, count,
                type, rates, bond);
        return addTransactionItem(item);
    }

	public boolean addIdentifiedRentalItem(String id, String type,
			MyDate lastMaintainedDate, double monthsBtwMaintenance) {
		if (checkHireItemTypeExistInPHS(type)) {
			if (getRentEz().getRentalItemType(type).hasIdentifiedItem(id)) { return false; }
		} else if (checkIdentifiedHireItemExistInTrans(type, id)) { return false; }
		TransactionItem item = new AddIdentifiedHireItemTransactionItem(this, id,
				type, lastMaintainedDate, monthsBtwMaintenance);
        addTransactionItem(item);
		return true;
	}

	private boolean checkIdentifiedHireItemExistInTrans(String type, String id) {
		for (Iterator<TransactionItem> i = super.getTransactionItems(); i.hasNext();) {
			TransactionItem transactionItem = i.next();
			if (transactionItem instanceof AddIdentifiedHireItemTransactionItem) {
				AddIdentifiedHireItemTransactionItem item = (AddIdentifiedHireItemTransactionItem) transactionItem;
				if (item.getType().equals(type) && item.getId().equals(id)) { return true; }
			}
		}
		return false;
	}
	public boolean checkHireItemExistInTrans(String type) {
		for (Iterator<TransactionItem> i = super.getTransactionItems(); i.hasNext();) {
			TransactionItem transactionItem = i.next();
			if (transactionItem instanceof AddHireItemTypeTransactionItem) {
				AddHireItemTypeTransactionItem item = (AddHireItemTypeTransactionItem) transactionItem;
				if (item.getType().equals(type)) { return true; }
			}
		}
		return false;
	}	
	private boolean checkHireItemTypeExistInPHS(String type) {
		return getRentEz().containsHireItemType(type);
	}
	public boolean maintenanceComplete(RentalItemType type, String itemIdentifier) {
		try {
			TransactionItem item = new MaintenanceCompleteTransactionItem(this, type,
					itemIdentifier);
            addTransactionItem(item);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	public boolean addClient(Client client) throws DuplicateException {
		return rentEz.addClient(client);
	}

	public boolean removeClient(String name) {
		return rentEz.removeClient(name);
	}

	public boolean createDeliveryZone(String city, String zone, double flatrate, double deliveryrate) {
        TransactionItem item = new AddDeliveryRateTransactionItem(this, city, zone, flatrate, deliveryrate);
        return addTransactionItem(item);
	}

	public boolean deleteDeliveryZone(String city, String zone) {
        TransactionItem item = new DeleteDeliveryRateTransactionItem(this, city, zone);
		return addTransactionItem(item);
	}

	public boolean changeDeliveryZone(String city, String zone, double flatrate, double deliveryrate, double newFlatrate) {
        TransactionItem item = new ChangeDeliveryRateTransactionItem(this, city, zone, flatrate, deliveryrate, newFlatrate);
		return addTransactionItem(item);
	}
}