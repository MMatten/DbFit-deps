package rps.time.test;

import java.text.ParseException;

import rps.time.Duration;
import rps.time.MyDate;

import junit.framework.TestCase;

public class TestMyDate extends TestCase {
	private MyDate date1, dateLater;

	@Override
	public void setUp() throws ParseException {
		date1 = date("2004/05/06 09:01");
		dateLater = date("2004/05/06 10:01");
	}
	public void testEquals() throws ParseException {
		assertEquals(date1,date1);
		assertEquals(date1,date("2004/05/06 09:01"));
	}
	public void testNotEquals() throws ParseException {
		assertTrue(!date1.equals(date("2004/05/06 09:02")));
		assertTrue(!date1.equals(date("2005/05/06 09:01")));
		assertTrue(!date1.equals("2004/05/06 09:01"));
	}
	public void testBefore() {
		assertFalse(date1.before(date1));
		assertTrue(date1.before(dateLater));
		assertFalse(dateLater.before(date1));
	}
	public void testAfter() {
		assertFalse(date1.after(date1));
		assertFalse(date1.after(dateLater));
		assertTrue(dateLater.after(date1));
	}
	public void testAfterMonths() throws ParseException {
		assertEquals(date("2004/07/06 09:01"),date1.afterMonths(2));
	}
	public void testCompare() {
		assertEquals(0,date1.compareTo(date1));
		assertEquals(-1,date1.compareTo(dateLater));
		assertEquals(+1,dateLater.compareTo(date1));
	}
	public void testPlusNoDuration() {
		assertEquals(date1,date1.plus(Duration.parse("0 hours")));
		assertEquals(date1,date1.plus(Duration.parse("0 days")));
	}
	public void testPlusDuration() throws ParseException {
		assertEquals(date("2004/05/06 10:01"),date1.plus(duration("1 hour")));
		assertEquals(date("2004/05/08 9:01"),date1.plus(duration("2 days")));
		assertEquals(date("2004/05/08 9:01"),date1.plus(duration("2 days")));
	}
	public void testDurationTo() throws ParseException {
		assertEquals(duration("0 hours"),date1.durationTo(date1));
		assertEquals(duration("1 hour"),date1.durationTo(dateLater));
		assertEquals(duration("3 hours"),date1.durationTo(date("2004/05/06 12:01")));
		assertEquals(duration("1 week 1 day 1 hour"),date1.durationTo(date("2004/05/14 10:01")));
	}
	
	private MyDate date(String dateString) throws ParseException {
		return MyDate.parse(dateString);
	}
	private Duration duration(String durationString) {
		return Duration.parse(durationString);
	}
}
