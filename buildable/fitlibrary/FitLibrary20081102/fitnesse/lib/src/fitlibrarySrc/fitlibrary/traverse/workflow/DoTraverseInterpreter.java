package fitlibrary.traverse.workflow;

import java.util.ArrayList;
import java.util.List;

import fit.Counts;
import fit.Fixture;
import fit.Parse;
import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.closure.LookupMethodTarget;
import fitlibrary.collection.CollectionSetUpTraverse;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.exception.IgnoredException;
import fitlibrary.exception.method.AmbiguousActionException;
import fitlibrary.exception.method.AmbiguousNameException;
import fitlibrary.exception.method.MissingMethodException;
import fitlibrary.suite.FlowControl;
import fitlibrary.suite.InFlowPageRunner;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.Traverse;
import fitlibrary.traverse.workflow.caller.ActionCaller;
import fitlibrary.traverse.workflow.caller.FixtureCaller;
import fitlibrary.traverse.workflow.caller.DefinedActionCaller;
import fitlibrary.traverse.workflow.caller.PostFixSpecialCaller;
import fitlibrary.traverse.workflow.caller.SpecialCaller;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.TableListener;
import fitlibrary.utility.TestResults;

public abstract class DoTraverseInterpreter extends Traverse implements DoEvaluator {
	protected boolean gatherExpectedForGeneration;
	protected Object expectedResult = new Boolean(true); // Used for UI code generation
	private CollectionSetUpTraverse setUpTraverse = null; // delegate for setup phase
	private boolean settingUp = true;
	protected FlowControl flowControl = new FlowControl() { // Default: do nothing
		public void abandon() {
			//
		}
		public void setStopOnError(boolean stopOnError) {
			//
		}
	};
	private boolean expandDefinedActions = false;
	
