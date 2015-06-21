package rps.paymentMethod.creditCardLib;

import rps.paymentMethod.CreditCard;

public class Mastercard extends CreditCardType {
	private String[] prefix = new String[] { "51", "52", "53", "54", "55" };
	private int[] cardNoLength = new int[] { 16 };

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
