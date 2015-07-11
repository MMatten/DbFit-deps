/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 10/12/2006
*/

package fitlibrary.suite;

import fit.FitServerBridge;
import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Traverse;
import fitlibrary.traverse.workflow.DoEvaluator;
import fitlibrary.utility.TableListener;
import fitlibrary.utility.TestResults;
import fitlibraryGeneric.typed.GenericTypedFactory;

public class IntegratedSuiteRunner implements SuiteRunner {
	private SuiteEvaluator suiteEvaluator;
	protected boolean abandoned = false;

	public IntegratedSuiteRunner(SuiteEvaluator suiteEvaluator) {
		this.suiteEvaluator = suiteEvaluator;
		suiteEvaluator.setDynamicVariable(Traverse.FITNESSE_URL_KEY,FitServerBridge.FITNESSE_URL);
	}
	public void runFirstStorytest(Tables tables, TableListener tableListener) {
		TestResults testResults = tableListener.getTestResults();
		Table firstTable = tables.table(0);
		suiteMethod("suiteSetUp", firstTable, tableListener.getTestResults());
		suiteEvaluator.setUp(firstTable, testResults);
		suiteEvaluator.interpretAfterFirstRow(firstTable,testResults);
		tableListener.tableFinished(firstTable);
		runEachStorytest(tables,1,tableListener);
	}
	private void suiteMethod(String methodName, Table firstTable, TestResults results) {
		try {
			CalledMethodTarget methodTarget = new GenericTypedFactory().asTypedObject(suiteEvaluator).
				optionallyFindMethodOnTypedObject(methodName,0,suiteEvaluator,false);
			if (methodTarget != null)
				methodTarget.invoke();
		} catch (Exception e) {
			firstTable.error(results, e);
		}
	}
	public void runStorytest(Tables tables, TableListener tableListener) {
		runEachStorytest(tables,0,tableListener);
	}
	private void runEachStorytest(Tables tables, int fromTable, TableListener tableListener) {
		TestResults testResults = tableListener.getTestResults();
		testResults.setSuiteFixtureSoDoNotTearDown(true);
		for (int i = fromTable; i < tables.size(); i++) {
			Table table = tables.table(i);
			if (testResults.isAbandoned())
				tableListener.tableFinished(table);
			else {
				Object result = suiteEvaluator.interpretWholeTable(table,tableListener);
				tableListener.tableFinished(table);
				if (abandoned)
					table.ignore(testResults);
				if (result instanceof DoEvaluator) {
					testResults.setSuiteFixtureSoDoNotTearDown(false);
					DoEvaluator doEvaluator = (DoEvaluator)result;
					doEvaluator.setRuntimeContext(suiteEvaluator.getCopyOfDynamicVariables());
					doEvaluator.setUp(table,testResults);
					new InFlowPageRunner(doEvaluator,testResults).run(tables,i+1,tableListener,true);
//					doEvaluator.tearDown(table,testResults);
					break;
				}
			}
		}
		tableListener.storytestFinished();
	}
	public void exit() {
		// We're unable to see any problems on tearDown!
		Row row = new Row();
		row.addCell();
		suiteMethod("suiteTearDown",new Table(row),new TestResults());
	}
}