	public DoTraverseInterpreter() {
		//
	}
	public DoTraverseInterpreter(Object sut) {
		super(sut);
	}
	public DoTraverseInterpreter(TypedObject typedObject) {
		super(typedObject);
	}
	public Object interpretAfterFirstRow(Table table, TestResults testResults) {
		Object result = null;
    	int size = table.size();
    	for (int rowNo = 1; rowNo < size; rowNo++) {
    		Row row = table.row(rowNo);
    		try {
				result = interpretRow(row,testResults,null);
				if (result instanceof DoEvaluator) {
					DoEvaluator resultingEvaluator = (DoEvaluator)result;
					resultingEvaluator.setDynamicVariables(dynamicVariables);
					resultingEvaluator.setUp(table, testResults);
    				resultingEvaluator.interpretInFlow(new Table(row),testResults);
					resultingEvaluator.tearDown(table, testResults);
    				break;
    			} else if (result instanceof Evaluator) {
					interpretEvaluator((Evaluator)result,new Table(row),testResults);
    				break;
    			} else if (getAlienTraverseHandler().isAlienTraverse(result)) {
    				getAlienTraverseHandler().doTable(result, new Table(row),testResults);
    				break;
    			}
    		} catch (Exception ex) {
    			row.error(testResults,ex);
    		}
    	}
    	return result;
	}
	// overridden in DomainTraverse (with quite separate code)
    public Object interpretWholeTable(Table table, TableListener tableListener) {
    	return interpretWholeTable(table, tableListener.getTestResults());
	}
	public Object interpretWholeTable(Table table, TestResults testResults) {
		try {
			Fixture fixtureByName = fixtureByName(table,testResults);
			if (fixtureByName != null && fixtureByName.getClass() == Fixture.class)
				fixtureByName = null;
			Object result = interpretRow(table.row(0),testResults,fixtureByName);
			if (result instanceof Evaluator) {
				interpretEvaluator((Evaluator)result, table, testResults);
				return result;
			} else if (getAlienTraverseHandler().isAlienTraverse(result)) {
				getAlienTraverseHandler().doTable(result,table,testResults);
			} else // do the rest of the table with this traverse
				return interpretInFlow(table,testResults);
		} catch (Throwable e) {
            table.error(testResults,e);
		}
		return null;
	}
	private void interpretEvaluator(Evaluator evaluator, Table table, TestResults testResults) {
		evaluator.setDynamicVariables(dynamicVariables);
		evaluator.setUp(table,testResults);
		evaluator.interpretAfterFirstRow(table,testResults);
		evaluator.tearDown(table,testResults);
	}
	protected Fixture fixtureByName(Table table, TestResults testResults) {
        if (table.row(0).text(0,this).trim().equals(""))
            return null;
		try {
			return new OpenFixture(testResults.getCounts()).getLinkedFixtureWithArgs(table.parse);
		} catch (Throwable e) {
			return null;
		}
	}
	static class OpenFixture extends Fixture {
		public OpenFixture(Counts counts) {
			this.counts = counts;
		}
		public Fixture getLinkedFixtureWithArgs(Parse tables) throws Throwable {
			return super.getLinkedFixtureWithArgs(tables);
		}
	}
    // @Overridden in CollectionSetUpTraverse
    public Object interpretInFlow(Table table, TestResults testResults) {
    	return interpretAfterFirstRow(table,testResults);
    }
    public Object interpretRow(Row row, TestResults testResults, Fixture fixtureByName) {
    	final Cell cell = row.cell(0);
    	if (cell.hasEmbeddedTable()) {
    		setExpectedResult(null);
    		interpretInnerTables(cell.innerTables(),testResults);
    		return null;
    	}
    	setExpectedResult(new Boolean(true));
    	try {
    		DoCaller[] actions = { 
    				new DefinedActionCaller(row, this),
    				new SpecialCaller(row,switchSetUp()),
    				new PostFixSpecialCaller(row,switchSetUp()),
    				new FixtureCaller(fixtureByName),
    				new ActionCaller(row,switchSetUp()) };
			checkForAmbiguity(actions);
			for (int i = 0; i < actions.length; i++)
				if (actions[i].isValid())
					return actions[i].run(row, testResults);
			methodsAreMissing(actions);
    	} catch (IgnoredException ex) {
    		//
    	} catch (Exception ex) {
    		row.error(testResults, ex);
    	}
    	return null;
    }
	public CalledMethodTarget findMethodFromRow(final Row row, int from, int less) throws Exception {
		return findMethodByActionName(row.rowFrom(from), row.size() - less);
	}
	/** Is overridden in subclass SequenceTraverse to process arguments differently
	 */
	public CalledMethodTarget findMethodByActionName(Row row, int allArgs) throws Exception {
		return LookupMethodTarget.findMethodInEverySecondCell(this, row, allArgs);
	}
	private static void checkForAmbiguity(DoCaller[] actions) {
		final String AND = " AND ";
		String message = "";
		List valid = new ArrayList();
		for (int i = 0; i < actions.length; i++) {
			if (actions[i].isValid()) {
				String ambiguityErrorMessage = actions[i].ambiguityErrorMessage();
				valid.add(ambiguityErrorMessage);
				message += AND+ambiguityErrorMessage;
			}
		}
		if (valid.size() > 1)
			throw new AmbiguousActionException(message.substring(AND.length()));
	}
	private void methodsAreMissing(DoCaller[] actions) {
		// It would be better to pass all these exceptions on in a wrapper exception.
		// Then they can be sorted and organised into <hr> lines in the cell.
		final String OR = " OR: ";
		String missingMethods = "";
		String missingAt = "";
		String ambiguousMethods = "";
		for (int i = 0; i < actions.length; i++)
			if (actions[i].isProblem()) {
				Exception exception = actions[i].problem();
				if (exception instanceof MissingMethodException) {
					MissingMethodException missingMethodException = (MissingMethodException) exception;
					missingMethods += OR+missingMethodException.getMethodSignature();
					missingAt = missingMethodException.getClasses();
				} else if (exception instanceof AmbiguousNameException) {
					AmbiguousNameException ambiguousNameException = (AmbiguousNameException) exception;
					ambiguousMethods += OR+ambiguousNameException.getMessage();
				} else
					missingMethods += OR+exception.getMessage();
			}
		String message = "";
		if (!"".equals(missingMethods))
			message += "Missing methods: "+missingMethods.substring(OR.length());
		if (!"".equals(ambiguousMethods))
			message += " "+ambiguousMethods.substring(OR.length());
		if (!"".equals(missingAt))
			message += " in "+missingAt;
		throw new FitLibraryException(message.trim());
	}
	private void interpretInnerTables(Tables tables, TestResults testResults) {
		new InFlowPageRunner(this,false).run(tables,0,new TableListener(testResults));
	}
	public void setSetUpTraverse(CollectionSetUpTraverse setUpTraverse) {
		this.setUpTraverse = setUpTraverse;
		setUpTraverse.setOuterContext(this);
	}
	public void setSetUpTraverse(Object object) {
		setSetUpTraverse(new CollectionSetUpTraverse(object));
	}
	public void setSettingUp(boolean settingUp) {
		this.settingUp = settingUp;
	}
	public DoTraverseInterpreter switchSetUp() {
		if (settingUp && setUpTraverse != null)
			return setUpTraverse;
		return this;
	}
	public void finishSettingUp() {
		setSettingUp(false);
	}
	public void registerFlowControl(FlowControl flowController) {
		this.flowControl = flowController;
	}
	public void setExpectedResult(Object expectedResult) {
		this.expectedResult = expectedResult;
	}
	public Object getExpectedResult() {
		return expectedResult;
	}
	protected Object callMethodInRow(Row row, TestResults testResults, boolean catchError) throws Exception {
		return findMethodFromRow(row,1, 2).invoke(row.rowFrom(2),testResults,catchError);
	}
	protected CalledMethodTarget findSpecialMethod(String name) {
		return LookupMethodTarget.findSpecialMethod(this, name);
	}
	public void setGatherExpectedForGeneration(boolean gatherExpectedForGeneration) {
		this.gatherExpectedForGeneration = gatherExpectedForGeneration;
	}
	public boolean toExpandDefinedActions() {
		return expandDefinedActions;
	}
	public void setExpandDefinedActions(boolean expandDefinedActions) {
		this.expandDefinedActions = expandDefinedActions;
	}
}
