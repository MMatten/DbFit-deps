/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package test.fitlibrary.parser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import fitlibrary.DoFixture;
import fitlibrary.parser.Parser;
import fitlibrary.parser.lookup.ResultParser;
import fitlibrary.table.Cell;
import fitlibrary.traverse.Traverse;
import fitlibrary.utility.TestResults;

public class TestCollectionParser extends TestCase {
    Collection list;
	public Collection aProp;

    public void setUp() {
        list = new ArrayList();
        list.add("1");
        list.add("2");
        list.add("3");
        aProp = new ArrayList();
        aProp.add("4");
        aProp.add("5");
        aProp.add("6");
    }
	public void testParserAlone() throws Exception {
		Parser parser = Traverse.asTyped(list).parser(new DoFixture());
		String cellText = "1, 2, 3";
		Cell cell = new Cell(cellText);
		assertEquals(list,parser.parseTyped(cell,new TestResults()).getSubject());
		assertTrue(parser.matches(cell, list,new TestResults()));
		assertEquals(cellText,parser.show(list));
	}
	public void testParserWithMethod() throws Exception {
		Method method = getClass().getMethod("aMethod", new Class[] {});
		ResultParser adapter = Traverse.asTypedObject(this).resultParser(new DoFixture(), method);
		adapter.setTarget(this);
		assertEquals(aProp,adapter.getResult());
		assertEquals("4, 5, 6",adapter.show(adapter.getResult()));
	}
	public Collection aMethod() {
		return aProp;
	}
}
