/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.table;

import fitlibrary.dynamicVariable.VariableResolver;
import fitlibrary.exception.FitLibraryShowException;
import fitlibrary.exception.table.MissingCellsException;
import fitlibrary.global.PlugBoard;
import fitlibrary.runResults.TestResults;
import fitlibrary.tableOnParse.CellOnParse;
import fitlibrary.utility.ExtendedCamelCase;

public class RowOnList extends TableElementOnList<Row,Cell> implements Row {
    private boolean rowIsHidden = false;
    
    public RowOnList() {
        super("tr");
    }
    @Override
	public Cell at(int i) {
    	if (i < 0 || i >= size())
    		throw new MissingCellsException("");
		return super.at(i);
	}
    public String text(int i, VariableResolver resolver) {
        return at(i).text(resolver);
    }
    public Cell addCell() {
    	Cell cell = TableFactory.cell("");
		add(cell);
		return cell;
    }
	public Cell addCell(String text) {
        Cell cell = TableFactory.cell(text);
        add(cell);
        return cell;
	}
    public Cell addCell(String text, int cols) {
        Cell cell = new CellOnParse(text);
        cell.setColumnSpan(cols);
        add(cell);
        return cell;
    }
	public Cell lastCell() {
		return last();
	}
    @Override
	public void pass(TestResults testResults) {
    	if (rowIsHidden)
    		System.out.println("Bug: colouring a cell in a hidden table");
    	super.pass(testResults);
    }
    @Override
	public void fail(TestResults testResults) {
    	if (rowIsHidden)
    		System.out.println("Bug: colouring a cell in a hidden table");
    	super.fail(testResults);
    }
    @Override
	public void error(TestResults testResults, Throwable throwable) {
		Throwable e = PlugBoard.exceptionHandling.unwrapThrowable(throwable);
		if (e instanceof FitLibraryShowException)
        	handleShow((FitLibraryShowException) e);
        else
        	at(0).error(testResults,e);
    }
    public void missing(TestResults testResults) {
        at(0).expectedElementMissing(testResults);
    }
    public void ignore(TestResults testResults) {
    	for (int i = 0; i < size(); i++)
    		at(i).ignore(testResults);
    }
	public void setIsHidden() {
		this.rowIsHidden  = true;
		for (int i = 0; i < size(); i++)
			at(i).setIsHidden();
	}
	public void passKeywords(TestResults testResults) {
		for (int i = 0; i < size(); i += 2)
			at(i).pass(testResults);
	}
	public Row deepCopy() {
		Row copy = TableFactory.row();
		for (int i = 0; i < size(); i++)
			copy.add((Cell) at(i).deepCopy());
		copy.setLeader(getLeader());
		copy.setTrailer(getTrailer());
		copy.setTagLine(getTagLine());
		return copy;
	}
	public String methodNameForPlain(VariableResolver resolver) {
		String name = "";
		for (int i = 0; i < size(); i += 2) {
			name += text(i,resolver);
			if ((i+1) < size())
				name += "|";
		}
		return name;
	}
	public String methodNameForCamel(VariableResolver resolver) {
		String name = "";
		for (int i = 0; i < size(); i += 2)
			name += text(i,resolver)+" ";
		return ExtendedCamelCase.camel(name.trim());
	}
	public int argumentCount() {
		return size() / 2;
	}
	public int getColumnSpan() {
		int col = 0;
		for (int i = 0; i < size(); i++)
			col += at(i).getColumnSpan();
		return col;
	}
	public void setColumnSpan(int span) {
		if (isEmpty())
			addCell();
		lastCell().setColumnSpan(span - getColumnSpan() + lastCell().getColumnSpan());
	}
	@Override
	public String getType() {
		return "Row";
	}
	@Override
	protected Row newObject() {
		return new RowOnList();
	}
    private void handleShow(FitLibraryShowException exception) {
    	Cell cell = addCell();
    	cell.setText(exception.getResult().getHtmlString());
    	cell.shown();
    }
}
