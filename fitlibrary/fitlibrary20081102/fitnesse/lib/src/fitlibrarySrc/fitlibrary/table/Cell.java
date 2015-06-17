/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.table;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import fit.Fixture;
import fit.Parse;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.exception.IgnoredException;
import fitlibrary.exception.table.NestedTableExpectedException;
import fitlibrary.exception.table.SingleNestedTableExpected;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.Traverse;
import fitlibrary.utility.ExtendedCamelCase;
import fitlibrary.utility.ParseUtility;
import fitlibrary.utility.TestResults;

public class Cell extends ParseNode {
    private boolean cellIsInHiddenRow = false;
    
	public Cell(Parse parse) {
        super(parse);
    }
    public Cell() {
        this("");
    }
    public Cell(String cellText) {
        this(new Parse("td",cellText,null,null));
    }
    public Cell(String cellText, int cols) {
        this(new Parse("td colspan="+cols,cellText,null,null));
	}
	public Cell(Tables innerTables) {
		this(new Parse("td","",innerTables.parse,null));
	}
	public Cell(Cell cell) {
		this();
		if (cell.hasEmbeddedTable())
			setInnerTables(cell.getEmbeddedTables());
		else
			setText(cell.fullText());
	}
	public String text(Evaluator evaluator) {
        if (parse.body == null)
            return "";
        if (evaluator == null)
        	return parse.text();
        return evaluator.getDynamicVariables().resolve(parse.text());
    }
	public String text() {
        if (parse.body == null)
            return "";
        return parse.text();
    }
	public String camelledText(Evaluator evaluator) {
		return ExtendedCamelCase.camel(text(evaluator));
	}
   public String textLower(Evaluator evaluator) {
        return text(evaluator).toLowerCase();
    }
    public boolean matchesText(String text, Evaluator evaluator) {
        return text(evaluator).toLowerCase().equals(text.toLowerCase());
    }
    public boolean isBlank(Evaluator evaluator) {
        return text(evaluator).equals("");
    }
    public boolean hasEmbeddedTable() {
        return parse.parts != null;
    }
    public Tables innerTables() {
        if (!hasEmbeddedTable())
            throw new NestedTableExpectedException();
        return new Tables(parse.parts);
    }
    public Cell copy() {
        return new Cell(ParseUtility.copyParse(parse));
    }
    public boolean equals(Object object) {
        if (!(object instanceof Cell))
            return false;
        Cell other = (Cell)object;
        return parse.body.equals(other.parse.body);
    }
     public void expectedElementMissing(TestResults testResults) {
        fail(testResults);
        addToBody(label("missing"));
    }
    public void actualElementMissing(TestResults testResults) {
        fail(testResults);
        addToBody(label("surplus"));
    }
	public void unexpected(TestResults testResults, String s) {
        fail(testResults);
        addToBody(label("unexpected "+s));
	}
    public void actualElementMissing(TestResults testResults, String value) {
        fail(testResults);
        parse.body = Fixture.gray(Fixture.escape(value.toString()));
        addToBody(label("surplus"));
    }
    public void pass(TestResults testResults) {
    	if (cellIsInHiddenRow)
    		System.out.println("Bug: colouring a cell in a hidden table");
    	super.pass(testResults);
    }
    public void fail(TestResults testResults) {
    	if (cellIsInHiddenRow)
    		System.out.println("Bug: colouring a cell in a hidden table");
    	super.fail(testResults);
    }
    public void fail(TestResults testResults, String msg, Evaluator evaluator) {
    	if ("".equals(parse.body) && !hasEmbeddedTable()) {
    		failHtml(testResults,msg);
    		return;
    	}
        fail(testResults);
        String resolved = "";
        if (!text().equals(text(evaluator)))
        	resolved = " = "+text(evaluator);
        addToBody(resolved+label("expected") + "<hr>" + Fixture.escape(msg)
                + label("actual"));
    }
    public void fail(TestResults testResults, String msg) {
    	if ("".equals(parse.body) && !hasEmbeddedTable()) {
    		failHtml(testResults,msg);
    		return;
    	}
        fail(testResults);
        addToBody(label("expected") + "<hr>" + Fixture.escape(msg)
                + label("actual"));
    }
    public void failHtml(TestResults testResults, String msg) {
        fail(testResults);
        addToBody(msg);
    }
    public void error(TestResults testResults, Throwable e) {
    	if (cellIsInHiddenRow)
    		System.out.println("Bug: colouring a cell in a hidden table");
        addToBody(exceptionMessage(e));
        parse.addToTag(ERROR);
        testResults.exception();
    }
   public void error(TestResults testResults, String msg) {
    	if (cellIsInHiddenRow)
    		System.out.println("Bug: colouring a cell in a hidden table");
        addToBody("<hr/>" + Fixture.label(msg));
        parse.addToTag(ERROR);
        testResults.exception();
    }
	private static String exceptionMessage(Throwable throwable) {
		Throwable exception = unwrapThrowable(throwable);
        if (exception instanceof IgnoredException)
            return "";
        if (exception instanceof FitLibraryException)
            return "<hr/>" + Fixture.label(Traverse.escapeHtml(exception.getMessage()));
        final StringWriter buf = new StringWriter();
        exception.printStackTrace(new PrintWriter(buf));
        return "<hr><pre><div class=\"fit_stacktrace\">"
            + (buf.toString()) + "</div></pre>";
    }
	public static Throwable unwrapThrowable(Throwable throwable) {
		Throwable exception = throwable;
		while (exception.getClass().equals(InvocationTargetException.class))
            exception = ((InvocationTargetException) exception).getTargetException();
		return exception;
	}
    public void ignore(TestResults testResults) {
    	if (cellIsInHiddenRow)
    		System.out.println("Bug: colouring a cell in a hidden table");
        ensureBodyNotNull();
        if (parse.tag.indexOf("class") >= 0)
        	throw new RuntimeException("Duplicate cell class in tag. Tag is already"+
        			parse.tag.substring(1,parse.tag.length()-2));
        parse.addToTag(IGNORE);
        testResults.ignore();
    }
    public Table getEmbeddedTable() {
        Tables tables = getEmbeddedTables();
        if (tables.size() != 1)
        	throw new SingleNestedTableExpected();
		return tables.table(0);
    }
    public Tables getEmbeddedTables() {
        if (!hasEmbeddedTable())
            throw new NestedTableExpectedException();
		return new Tables(parse.parts);
    }
    public String toString() {
        if (hasEmbeddedTable())
            return "Cell["+ParseUtility.toString(parse.parts)+"]";
        return "Cell["+text()+"]";
    }
    public void wrongHtml(TestResults counts, String actual) {
        fail(counts);
        addToBody(label("expected") + "<hr>" + actual
                + label("actual"));
    }
    private void addToBody(String msg) {
        if (hasEmbeddedTable()) {
            if (parse.parts.more == null)
                parse.parts.trailer = msg;
            else
                parse.parts.more.leader += msg;
        }
        else {
            ensureBodyNotNull();
            parse.addToBody(msg);
        }
    }
	public void setText(String text) {
		parse.body = text;
	}
	public String fullText() {
		return parse.body;
	}
	public void setUnvisitedEscapedText(String s) {
		setUnvisitedText(Fixture.escape(s));
	}
	public void setUnvisitedText(String s) {
		setText(Fixture.gray(s));
	}
	public void passIfBlank(TestResults counts, Evaluator evaluator) {
		if (isBlank(evaluator))
			pass(counts);
		else
			fail(counts,"",evaluator);
	}
	public void passIfNotEmbedded(TestResults counts) {
		if (!hasEmbeddedTable()) // already coloured
			pass(counts);
	}
	public void setIsHidden() {
		this.cellIsInHiddenRow = true;
	}
	public void setInnerTables(Tables tables) {
		parse.parts = tables.parse();
	}
	public void extraColumns(int i) {
		parse.addToTag(" colspan=\""+(i+1)+"\"");
	}
}
