package rps.paymentMethod.creditCardLib;

import rps.paymentMethod.CreditCard;

public class AmericanExpress extends CreditCardType {
	private String[] prefix = new String[] { "34", "37" };
	private int[] cardNoLength = new int[] { 15 };

	@Override
	public boolean isValidCreditCard(CreditCard checkCard) {

		if (!checkExpiry(checkCard))
			return false;

		if (!checkPrefix(checkCard, prefix))
			return false;

		if (!checkLength(checkCard, cardNoLength))
			return false;

		return mod10Test(checkCard.getCreditCardNo());
	}

}
