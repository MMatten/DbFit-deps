package fitlibrary.suite;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fit.Fixture;
import fit.FixtureBridge;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.workflow.DoEvaluator;
import fitlibrary.utility.TableListener;
import fitlibrary.utility.TestResults;

public class OutOfFlowPageRunner extends PageRunner {
	FixtureBridge bridge = new FixtureBridge();
	Map summary = new HashMap();
	
	public OutOfFlowPageRunner(Object fixtureLikeObject, Tables tables, TableListener tableListener) {
		summary.put("run date", new Date());
		summary.put("run elapsed time", new Fixture().new RunTime());
		Table table = tables.table(0);
		TestResults testResults = tableListener.getTestResults();
		if (fixtureLikeObject instanceof Fixture) {
			Fixture fixture = (Fixture) fixtureLikeObject;
			fixture.summary = summary;
			fixture.listener = tableListener.getListener();
			fixture.counts = tableListener.getTestResults().getCounts();
			try {
				fixture.doTable(table.parse);
			} catch (Exception e1) {
				tables.table(0).error(tableListener, e1);
			}
		} else {
			Evaluator evaluator = (Evaluator) fixtureLikeObject;
			evaluator.setUp(table, testResults);
			evaluator.interpretAfterFirstRow(table, tableListener.getTestResults());
			evaluator.tearDown(table, testResults);
		}
		tableListener.tableFinished(tables.table(0));

		for (int t = 1; t < tables.size(); t++) {
			if (ignored(tables,t,tableListener))
				break;
			table = tables.table(t);
			Object object = bridge.getFixture(table, testResults);
			if (object instanceof DoEvaluator) {
				DoEvaluator doEvaluator = (DoEvaluator) object;
				doEvaluator.setUp(table, testResults);
				doEvaluator.interpretAfterFirstRow(table, tableListener.getTestResults());
				tableListener.tableFinished(table);
				new InFlowPageRunner(doEvaluator,false).run(tables,t+1,tableListener);
				doEvaluator.tearDown(table, testResults); // Should this be before the tableListener.tableFinished()??
				return;
			}
			if (object instanceof Evaluator) { // But not DoEvaluator
				Evaluator evaluator = (Evaluator) object;
				evaluator.setUp(table, testResults);
				evaluator.interpretAfterFirstRow(table, tableListener.getTestResults());
				evaluator.tearDown(table, testResults);
			} else if (object instanceof Fixture) {
				try {
					Fixture fix = (Fixture) object;
					fix.listener = tableListener.getListener();
					fix.counts = tableListener.getTestResults().getCounts();
					fix.summary = summary;
					fix.doTable(table.parse);
				} catch (Exception e) {
					table.error(tableListener, e);
				}
			}
			tableListener.tableFinished(table);
		}
	}
}
