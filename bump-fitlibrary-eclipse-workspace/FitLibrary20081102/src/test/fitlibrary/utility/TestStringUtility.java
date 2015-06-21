package test.fitlibrary.utility;

import junit.framework.TestCase;

public class TestStringUtility extends TestCase {
	public void testSpecials() {
		assertEquals("a","\\".replaceAll("\\\\","a"));
	}
	public void testExpansion() {
		assertEquals("aaaaaa","aaa".replaceAll("a","aa"));
	}
}
