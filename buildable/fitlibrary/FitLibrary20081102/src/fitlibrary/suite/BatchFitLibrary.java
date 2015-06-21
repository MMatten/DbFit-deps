package fitlibrary.suite;

import fit.FixtureBridge;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Traverse;
import fitlibrary.typed.TypedFactory;
import fitlibrary.utility.TableListener;
import fitlibrary.utility.TestResults;

public class BatchFitLibrary {
    private boolean first = true;
	private SuiteRunner suiteRunner = new IndependentSuiteRunner(null);
	private TableListener tableListener = new TableListener();
	private Reportage reportage;

	public BatchFitLibrary() {
		this(new DefaultReportage());
	}
	public BatchFitLibrary(Reportage reportage) {
		this.reportage = reportage;
        installGenericProcessing();
	}
	public BatchFitLibrary(TableListener tableListener) {
		this.tableListener = tableListener;
        installGenericProcessing();
	}
	public TestResults doTables(Tables theTables) {
		tableListener.clearTestResults();
		if (first) {
			first = false;
			FixtureBridge fixtureBridge = new FixtureBridge();
			fixtureBridge.counts = tableListener.getTestResults().getCounts();
			Object firstObjectOfSuite = fixtureBridge.firstObject(theTables.parse(),tableListener.getTestResults());
			if (firstObjectOfSuite == null) {
				theTables.ignoreAndFinished(tableListener);
				return tableListener.getTestResults();
			}
			if (firstObjectOfSuite instanceof SuiteEvaluator) {
				suiteRunner = new IntegratedSuiteRunner((SuiteEvaluator)firstObjectOfSuite);
				reportage.showAllReports();
			} else
				suiteRunner = new IndependentSuiteRunner(firstObjectOfSuite);
			suiteRunner.runFirstStorytest(theTables,tableListener);
		} else
			suiteRunner.runStorytest(theTables,tableListener);
		return tableListener.getTestResults();
	}
	public void doTables(Tables theTables, TableListener listener) {
		this.tableListener = listener;
		doTables(theTables);
	}
	public void exit() {
		if (suiteRunner != null)
			suiteRunner.exit();
	}
	public static void installGenericProcessing() {
		try {
			Class typedFactory = Class.forName("fitlibraryGeneric.typed.GenericTypedFactory");
			Traverse.installTypedFactory((TypedFactory) typedFactory.newInstance());
		} catch (Exception ex) {
			// FitLibrary2 is not on the classpath, so don't install generics handling after all.
		}
	}
	public static class DefaultReportage implements Reportage {
		public void showAllReports() {
			//
		}
	}
}
