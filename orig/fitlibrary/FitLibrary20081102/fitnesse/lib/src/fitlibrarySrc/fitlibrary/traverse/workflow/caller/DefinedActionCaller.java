package fitlibrary.traverse.workflow.caller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fitlibrary.DefineAction;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.Tables;
import fitlibrary.traverse.workflow.DoCaller;
import fitlibrary.traverse.workflow.DoTraverseInterpreter;
import fitlibrary.utility.ParameterSubstitution;
import fitlibrary.utility.TestResults;

public class DefinedActionCaller extends DoCaller {
	protected static final ThreadLocal definedActionCallsInProgress = new ThreadLocal();
	private ParameterSubstitution parameterSubstitution;
	private String methodName;
	private DoTraverseInterpreter doTraverse;
	private List actualArgs = new ArrayList();

	public DefinedActionCaller(Row row, DoTraverseInterpreter doTraverse) {
		this.doTraverse = doTraverse;
		methodName = methodName(row,doTraverse);
		actualArgs = actualArgs(row);
		this.parameterSubstitution = DefineAction.lookup(methodName,actualArgs);
	}
	public DefinedActionCaller(String object, String className, Row row, DoTraverseInterpreter doTraverse) {
		this.doTraverse = doTraverse;
		methodName = methodName(row,doTraverse);
		actualArgs.add(object);
		actualArgs(row,actualArgs);
		this.parameterSubstitution = DefineAction.lookupByClass(className,methodName,actualArgs,
				doTraverse.getDynamicVariables());
		if (parameterSubstitution == null)
			throw new FitLibraryException("Unknown defined action for object of class "+className);
	}
	public boolean isValid() {
		return parameterSubstitution != null;
	}
	public Object run(Row row, TestResults testResults) {
		Set set = ensureNotInfinite();
		set.add(parameterSubstitution);
		processDefinedAction(parameterSubstitution.substitute(actualArgs,doTraverse),row,testResults);
		set.remove(parameterSubstitution);
		return null;
	}
	public String ambiguityErrorMessage() {
		return "macro "+methodName;
	}
	private List actualArgs(Row row) {
		return actualArgs(row, new ArrayList());
	}
	private List actualArgs(Row row, List result) {
		for (int i = 1; i < row.size(); i += 2) {
			Cell cell = row.cell(i);
			if (cell.hasEmbeddedTable())
				result.add(cell.getEmbeddedTables());
			else
				result.add(cell.text(doTraverse));
		}
		return result;
	}
	private Set ensureNotInfinite() {
		Set set = (Set) definedActionCallsInProgress.get();
		if (set == null) {
			set = new HashSet();
			definedActionCallsInProgress.set(set);
		}
		if (set.contains(parameterSubstitution))
			throw new FitLibraryException("Infinite calling of defined actions");
		return set;
	}
	private void processDefinedAction(Tables definedActionBody, Row row, TestResults testResults) {
		TestResults subTestResults = new TestResults();
		for (int i = 0; i < definedActionBody.size(); i++)
			doTraverse.interpretWholeTable(definedActionBody.table(i),subTestResults);
		if (doTraverse.toExpandDefinedActions() || subTestResults.problems()) {
			row.addCommentRow(new Cell(definedActionBody));
			if (subTestResults.passed())
				row.passKeywords(testResults);
			else if (subTestResults.errors())
				for (int i = 0; i < row.size(); i += 2)
					row.cell(i).error(testResults, new FitLibraryException(""));
			else if (subTestResults.failed())
				for (int i = 0; i < row.size(); i += 2)
					row.cell(i).fail(testResults);
			else
				for (int i = 0; i < row.size(); i += 2)
					row.cell(i).ignore(testResults);
		} else
			for (int i = 0; i < row.size(); i += 2)
				row.cell(i).pass(testResults);
	}
}