package rps.paymentMethod.test;

import rps.paymentMethod.Money;
import junit.framework.TestCase;

public class TestMoney extends TestCase {
	public void testZero() {
		assertTrue(new Money().isZero());
		assertTrue(!new Money(1).isZero());
	}
	public void testEquals(){
		Money money1 = new Money(1);
		String s = "Money";
		assertNotSame(money1,s);
	}
	public void testHashCode(){
		Money money1 = new Money(18695);
		assertEquals(18695,money1.hashCode());
	}
	public void testRelations() {
		Money money1 = new Money(1);
		Money moneyOne = new Money(1);
		Money money2 = new Money(2);
		assertEquals(money1,money1);
		assertEquals(money1,moneyOne);
		assertTrue(!money1.equals(money2));
		
		assertTrue(money2.greaterThan(money1));
		assertTrue(money2.greaterThanEqual(money2));
		assertTrue(!money1.greaterThan(money2));
		assertTrue(!money1.greaterThanEqual(money2));
	}
	public void testNegate() {
		assertEquals(new Money(), new Money().negate());
		assertEquals(new Money(3000), new Money(3000).negate().negate());
		assertEquals(new Money(3000), new Money(-3000).negate());
	}
	public void testAdd() {
		Money money100 = new Money(100);
		assertEquals(new Money(300), money100.plus(new Money(200)));
		assertEquals(new Money(), money100.plus(new Money(-100)));
		assertEquals(new Money(), money100.plus(money100.negate()));
	}
	public void testMinus() {
		Money money100 = new Money(100);
		Money money200 = new Money(200);
		assertEquals(new Money(-100), money100.minus(money200));
		assertEquals(money200, money100.minus(new Money(-100)));
		assertEquals(money200, money100.minus(money100.negate()));
	}
	public void testTimes() {
		assertEquals(new Money(), new Money().times(3));
		assertEquals(new Money(300), new Money(100).times(3));
		assertEquals(new Money(-300), new Money(100).times(-3));
		
		assertEquals(new Money(10), new Money(100).times(0.1));
		assertEquals(new Money(1), new Money(100).times(0.01));
		assertEquals(new Money(), new Money(100).times(0.001));
		
		assertEquals(new Money(10), new Money(-100).times(-0.1));
		assertEquals(new Money(-1), new Money(100).times(-0.01));
		assertEquals(new Money(), new Money(100).times(-0.001));
	}
	public void testParse() {
		assertEquals(new Money(),Money.parse("0.00"));
		assertEquals(new Money(10.5),Money.parse("10.50"));
		assertEquals(new Money(0.55),Money.parse("0.55"));
		try{
			Money.parse("90.002");
			fail("Parsing this string should throw a runtime exception");
		}catch(RuntimeException e){
		}
	}
	public void testToString() {
		assertEquals("0.00", new Money(0).toString());
		assertEquals("1.01", new Money(101).toString());
		assertEquals("0.10", new Money(10).toString());
		assertEquals("0.01", new Money(1).toString());
		assertEquals("1234.56", new Money(123456).toString());
		assertEquals("-1234.56", new Money(-123456).toString());		
	}
	public void testDouble() {
		assertEquals(new Money(), new Money(0.0001));
		assertEquals(new Money(100), new Money(1.004));
		assertEquals(new Money(101), new Money(1.005));
		assertEquals(new Money(1004), new Money(10.044));

		assertEquals(new Money(-100), new Money(-1.004));
		assertEquals(new Money(-101), new Money(-1.005));
		assertEquals(new Money(-1004), new Money(-10.044));
}
}
