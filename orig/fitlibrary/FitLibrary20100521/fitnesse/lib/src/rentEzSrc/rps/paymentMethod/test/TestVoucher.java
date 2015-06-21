package rps.paymentMethod.test;

import java.sql.Date;

import rps.paymentMethod.Money;
import rps.paymentMethod.Voucher;
import rps.time.MyDate;
import junit.framework.TestCase;

public class TestVoucher extends TestCase {
	Voucher voucher, voucher2,voucher3;
	protected void setUp() throws Exception {
		super.setUp();
		voucher = new Voucher(20.0,new MyDate(new Date(1)));
		voucher2 = new Voucher(new Money(2000));
		voucher3 = new Voucher(new Money(2000),new MyDate(new Date(1)));
	}
	/*
	 * Class under test for boolean equals(Object)
	 */
	public void testEqualsObject() {		
		assertTrue(voucher.equals(new Voucher(20.0)));
		assertTrue(voucher.equals(voucher2));
		assertTrue(voucher2.equals(voucher3));
		assertFalse(voucher.equals(new Voucher(10.0)));
		assertFalse(voucher.equals(new Money(2000)));
	}

	/*
	 * Class under test for String toString()
	 */
	public void testToString() {
		assertEquals(voucher.toString(),"20.00");
		assertEquals(voucher2.toString(),"20.00");
		assertEquals(voucher3.toString(),"20.00");
		assertEquals(new Voucher(0).toString(),"0.00");
		}

	public void testGetValueInDouble() {
		assertEquals(voucher.getValueInDouble(),20.0);
		assertEquals(voucher2.getValueInDouble(),20.0);
		assertEquals(voucher3.getValueInDouble(),20.0);
	}

	public void testGetExpiryDate() {
		assertEquals(voucher.getExpiryDate(), new MyDate(new Date(1)));
		assertEquals(voucher2.getExpiryDate(), null);
		assertEquals(voucher3.getExpiryDate(),new MyDate(new Date(1)));
	}

}
