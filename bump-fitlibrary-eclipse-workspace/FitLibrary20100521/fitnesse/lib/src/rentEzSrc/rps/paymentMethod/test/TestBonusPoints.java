package rps.paymentMethod.test;

import rps.paymentMethod.BonusPoints;
import rps.paymentMethod.Money;
import junit.framework.TestCase;

public class TestBonusPoints extends TestCase {
    private BonusPoints bonusPointsOne;
    private BonusPoints bonusPointsTwo;
    private BonusPoints bonusPointsTwo2;
    private BonusPoints bonusPointsZero;

    @Override
	protected void setUp() throws Exception {
        super.setUp();
        bonusPointsOne = new BonusPoints(340);
        bonusPointsTwo = new BonusPoints(456);
        bonusPointsZero = new BonusPoints();
        bonusPointsTwo2 = new BonusPoints(4.5623);
    }

    /*
     * Class under test for boolean equals(Object)
     */
    public void testEqualsObject() {
        assertTrue(bonusPointsTwo.equals(bonusPointsTwo2));
        assertFalse(bonusPointsTwo.equals(bonusPointsOne));
        assertTrue(bonusPointsTwo.equals(bonusPointsTwo));
    }

    public void testPlus() {
        assertEquals(new BonusPoints(), bonusPointsZero.plus(new BonusPoints()));
        assertEquals(new BonusPoints(796), bonusPointsTwo.plus(bonusPointsOne));  
    }

    public void testMinus() {
        assertEquals(new BonusPoints(), bonusPointsZero.minus(new BonusPoints()));
        assertEquals(new BonusPoints(116), bonusPointsTwo.minus(bonusPointsOne)); 
    }

    public void testGetValueInDouble() {
        assertEquals(bonusPointsOne.getValueInDouble(), 3.4);
        assertEquals(bonusPointsTwo.getValueInDouble(), 4.56);
        assertEquals(bonusPointsZero.getValueInDouble(), 0.0);
        assertEquals(bonusPointsTwo2.getValueInDouble(), 4.56);
    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
        assertEquals(bonusPointsOne.toString(), "3.40");
        assertEquals(bonusPointsTwo.toString(), "4.56");
        assertEquals(bonusPointsZero.toString(), "0.00");
    }

    public void testParse() {
        assertEquals(bonusPointsOne, BonusPoints.parse("3.40"));
        assertEquals(bonusPointsTwo, BonusPoints.parse("4.56"));
        assertEquals(bonusPointsTwo2, BonusPoints.parse("4.56"));
        assertEquals(bonusPointsZero, BonusPoints.parse("0"));
    }

    public void testBonusPointCalculation() {
        assertEquals(BonusPoints.bonusPointCalculation(new Money(100.00)), new BonusPoints(0));
        assertEquals(BonusPoints.bonusPointCalculation(new Money(100.01)), new BonusPoints(0.01));
        assertEquals(BonusPoints.bonusPointCalculation(new Money(100.10)), new BonusPoints(0.01));
        assertEquals(BonusPoints.bonusPointCalculation(new Money(101.00)), new BonusPoints(0.05));
        assertEquals(BonusPoints.bonusPointCalculation(new Money(380.00)), new BonusPoints(14.00));
        assertEquals(BonusPoints.bonusPointCalculation(new Money(500.00)), new BonusPoints(20.00));
        assertEquals(BonusPoints.bonusPointCalculation(new Money(725.00)), new BonusPoints(42.50));
    }

}
