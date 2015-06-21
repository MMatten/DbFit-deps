package rps.paymentMethod.test;

import java.text.ParseException;

import junit.framework.TestCase;
import rps.RentEz;
import rps.paymentMethod.CreditCard;
import rps.paymentMethod.creditCardLib.InvalidCreditCardException;

public class TestCreditCard extends TestCase {
	
	public void testCreateNonExistentCreditCard(){
		try {
			CreditCard ccOne = new CreditCard("None","05/06","49927398716",new RentEz());
			fail("Creating a credit card that doesn't exist should cause an InvalidCreditCardException");
		} catch (ParseException e) {
			fail("Creating a credit card that doesn't exist should cause an InvalidCreditCardException");
		} catch (InvalidCreditCardException e) {		
		}
	}

}
