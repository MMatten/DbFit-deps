package rps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rps.paymentMethod.Money;
import rps.time.Duration;

public class Template {
    private List<List<Object>> items = new ArrayList<List<Object>>(); 

    public boolean deleteItem(RentalItemType itemType) {
        for (Iterator<List<Object>> iter = items.iterator(); iter.hasNext();) {
        	List<Object> b = iter.next();
            RentalItemType item = ((RentalItemType)b.get(0));
            if(item.getName().equals(itemType.getName())){
                items.remove(b);
                return true;
            }
        }
        return false;

    }

    public boolean addItem (ItemType itemType, float numPeople) {
        // if the itemtype already exists in the template then edit it rather then adding a new one
        for (Iterator<List<Object>> iter = items.iterator(); iter.hasNext();) {
        	List<Object> b = iter.next();
            ItemType item = ((ItemType)b.get(0));
            if(item.getName().equals(itemType.getName())){
                b.set(1, numPeople);
                return true;
            }
        }
        List<Object> a = new ArrayList<Object>();
        a.add(itemType);
        a.add(numPeople);
        items.add(a);
        return true;
    }

    public Money fillTemplate(int numPeople, Duration duration) {
        Money total = new Money();
        for (Iterator<List<Object>> iter = items.iterator(); iter.hasNext();) {
        	List<Object> a = iter.next();
            int numItems = (int)Math.ceil( numPeople/( (Float)a.get(1) ) );
            Object item = a.get(0);
			if (item instanceof RentalItemType) {
				RentalItemType rentalItem = (RentalItemType) item;	
	            Money m = rentalItem.totalRentalCost( duration, numItems );
	            total = total.plus(m);
			}
			else if (item instanceof BuyItemType) {
				BuyItemType buyItem = (BuyItemType) item;
				Money m = buyItem.getSellingPrice(numItems);
				total = total.plus(m);
			}
        }
        return total;
    }
    
    public List<List<Object>> getItems(){
        return items;
    }

}
