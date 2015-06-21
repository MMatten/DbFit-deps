package rps;

import java.util.ArrayList;
import java.util.Iterator;

import rps.paymentMethod.Money;
import rps.time.Duration;

public class Template {
    private ArrayList<ArrayList> items = new ArrayList<ArrayList>(); 

    public Template(String name) {
    	//
    }
    
    public boolean deleteItem(RentalItemType itemType) {
        
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            ArrayList b = (ArrayList) iter.next();
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
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            ArrayList b = (ArrayList) iter.next();
            ItemType item = ((ItemType)b.get(0));
            if(item.getName().equals(itemType.getName())){
                b.set(1, numPeople);
                return true;
            }
        }
        ArrayList a = new ArrayList();
        a.add(itemType);
        a.add(numPeople);
        items.add(a);
        return true;
    }

    public Money fillTemplate(int numPeople, Duration duration) {
        Money total = new Money();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            ArrayList a = (ArrayList) iter.next();
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
    
    public ArrayList getItems(){
        return items;
    }

}
