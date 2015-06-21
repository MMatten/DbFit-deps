package rps.test;

import rps.ItemRestriction;
import junit.framework.TestCase;

public class ItemRestrictionTest extends TestCase {

	ItemRestriction restriction, restriction2;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		restriction = new ItemRestriction(1, "test");
		restriction2 = new ItemRestriction("test12", "test");
	}



	public void testGetID() {
		assertEquals(restriction.getID(), "1");

		assertEquals(restriction2.getID(), "test12");
	}

}
