/*
 * @author Rick Mugridge on Sep 26, 2004
 * Released under the terms of the GNU General Public License version 2 or later.
 *
 */
package rps.time.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import rps.time.Duration;
import junit.framework.TestCase;

/**
 *
 */
public class TestDuration extends TestCase {
    public void test1Hour() {
        check("1 hour", 1,0,0);
    }
    public void test2Hours() {
        check("2 hours", 2,0,0);
    }
    public void test1Day() {
        check("1 day", 0,1,0);
    }
    public void test3Day3() {
        check("3 days", 0,3,0);
    }
    public void test1Week() {
        check("1 week", 0,0,1);
    }
    public void test10Weeks() {
        check("10 weeks", 0,0,10);
    }
    public void test1Day1Hour() {
        check("1 day 1 hour", 1,1,0);
    }
    public void test1Hour1Day() {
        check("1 hour 1 day", 1,1,0);
    }
    public void testThree() {
        check("2 weeks 3 days 1 hour", 1,3,2);
    }
    public void testThreeAgain() {
        check("2 hours 1 day 9 weeks", 2,1,9);
    }
    public void testMissingUnits() {
        try {
            check("1", 1,1,0);
            fail("Didn't throw exception");
        } catch (RuntimeException e) {
        }
    }
   private void check(String string, int i, int j, int j2) {
        Duration d = Duration.parse(string);
        assertEquals(i,d.getHours());
        assertEquals(j,d.getDays());
        assertEquals(j2,d.getWeeks());
    }
   public void testCreateDuration() {
       SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
       Duration d = null;
       try {
           d = Duration.createDuration(sdf.parse("05/05/05"), sdf.parse("02/06/05"));
       } catch (ParseException e) {
           e.printStackTrace();
           fail();
       }
       assertEquals(0,d.getDays());
       assertEquals(0,d.getHours());
       assertEquals(4,d.getWeeks());
       assertEquals("4 weeks",d.toString());
       try {
           d = Duration.createDuration(sdf.parse("05/02/05"), sdf.parse("22/06/06"));
       } catch (ParseException e) {
           e.printStackTrace();
           fail();
       }
       assertEquals(4,d.getDays());
       assertEquals(0,d.getHours());
       assertEquals(19,d.getWeeks());
       assertEquals("4 days 19 weeks",d.toString());
       assertEquals(d,new Duration(0,4,19));
   }
   public void testToString() {
       Duration d = new Duration(1,5,8);
       assertEquals("1 hour 5 days 8 weeks",d.toString());
       d = new Duration(1,5,8);
       assertEquals("1 hour 5 days 8 weeks",d.toString());
   }
}
