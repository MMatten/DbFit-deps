package rps.paymentMethod.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import rps.RentEz;
import rps.paymentMethod.CreditCard;
import rps.paymentMethod.creditCardLib.InvalidCreditCardException;
import rps.time.MyDate;

public class TestAmericanExpress extends TestCase {
	
	private RentEz rent;
	CreditCard ccOne;
	
	@Override
	public void setUp(){
		this.rent = new RentEz();
	}
	
	public void testIsRightNo(){
		try {
            ccOne = new CreditCard("AmericanExpress", "05/06", "372730553483969",rent);
            assertEquals("372730553483969", ccOne.getCreditCardNo());
        } catch (ParseException e) {
            fail();
		} catch (InvalidCreditCardException e) {
			fail();
		}
	}

    public void testExpiryDate() {
        try {
            ccOne = new CreditCard("AmericanExpress", "05/06", "372730553483969",rent);
            new CreditCard("AmericanExpress", "05/05", "372730553483969",rent);
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
				ccOne = new CreditCard("AmericanExpress","05/06","582730553483969",rent);
				fail("Creating a AmericanExpress card with prefix 5 should fail");
			} catch (ParseException e) {
				//
			} catch (InvalidCreditCardException e) {
				//
			}
	}
	public void testLength(){
		try {
            ccOne = new CreditCard("AmericanExpress","05/06","372730553483969",rent);
            ccOne = new CreditCard("AmericanExpress","05/06","  372 730 55348 3969 ",rent);
            assertEquals("372730553483969", ccOne.getCreditCardNo());
        } catch (ParseException e) {
            fail();
		} catch (InvalidCreditCardException e) {
			fail();
		}
		
		try {
			ccOne = new CreditCard("AmericanExpress","05/06","372730553483969595959",rent);
			fail("Creating a AmericanExpress with a number length that is too long should cause an invalid card exception");
		} catch (ParseException e) {
			fail("Creating a AmericanExpress with a number length that is too long should cause an invalid card exception");
		} catch (InvalidCreditCardException e) {
			//
		}
		
	}
}

