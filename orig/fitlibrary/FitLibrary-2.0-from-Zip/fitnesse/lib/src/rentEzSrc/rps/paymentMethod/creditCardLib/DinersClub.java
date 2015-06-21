package rps.paymentMethod.creditCardLib;

import rps.paymentMethod.CreditCard;

public class DinersClub extends CreditCardType {
	private String[] prefix = new String[] { "36", "38", "300", "301", "302","303","304","305" };
	private int[] cardNoLength = new int[] { 14 };
	
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
