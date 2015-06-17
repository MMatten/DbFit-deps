package test.fitlibrary.utility;

import junit.framework.TestCase;
import fitlibrary.dynamicVariable.DynamicVariables;

public class TestDynamicVariables extends TestCase {
	private DynamicVariables varEmpty;
	private DynamicVariables varFull;
	
	public void setUp() {
		varEmpty = new DynamicVariables();
		String[] vars = { "a","A",
				"b","B" };
		varFull = new DynamicVariables(vars);
	}
	public void testEmptyString() {
		assertEquals("", varEmpty.resolve(""));
		assertEquals("", varFull.resolve(""));
	}
	public void testMatchSingle() {
		assertEquals("@{a}", varEmpty.resolve("@{a}"));
		assertEquals("A", varFull.resolve("@{a}"));
	}
	public void testMatchDouble() {
		assertEquals("@{a}@{b}", varEmpty.resolve("@{a}@{b}"));
		assertEquals("AB", varFull.resolve("@{a}@{b}"));
	}
	public void testInfinite() {
		varFull.put("a","@{a}");
		assertEquals("INFINITE SUBSTITUTION!", varFull.resolve("@{a}"));
	}
	public void testInfinite2() {
		varFull.put("a","@{a}A");
		assertEquals("INFINITE SUBSTITUTION!", varFull.resolve("@{a}"));
	}
	public void testDoubleSubstitution() {
		varFull.put("a","@{b}");
		assertEquals("BBBBBBB", varFull.resolve("@{a}@{a}@{a}@{a}@{a}@{a}@{a}"));
	}
	public void testFourSubstitutions() {
		varFull.put("a","@{b}A");
		varFull.put("b","@{c}B");
		varFull.put("c","@{d}C");
		varFull.put("d","D");
		assertEquals("DCBA", varFull.resolve("@{a}"));
	}
	public void testMatchSingleA() {
		assertEquals("@{@{a}}", varEmpty.resolve("@{@{a}}"));
		varFull.put("a","b");
		assertEquals("B", varFull.resolve("@{@{a}}"));
	}
	public void testMatchOutOfSystemProperties() {
		System.getProperties().put("a", "A");
		assertEquals("A", new DynamicVariables().resolve("@{a}"));
	}
}
