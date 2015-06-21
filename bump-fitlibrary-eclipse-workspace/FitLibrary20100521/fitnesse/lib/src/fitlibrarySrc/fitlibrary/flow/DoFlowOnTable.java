/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.flow;

import fit.Fixture;
import fitlibrary.DefineAction;
import fitlibrary.DoFixture;
import fitlibrary.DomainFixture;
import fitlibrary.SelectFixture;
import fitlibrary.SetUpFixture;
import fitlibrary.collection.CollectionSetUpTraverse;
import fitlibrary.dynamicVariable.VariableResolver;
import fitlibrary.object.DomainFixtured;
import fitlibrary.runResults.ITableListener;
import fitlibrary.runResults.TestResults;
import fitlibrary.runtime.RuntimeContextInternal;
import fitlibrary.suite.SuiteEvaluator;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.workflow.DoEvaluator;
import fitlibrary.traverse.workflow.DoTraverse;
import fitlibrary.traverse.workflow.FlowEvaluator;
import fitlibrary.typed.TypedObject;

public class DoFlowOnTable implements DoFlowerOnTable {
	private final FlowEvaluator flowEvaluator;
	private final IScopeStack scopeStack;
	private final SetUpTearDown setUpTearDown;
	private final DoFlower doFlower;
	private RuntimeContextInternal runtime;

