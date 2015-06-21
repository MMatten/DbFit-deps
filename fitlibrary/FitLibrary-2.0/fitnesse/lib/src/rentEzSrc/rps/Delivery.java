package rps;

import rps.time.MyDate;

public class Delivery {
	public String date;
	public String city;
	public String zone;
	public String deliveryAddress;
	public String item;
	public int itemCount;
	
	public Delivery(MyDate date, String city, String zone, String deliveryAddress, String item, int itemCount) {
		this.date = date.toString();
		this.city = city;
		this.zone = zone;
		this.deliveryAddress = deliveryAddress;
		this.itemCount = itemCount;
		this.item = item;
	}
	
	@Override
	public boolean equals(Object o) {
    	if (o instanceof Delivery) {
			Delivery other = (Delivery)o;
    		return (date.equals(other.date) && city.equals(other.city) && zone.equals(other.zone) && deliveryAddress.equals(deliveryAddress) && item.equals(other.item) && itemCount == other.itemCount);
    	}
    	return false;
	}
}
