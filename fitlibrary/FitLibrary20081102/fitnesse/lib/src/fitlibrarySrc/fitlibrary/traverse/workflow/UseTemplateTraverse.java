package fitlibrary.traverse.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fitlibrary.exception.FitLibraryException;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Traverse;
import fitlibrary.utility.TestResults;

public class UseTemplateTraverse extends Traverse {
	private String templateName;

	public UseTemplateTraverse(String templateName) {
		this.templateName = templateName;
	}
	public Object interpretAfterFirstRow(Table table, TestResults testResults) {
		Tables tables = table.getTables();
		for (int t = 0; t < tables.size(); t++) {
			Table defTable = tables.table(t);
			Row firstRow = defTable.row(0);
			if (firstRow.size() == 2 && firstRow.text(0,this).equals("template") && firstRow.text(1,this).equals(templateName)) {
				interpret(defTable, table, testResults);
				return null;
			}
		}
		throw new FitLibraryException("Missing definition for template "+templateName);
	}
	private void interpret(Table definingTable, Table callingTable, TestResults testResults) {
		Row actualParameterNames = callingTable.row(1);
		int parameterCount = actualParameterNames.size();
		DefinedActionTraverse defineTemplateTraverse = new DefinedActionTraverse(definingTable,parameterCount);
		int errors = 0;
		for (int r = 2; r < callingTable.size(); r++) {
			Row row = callingTable.row(r);
			List parameters = new ArrayList();
			for (int c = 0; c < row.size(); c++)
				parameters.add(row.text(c,this));
			TestResults results = new TestResults();
			Tables resultingTables = defineTemplateTraverse.call(parameters,results);
			if (results.passed())
				row.pass(testResults);
			else {
				Row argsRow = appendTableToReport(callingTable, actualParameterNames, errors, parameters, resultingTables);
				passOnColourings(testResults, row, argsRow, results);
				errors++;
			}
		}
	}
	private Row appendTableToReport(Table callingTable, Row actualParameterNames, int errors, List parameters, Tables resultingTables) {
		Table commentTable = new Table();
		commentTable.newRow().addCell("comment");
		Table paramsTable = new Table();
		Row templateRow = paramsTable.newRow();
		templateRow.addCell("use template");
		templateRow.addCell(templateName);
		Row paramsRow = paramsTable.newRow();
		for (int c = 0; c < actualParameterNames.size(); c++)
			paramsRow.addCell(new Cell(actualParameterNames.text(c,this)));
		Row argsRow = paramsTable.newRow();
		for (Iterator it = parameters.iterator(); it.hasNext(); )
			argsRow.addCell((String)it.next());
		paramsTable.evenUpRows();
		commentTable.newRow().addCell(new Cell(new Tables(paramsTable)));
		commentTable.newRow().addCell(new Cell(resultingTables));
		callingTable.insertTable(errors,commentTable);
		return argsRow;
	}
	private void passOnColourings(TestResults testResults, Row row, Row argsRow, TestResults results) {
		if (results.failed()) {
			row.fail(testResults);
			argsRow.fail(testResults);
		} else if (results.errors()) {
			row.error(testResults, new FitLibraryException(""));
			argsRow.error(testResults, new FitLibraryException(""));
		} else {
			row.ignore(testResults);
			argsRow.ignore(testResults);
		}
	}
}