	public DoFlowOnTable(FlowEvaluator flowEvaluator, IScopeStack scopeStack, SetUpTearDown setUpTearDown, DoFlower doFlower) {
		this.flowEvaluator = flowEvaluator;
		this.scopeStack = scopeStack;
		this.setUpTearDown = setUpTearDown;
		this.doFlower = doFlower;
	}
	public void runTable(Table table, ITableListener tableListener, RuntimeContextInternal runtimeInternal) {
		this.runtime = runtimeInternal;
		runtimeInternal.setCurrentTable(table);
		TestResults testResults = tableListener.getTestResults();
		runtimeInternal.pushTestResults(testResults);
		try {
			runTable(table, testResults,tableListener);
		} finally {
			runtimeInternal.popTestResults();
		}
	}
	private void runTable(Table table, TestResults testResults, ITableListener tableListener) {
		for (int rowNo = 0; rowNo < table.size(); rowNo++) {
			Row row = table.at(rowNo);
			if (runtime.isAbandoned(testResults)) {
				row.ignore(testResults);
			} else if (doFlower.hasDomainCheck() && row.size() == 1 && row.text(0, flowEvaluator).equals("checks")) {
				doFlower.setDomainToCheck(); // Remove this hack later
			} else {
				try {
//					System.out.println("DoFlow row "+row);
					final Cell cell = row.at(0);
			    	if (cell.hasEmbeddedTables()) { // Doesn't allow for other cells in row...
			    		doFlower.runInnerTables(cell.getEmbeddedTables(), tableListener);
			    	} else {
			    		row = mapOddBalls(row,flowEvaluator);
//			    		System.out.println("DoFlow set current Row "+row);
			    		runtime.setCurrentRow(row);
//			    		System.out.println("DoFlow runtime = "+runtime.hashCode());
			    		TypedObject typedResult = flowEvaluator.interpretRow(row,testResults);
			    		Object subject = typedResult.getSubject();
//			    		System.out.println("DoFlow got "+subject);
			    		typedResult.injectRuntime(runtime);
			    		if (subject == null) {
			    			// Can't do anything useful with a null
			    		} else if (subject.getClass() == Fixture.class) {
			    			// Ignore it, as it does nothing.
			    		} else if (subject.getClass() == DoFixture.class || subject.getClass() == DoTraverse.class) {
			    			handleActualDoFixture((DoEvaluator)subject,row,testResults);
			    		} else if (subject.getClass() == SelectFixture.class) {
			    			runtime.showAsAfterTable("warning", "This is no longer needed");
			    			handleActualDoFixture((DoEvaluator)subject,row,testResults);
			    		} else if (subject instanceof DomainFixtured || subject instanceof DomainFixture) {
			    			handleDomainFixture(typedResult, row, testResults);
			    		} else if (subject instanceof SuiteEvaluator) {
			    			handleSuiteFixture((SuiteEvaluator)subject, typedResult, row, testResults);
			    		} else if (subject instanceof CollectionSetUpTraverse || subject instanceof SetUpFixture) {
			    			handleEvaluator(typedResult, (Evaluator) subject, rowNo, table, testResults);
			    			return;// have finished table
			    		} else if (subject instanceof DoEvaluator) {
			    			pushOnScope(typedResult,row,testResults);
			    		} else if (subject instanceof Evaluator) { // Calculate, etc
			    			handleEvaluator(typedResult, (Evaluator) subject, rowNo, table, testResults);
			    			return; // have finished table
			    		} else if (subject instanceof Fixture) {
			    			Table remainingTable = table.fromAt(rowNo).asTableOnParse();
			    			flowEvaluator.fitHandler().doTable((Fixture) subject,remainingTable,testResults,flowEvaluator);
			    			for (int i = 0; i < remainingTable.size(); i++)
			    				table.replaceAt(rowNo+i, remainingTable.at(i));
			    			return; // have finished table
			    		}
			    	}
				} catch (Exception ex) {
					row.error(testResults, ex);
				}
			}
		}
	}
	private void handleEvaluator(TypedObject typedResult, Evaluator subject,
			int rowNo, Table table, TestResults testResults) {
		Table restOfTable = table.fromAt(rowNo);
		int rest = restOfTable.size();
		Row row = table.at(rowNo);
		typedResult.injectRuntime(runtime);
		if (!(subject instanceof DefineAction)) // Don't want this as the storytest's main fixture/object
			pushOnScope(typedResult,row,testResults);
		else
			callSetUpSutChain(subject,row,testResults);
		subject.interpretAfterFirstRow(restOfTable, testResults);
		if (subject instanceof DefineAction)
			setUpTearDown.callTearDownOnSutChain(subject, row, testResults);
		if (restOfTable != table && restOfTable.size() > rest)
			for (int i = rest; i < restOfTable.size(); i++)
				table.add(restOfTable.at(i));
	}
	private void handleActualDoFixture(DoEvaluator doEvaluator, Row row, TestResults testResults) {
		// Unwrap an auto-wrap, keeping the type information
		TypedObject typedSystemUnderTest = doEvaluator.getTypedSystemUnderTest();
		if (!typedSystemUnderTest.isNull()) {
			pushOnScope(typedSystemUnderTest,row,testResults);
			typedSystemUnderTest.injectRuntime(runtime);
		}
	}
	private void handleSuiteFixture(SuiteEvaluator suiteEvaluator, TypedObject typedResult, Row row, TestResults testResults) {
		doFlower.setSuite(suiteEvaluator);
		setUpTearDown.callSuiteSetUp(suiteEvaluator, row, testResults);
		pushOnScope(typedResult,row,testResults);
	}
	private void handleDomainFixture(TypedObject typedResult, Row row, TestResults testResults) {
		pushOnScope(typedResult,row,testResults);
		doFlower.setDomainFixture(typedResult);
	}
	private void pushOnScope(TypedObject typedResult, Row row, TestResults testResults) {
		scopeStack.push(typedResult);
		callSetUpSutChain(typedResult.getSubject(), row, testResults);
	}
	private void callSetUpSutChain(Object sutInitially, final Row row, final TestResults testResults) {
		setUpTearDown.callSetUpOnSutChain(sutInitially, row, testResults);
	}
	public static Row mapOddBalls(Row row, VariableResolver evaluator) {
		// |add|class|as|name| => |add named|name|class|
		if (row.size() == 4 && "add".equals(row.text(0,evaluator)) && "as".equals(row.text(2,evaluator))) {
			String className = row.text(1,evaluator);
			row.at(0).setText("add named");
			row.at(1).setText(row.text(3,evaluator));
			row.at(2).setText(className);
			row.removeElementAt(3);
		}
		return row;
	}
	public interface DoFlower {
		void runInnerTables(Tables embeddedTables, ITableListener tableListener);
		void setDomainFixture(TypedObject typedResult);
		void setSuite(SuiteEvaluator suiteEvaluator);
		boolean hasDomainCheck();
		void setDomainToCheck();
	}
}