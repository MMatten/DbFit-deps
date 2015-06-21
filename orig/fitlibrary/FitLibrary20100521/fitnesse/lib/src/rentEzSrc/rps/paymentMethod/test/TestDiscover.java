package rps.paymentMethod.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import rps.RentEz;
import rps.paymentMethod.CreditCard;
import rps.paymentMethod.creditCardLib.InvalidCreditCardException;
import rps.time.MyDate;

public class TestDiscover extends TestCase {
	
	private RentEz rent;
	CreditCard ccOne;
	
	public void setUp(){
		this.rent = new RentEz();
	}
	
	public void testIsRightNo(){
		try {
            ccOne = new CreditCard("Discover", "05/06", "6011404643081688",rent);
            assertEquals("6011404643081688", ccOne.getCreditCardNo());
        } catch (ParseException e) {
            fail();
		} catch (InvalidCreditCardException e) {
			fail();
		}
	}

    public void testExpiryDate() {
		CreditCard ccTwo = null;
        try {
            ccOne = new CreditCard("Discover", "05/06", "6011404643081688",rent);
			ccTwo = new CreditCard("Discover", "05/05", "6011404643081688",rent);
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
				ccOne = new CreditCard("Discover","05/06","6045404643081688",rent);
				fail("Creating a Discover card with prefix 6045 should fail");
			} catch (ParseException e) {
			} catch (InvalidCreditCardException e) {
			}
	}
	public void testLength(){
		try {
            ccOne = new CreditCard("Discover","05/06","6011404643081688",rent);
            assertEquals("6011404643081688", ccOne.getCreditCardNo());
        } catch (ParseException e) {
            fail();
		} catch (InvalidCreditCardException e) {
			fail();
		}
		
		try {
			ccOne = new CreditCard("Discover","05/06","6011404643081688595959",rent);
			fail("Creating a Discover with a number length that is too long should cause an invalid card exception");
		} catch (ParseException e) {
			fail("Creating a Discover with a number length that is too long should cause an invalid card exception");
		} catch (InvalidCreditCardException e) {
		}
		
	}
}
