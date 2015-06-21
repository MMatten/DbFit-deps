package rps.paymentMethod.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import rps.RentEz;
import rps.paymentMethod.CreditCard;
import rps.paymentMethod.creditCardLib.InvalidCreditCardException;
import rps.time.MyDate;

public class TestVisa extends TestCase {
	
	private RentEz rent;
	CreditCard ccOne;
	
	public void setUp(){
		this.rent = new RentEz();
	}

	public void testLength(){
		try {
            ccOne = new CreditCard("Visa","05/06","49927398716",rent);
            ccOne = new CreditCard("Visa","05/06","  499 273 98716  ",rent);
            assertEquals("49927398716", ccOne.getCreditCardNo());
        } catch (ParseException e) {
            fail();
		} catch (InvalidCreditCardException e) {
			fail();
		}
		
		try {
			ccOne = new CreditCard("Visa","05/06","49927398716595959",rent);
			fail("Creating a Visa with a number length that is too long should cause an invalid card exception");
		} catch (ParseException e) {
			fail("Creating a Visa with a number length that is too long should cause an invalid card exception");
		} catch (InvalidCreditCardException e) {
		}
		
	}
	
	public void testIsRightNo(){
		try {
            ccOne = new CreditCard(" Visa  ", "05/06", "4485891284549100",rent);
            assertEquals("4485891284549100", ccOne.getCreditCardNo());
        } catch (ParseException e) {
            fail();
		} catch (InvalidCreditCardException e) {
			fail();
		}
	}

    public void testExpiryDate() {
		CreditCard ccTwo = null;
        try {
            ccOne = new CreditCard("Visa", "05/06", "49927398716",rent);
			ccTwo = new CreditCard("Visa", "05/05", "49927398716",rent);
			fail("Creating a credit card that has expired should cause an invalid card exception");
        } catch (ParseException e) {
            fail();
        } catch (InvalidCreditCardException e) {
        }
        try {
            assertEquals( new MyDate(new SimpleDateFormat("MM/yy").parse("05/06")), ccOne.getExpiryDate());
        } catch (ParseException e) {
            fail();
        }
    }
	public void testIncorrectPrefix(){
			try {
				ccOne = new CreditCard("Visa","05/06","59927398715",rent);
				fail("Creating a Visa card with prefix 5 should fail");
			} catch (ParseException e) {
			} catch (InvalidCreditCardException e) {
			}
	}
}
