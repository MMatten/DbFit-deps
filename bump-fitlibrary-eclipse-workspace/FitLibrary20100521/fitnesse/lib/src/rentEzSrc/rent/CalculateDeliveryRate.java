package rent;

import rps.RentEz;
import rps.paymentMethod.Money;
import fitlibrary.CalculateFixture;

public class CalculateDeliveryRate extends CalculateFixture {
	RentEz rentEz;

	public CalculateDeliveryRate(RentEz rentEz) {
		this.rentEz = rentEz;
	}

	public Money deliveryFeeDollarActualDollarSpentCityZone(Money actualDollarsSpent, String city, String zone) {
		return rentEz.getDeliveryRate(city, zone).getDeliveryCost(actualDollarsSpent);
	}
}
