package rps.paymentMethod.creditCardLib;

import rps.paymentMethod.CreditCard;

public abstract class CreditCardType {

	public abstract boolean isValidCreditCard(CreditCard checkCard);

	protected boolean mod10Test(String creditCardNo) {
		boolean isEven = false;
		int total = 0;
		for (int i = creditCardNo.length() - 1; i >= 0; i--) {
			int digit = Integer.parseInt("" + creditCardNo.charAt(i));
			if (isEven) {
				int timesTwo = digit * 2;

				if (timesTwo >= 10) {
					total += timesTwo - 10 + 1;
				} else {
					total += timesTwo;
				}
			} else {
				total += digit;
			}
			isEven = !isEven;
		}
		return total % 10 == 0;
	}

	protected boolean checkExpiry(CreditCard checkCard) {
		if(checkCard.getExpiryDate().afterMonths(1).before(checkCard.getRent().getTime()))
			return false;
		return true;
	}

	protected boolean checkPrefix(CreditCard checkCard, String[] prefix) {
		String creditCardNo = checkCard.getCreditCardNo();
		boolean current = false;
		for (int i = 0; i < prefix.length; i++) {
			if (creditCardNo.startsWith(prefix[i])) {
				current = true;
			}
		}
		return current;
	}
	protected boolean checkLength(CreditCard checkCard,int[] cardNoLength) {
		boolean matchLength = false;
		for(int i = 0 ; i < cardNoLength.length ; i++){
			if (checkCard.getCreditCardNo().length()==cardNoLength[i]){
				matchLength = true;
			}
		}
		return matchLength;
	}

}
