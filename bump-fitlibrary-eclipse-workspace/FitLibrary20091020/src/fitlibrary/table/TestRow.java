/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.table;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import fitlibrary.DoFixture;

public class TestRow {
	Row row = new Row();
	DoFixture evaluator = new DoFixture();
	
	@Test
	public void sizeOfEmptyRow() {
		assertThat(row.size(),is(0));
		assertThat(row.isEmpty(),is(true));
	}
	@Test public void columnSpanWithNoCellsIsZero() throws Exception {
		assertThat(row.getColumnSpan(),is(0));
	}
	@Test public void columnSpanWithOneCellWithNoColumnSpanSetIsOne() throws Exception {
		row.addCell("1");
		assertThat(row.getColumnSpan(),is(1));
	}
	@Test public void columnSpanWithOneCellWithColumnSpanSet() throws Exception {
		row.addCell("1", 4);
		assertThat(row.getColumnSpan(),is(4));
	}
	@Test public void columnSpanWithMultipleCellsWithAndWithoutColumnSpanSet() throws Exception {
		row.addCell("1", 4);
		row.addCell("2");
		row.addCell("3", 2);
		row.addCell("4");
		assertThat(row.getColumnSpan(),is(8));
	}
	@Test public void setColumnSpanWithNoCellsHadsOneCell() {
		row.setColumnSpan(4);
		assertThat(row.getColumnSpan(),is(4));
		assertThat(row.cell(0).getColumnSpan(),is(4));
	}
	@Test public void setColumnSpanWithOneCellWithNoColumnSpanSet() {
		row.addCell("1");
		row.setColumnSpan(4);
		assertThat(row.getColumnSpan(),is(4));
		assertThat(row.cell(0).getColumnSpan(),is(4));
	}
	@Test public void setColumnSpanWithOneCellWithColumnSpanSet() {
		row.addCell("1", 3);
		row.setColumnSpan(4);
		assertThat(row.getColumnSpan(),is(4));
		assertThat(row.cell(0).getColumnSpan(),is(4));
	}
	@Test public void setColumnSpanWithMultipleCellsWithAndWithoutColumnSpanSetAddToTheColumnSpanOfLastCell() throws Exception {
		row.addCell("1", 4);
		row.addCell("2");
		row.addCell("3", 2);
		row.addCell("4",4);
		row.setColumnSpan(15);
		assertThat(row.getColumnSpan(),is(15));
		assertThat(row.cell(0).getColumnSpan(),is(4));
		assertThat(row.cell(1).getColumnSpan(), is(1));
		assertThat(row.cell(2).getColumnSpan(),is(2));
		assertThat(row.cell(3).getColumnSpan(),is(8));
	}
	@Test
	public void plainMethodNameNoArg() {
		Row row2 = new Row("aa");
		assertThat(row2.methodNameForPlain(evaluator),is("aa"));
		assertThat(row2.methodNameForCamel(evaluator),is("aa"));
	}
	@Test
	public void plainMethodNameOneArg() {
		Row row2 = new Row("aa","1");
		assertThat(row2.methodNameForPlain(evaluator),is("aa|"));
		assertThat(row2.methodNameForCamel(evaluator),is("aa"));
	}
	@Test
	public void plainMethodNameOneArgTwoKeywords() {
		Row row2 = new Row("aa","1","bb");
		assertThat(row2.methodNameForPlain(evaluator),is("aa|bb"));
		assertThat(row2.methodNameForCamel(evaluator),is("aaBb"));
	}
	@Test
	public void plainMethodNameTwoArgs() {
		Row row2 = new Row("aa","1","bb","2");
		assertThat(row2.methodNameForPlain(evaluator),is("aa|bb|"));
		assertThat(row2.methodNameForCamel(evaluator),is("aaBb"));
	}
	@Test
	public void plainMethodNameTwoArgsThreeKeywords() {
		Row row2 = new Row("aa","1","bb","2",".");
		assertThat(row2.methodNameForPlain(evaluator),is("aa|bb|."));
		assertThat(row2.methodNameForCamel(evaluator),is("aaBbDot"));
	}
}
