/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package test.fitlibrary;

import fit.Fixture;
import fit.Parse;
import fitlibrary.utility.ParseUtility;
import junit.framework.TestCase;

public class TestFixtureFixture extends TestCase {
    public void test1() throws Exception {
        Parse table = new Parse("<table><tr><td>fit.ff.FixtureUnderTest</td>"+
                "<td>r</td>"+
                "</tr></table>\n");
        new Fixture().doTables(table);
        ParseUtility.printParse(table,"test");
    }
}
