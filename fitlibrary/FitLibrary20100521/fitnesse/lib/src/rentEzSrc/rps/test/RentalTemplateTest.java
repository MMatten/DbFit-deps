package rps.test;

import rps.Rates;
import rps.RentalItemType;
import rps.Template;
import rps.paymentMethod.Money;
import rps.time.Duration;
import junit.framework.TestCase;

public class RentalTemplateTest extends TestCase {
	
	Template template;
	RentalItemType item1;

	protected void setUp() throws Exception {
		super.setUp();
		template = new Template("TEST");
	    item1 = new RentalItemType("test", 2, new Rates(new Money(2), new Money(4), new Money(8)), new Money(12), 6);

	}


	public void testTemplate() {
		assertEquals(template.getItems().size(), 0);
		template.addItem(item1, 23);
		assertEquals(template.getItems().size(), 1);
		template.deleteItem(item1);
		assertEquals(template.getItems().size(), 0);
	}
	
	public void testFillTemplate() {
		assertEquals(template.fillTemplate(12, new Duration(0,4,0)), new Money());
		template.addItem(item1, 23);
		assertEquals(template.fillTemplate(12, new Duration(0,4,0)), new Money(0.20));
	}


}
