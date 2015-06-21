package rent;

import java.text.ParseException;

import rps.RentEz;
import rps.paymentMethod.CreditCard;
import rps.paymentMethod.creditCardLib.InvalidCreditCardException;

public class CreditCardValidationFixture {
	private RentEz rent;
	
	public CreditCardValidationFixture(RentEz rent){
		this.rent = rent;
	}
	public boolean validNumberCreditCardNoCardTypeExpires(String creditCardNo,String cardType,String expires){
		try {
			new CreditCard(cardType, expires, creditCardNo,rent);
		} catch (ParseException e) {
			return false;
		} catch (InvalidCreditCardException e) {
			return false;
		} 
		return true;
	}

}
