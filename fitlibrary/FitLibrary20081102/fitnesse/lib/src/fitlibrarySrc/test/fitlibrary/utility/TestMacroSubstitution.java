package test.fitlibrary.utility;

import java.util.ArrayList;

import junit.framework.TestCase;
import fitlibrary.DoFixture;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.utility.ParameterSubstitution;

public class TestMacroSubstitution extends TestCase {
	DoFixture evaluator = new DoFixture();
	public void testNoParameters() {
		Tables tables = bodyTables("a","b");
		ParameterSubstitution macro = new ParameterSubstitution(new ArrayList(), tables,evaluator);
		Tables substituted = macro.substitute(new ArrayList(),evaluator);
		assertEquals(tables,substituted);
	}
	private Tables bodyTables(String a, String b) {
		Row row = new Row();
		row.addCell(a);
		row.addCell(b);
		Tables tables = new Tables(new Table(row));
		return tables;
	}
	public void testOneParameter() {
		Tables tables = bodyTables("A","b");
		ArrayList formalParameterList = new ArrayList();
		formalParameterList.add("A");
		ParameterSubstitution macro = new ParameterSubstitution(formalParameterList, tables,evaluator);
		ArrayList actualParameterList = new ArrayList();
		actualParameterList.add("a");
		Tables substituted = macro.substitute(actualParameterList,evaluator);
		assertEquals(bodyTables("a","b"),substituted);
	}
	public void testOneParameterSubstitutedTwice() {
		Tables tables = bodyTables("A","A");
		ArrayList formalParameterList = new ArrayList();
		formalParameterList.add("A");
		ParameterSubstitution macro = new ParameterSubstitution(formalParameterList, tables,evaluator);
		ArrayList actualParameterList = new ArrayList();
		actualParameterList.add("a");
		Tables substituted = macro.substitute(actualParameterList,evaluator);
		assertEquals(bodyTables("a","a"),substituted);
	}
	public void testTwoParameters() {
		Tables tables = bodyTables("A","B");
		ArrayList formalParameterList = new ArrayList();
		formalParameterList.add("A");
		formalParameterList.add("B");
		ParameterSubstitution macro = new ParameterSubstitution(formalParameterList, tables,evaluator);
		ArrayList actualParameterList = new ArrayList();
		actualParameterList.add("a");
		actualParameterList.add("b");
		Tables substituted = macro.substitute(actualParameterList,evaluator);
		assertEquals(bodyTables("a","b"),substituted);
	}
	public void testNoDoubleSubstitutions() {
		Tables tables = bodyTables("A","B");
		ArrayList formalParameterList = new ArrayList();
		formalParameterList.add("A");
		formalParameterList.add("B");
		ParameterSubstitution macro = new ParameterSubstitution(formalParameterList, tables,evaluator);
		ArrayList actualParameterList = new ArrayList();
		actualParameterList.add("B");
		actualParameterList.add("b");
		Tables substituted = macro.substitute(actualParameterList,evaluator);
		assertEquals(bodyTables("B","b"),substituted);
	}
}
