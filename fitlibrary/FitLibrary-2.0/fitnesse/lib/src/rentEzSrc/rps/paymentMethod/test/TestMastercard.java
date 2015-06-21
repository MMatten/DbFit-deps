package rps.paymentMethod.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import rps.RentEz;
import rps.paymentMethod.CreditCard;
import rps.paymentMethod.creditCardLib.InvalidCreditCardException;
import rps.time.MyDate;

public class TestMastercard extends TestCase {
	
	private RentEz rent;
	CreditCard ccOne;
	
	@Override
	public void setUp(){
		this.rent = new RentEz();
	}
	
	public void testIsRightNo(){
		try {
            ccOne = new CreditCard("Mastercard", "05/06", "5551559755184006",rent);
            assertEquals("5551559755184006", ccOne.getCreditCardNo());
        } catch (ParseException e) {
            fail();
		} catch (InvalidCreditCardException e) {
			fail();
		}
	}

    public void testExpiryDate() {
        try {
            ccOne = new CreditCard("Mastercard", "05/06", "5551559755184006",rent);
			new CreditCard("Mastercard", "05/05", "5551559755184006",rent);
			fail("Creating a credit card that has expired should cause an invalid card exception");
        } catch (ParseException e) {
            fail();
        } catch (InvalidCreditCardException e) {
        	//
        }
        try {
            assertEquals( new MyDate(new SimpleDateFormat("MM/yy").parse("05/06")), ccOne.getExpiryDate());
        } catch (ParseException e) {
            fail();
        }
    }
	public void testIncorrectPrefix(){
			try {
				ccOne = new CreditCard("Mastercard","05/06","5981559755184006",rent);
				fail("Creating a Mastercard card with prefix 311 should fail");
			} catch (ParseException e) {
				//
			} catch (InvalidCreditCardException e) {
				//
			}
	}
	public void testLength(){
		try {
            ccOne = new CreditCard("Mastercard","05/06","5551559755184006",rent);
            assertEquals("5551559755184006", ccOne.getCreditCardNo());
        } catch (ParseException e) {
            fail();
		} catch (InvalidCreditCardException e) {
			fail();
		}
		
		try {
			ccOne = new CreditCard("Mastercard","05/06","5551559755184006595959",rent);
			fail("Creating a Mastercard with a number length that is too long should cause an invalid card exception");
		} catch (ParseException e) {
			fail("Creating a Mastercard with a number length that is too long should cause an invalid card exception");
		} catch (InvalidCreditCardException e) {
			//
		}
		
	}
}
