package rps;

import rps.paymentMethod.Money;

public class DeliveryRate {
    private String city;
    private String zone;
    private double deliveryRateFlatFee;
    private double deliveryRatePercent;

    public DeliveryRate(String city, String zone, double deliveryRateFlatFee, double deliveryRate) {
        this.city = city;
        this.zone = zone;
        this.deliveryRatePercent = deliveryRate;
        this.deliveryRateFlatFee = deliveryRateFlatFee;
    }

    public Money getDeliveryCost(Money subTotal) {
        return subTotal.times(deliveryRatePercent/100).plus(new Money(deliveryRateFlatFee));
    }

    public String getZone() {
        return zone;
    }
    public String getCity() {
        return city;
    }
    public String toString() {
    	return city + " " + zone + " " + deliveryRatePercent + " " + deliveryRateFlatFee;
    }
	public double getDeliveryRatePercent() {
		return deliveryRatePercent;
	}
	public double getDeliveryRateFlatFee() {
		return deliveryRateFlatFee;
	}

	public void setFlatrate(double newFlatFee) {
		this.deliveryRateFlatFee = newFlatFee;
	}
}
