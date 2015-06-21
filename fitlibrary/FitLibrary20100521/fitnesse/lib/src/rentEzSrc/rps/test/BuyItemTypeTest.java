package rps.test;

import rps.BuyItemType;
import rps.paymentMethod.Money;
import junit.framework.TestCase;

public class BuyItemTypeTest extends TestCase {

	BuyItemType itemtype;
	protected void setUp() throws Exception {
		super.setUp();
		itemtype = new BuyItemType("Test",23, new Money(1200));
	}


	public void testGetName() {
		assertEquals(itemtype.getName(), "Test");
	}

	/*
	 * Class under test for Money getSellingPrice()
	 */
	public void testGetSellingPrice() {
		assertEquals(itemtype.getSellingPrice(), new Money(1200));
	}

	/*
	 * Class under test for Money getSellingPrice(int)
	 */
	public void testGetSellingPriceint() {

		assertEquals(itemtype.getSellingPrice(2), new Money(2400));
	}

	public void testSale() {
		assertFalse(itemtype.sale(24));
		assertTrue(itemtype.sale(2));		
	}

	public void testGetCount() {
		assertEquals(itemtype.getCount(), 23);
	}
	
	public void testEquals(){
		BuyItemType itemtype2 = new BuyItemType("Test",23, new Money(1200));
		BuyItemType itemtype3 = new BuyItemType("Testing",23, new Money(1200));
		BuyItemType itemtype4 = new BuyItemType("Test",245, new Money(1200));
		BuyItemType itemtype5 = new BuyItemType("Test",23, new Money(1000));
		assertEquals(itemtype,itemtype2);
		assertNotSame(itemtype,itemtype3);
		assertNotSame(itemtype,itemtype4);
		assertNotSame(itemtype,itemtype5);
	}

}
