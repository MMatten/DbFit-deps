/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse.workflow;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import fitlibrary.DefineAction;
import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.exception.FitLibraryExceptionWithHelp;
import fitlibrary.exception.IgnoredException;
import fitlibrary.exception.NotRejectedException;
import fitlibrary.exception.parse.ParseException;
import fitlibrary.exception.table.ExtraCellsException;
import fitlibrary.exception.table.MissingCellsException;
import fitlibrary.parser.Parser;
import fitlibrary.parser.graphic.GraphicParser;
import fitlibrary.parser.graphic.ObjectDotGraphic;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.traverse.CommentTraverse;
import fitlibrary.traverse.function.CalculateTraverse;
import fitlibrary.traverse.function.ConstraintTraverse;
import fitlibrary.traverse.workflow.caller.DefinedActionCaller;
import fitlibrary.typed.NonGenericTyped;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.ClassUtility;
import fitlibrary.utility.TestResults;

public class DoTraverse extends DoTraverseInterpreter {
	private int becomesTimeout = 1000;

	// Methods that can be called within DoTraverse.
	// Each element is of the form "methodName/argCount"
	private final static String[] methodsThatAreVisibleAsActions = { 
		"useTemplate/1", "template/1", "abandonStorytest/0", "setStopOnError/1","becomesTimeout/1",
		"comment/0", "ignored/0", "ignoreTable/0",
		"clearDynamicVariables/0", "addDynamicVariablesFromFile/1", "setVariables/0", "to/0", "get/1",
		"setExpandDefinedActions/1", "defineAction/0", "defineAction/1"
	};
	
	protected DoTraverse() {
		super();
	}
	public DoTraverse(Object sut) {
		super(sut);
	}
	public DoTraverse(TypedObject typedObject) {
		super(typedObject);
	}

	//------------------- Methods that are visible as actions (the rest are hidden):
	public List methodsThatAreVisible() {
		return Arrays.asList(methodsThatAreVisibleAsActions);
	}
	public void becomesTimeout(int timeout) {
		this.becomesTimeout = timeout;
	}
	/** To support defined actions */
	public UseTemplateTraverse useTemplate(String name) {
		return new UseTemplateTraverse(name);
	}
	/** To support defined actions */
	public DefinedActionTraverse template(String name) {
		return new DefinedActionTraverse();
	}
	/** The rest of the storytest is ignored (but is not coloured as ignored) */
	public void abandonStorytest() {
		flowControl.abandon();
	}
	/** When (stopOnError), don't continue intepreting a table if there's been a problem */
	public void setStopOnError(boolean stopOnError) {
		flowControl.setStopOnError(stopOnError);
	}
    /** The rest of the table is ignored (and not coloured) */
	public CommentTraverse comment() {
		return new CommentTraverse();
	}
    /** The rest of the table is ignored (and the first row is coloured as ignored) */
	public CommentTraverse ignored() {
		return new CommentTraverse(true);
	}
	public CommentTraverse ignoreTable() {
		return new CommentTraverse(true);
	}
	public boolean clearDynamicVariables() {
		getDynamicVariables().clearAll();
		return true;
	}
	public boolean addDynamicVariablesFromFile(String fileName) {
		return getDynamicVariables().addPropertiesFromFile(fileName);
	}
	public SetVariableFixture setVariables() {
		return new SetVariableFixture();
	}
	public void set(final Row row, TestResults testResults) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("set");
		getDynamicVariables().put(row.text(1,this), row.text(3,this));
	}
	public String to(String s) {
		return s;
	}
	public String get(String s) {
		return s;
	}
	public DefineAction defineAction() {
		return new DefineAction();
	}
	public DefineAction defineAction(String wikiClassName) {
		return new DefineAction(wikiClassName);
	}
	//------------------- Postfix Special Actions:
	/** Check that the result of the action in the first part of the row matches
	 *  the expected value in the last cell of the row.
	 */
	public void is(TestResults testResults, final Row row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("DoTraverseIs");
		CalledMethodTarget target = findMethodFromRow(row,0,less);
		Cell expectedCell = row.last();
		if (gatherExpectedForGeneration)
			expectedResult = target.getResult(expectedCell,testResults);
		target.invokeAndCheck(row.rowTo(1,row.size()-2),expectedCell,testResults,false);
	}
	/** Check that the result of the action in the first part of the row, as a string, matches
	 *  the regular expression in the last cell of the row.
	 */
	public void matches(TestResults testResults, final Row row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("DoTraverseMatches");
		CalledMethodTarget target = findMethodFromRow(row,0,less);
		Cell expectedCell = row.last();
		if (gatherExpectedForGeneration)
			expectedResult = target.getResult(expectedCell,testResults);
		String result = target.invoke(row.rowTo(1,row.size()-2),testResults,false).toString();
		if (Pattern.compile(".*"+expectedCell.text(this)+".*",Pattern.DOTALL).matcher(result).matches())
			expectedCell.pass(testResults);
		else
			expectedCell.fail(testResults, result,this);
	}
	/** Check that the result of the action in the first part of the row, as a string becomes equals
	 *  to the given value within the timeout period.
	 */
	public void becomes(TestResults testResults, final Row row) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("DoTraverseMatches");
		CalledMethodTarget target = findMethodFromRow(row,0,less);
		Cell expectedCell = row.last();
		Row actionPartOfRow = row.rowTo(1,row.size()-2);
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < becomesTimeout) {
			Object result = target.invoke(actionPartOfRow, testResults, false);
			if (target.getResultParser().matches(expectedCell, result, testResults))
				break;
			try {
				Thread.sleep(Math.min(100,becomesTimeout/10));
			} catch (Exception e) {
				//
			}
		}
		target.invokeAndCheck(actionPartOfRow,expectedCell,testResults,false);
	}

	//------------------- Prefix Special Actions:
	/** Check that the result of the action in the rest of the row matches
	 *  the expected value in the last cell of the row.
	 */
	public void check(final Row row, TestResults testResults) throws Exception {
		int less = 3;
		if (row.size() < less)
			throw new MissingCellsException("DoTraverseCheck");
		CalledMethodTarget target = findMethodFromRow(row,1, less);
		Cell expectedCell = row.last();
		if (gatherExpectedForGeneration)
			expectedResult = target.getResult(expectedCell,testResults);
		target.invokeAndCheck(row.rowFrom(2),expectedCell,testResults,false);
	}
	public void reject(Row row, TestResults testResults) throws Exception {
		not(row,testResults);
	}
    /** Same as reject()
     * @param testResults 
     */
	public void not(Row row, TestResults testResults) throws Exception {
		Cell notCell = row.cell(0);
		expectedResult = new Boolean(false);
		try {
			Object result = callMethodInRow(row,testResults,false);
		    if (!(result instanceof Boolean))
		        notCell.error(testResults,new NotRejectedException());
		    else if (((Boolean)result).booleanValue())
		        notCell.fail(testResults);
		    else
		        notCell.pass(testResults);
		} catch (IgnoredException e) {
			// No result, so ignore
		} catch (FitLibraryException e) {
			if (e instanceof ParseException)
				notCell.pass(testResults);
			else
				row.error(testResults,e);
		} catch (Exception e) {
		    notCell.pass(testResults);
		}
	}
	/** Add a cell containing the result of the rest of the row.
     *  HTML is not altered, so it can be viewed directly.
     */
	public void show(Row row, TestResults testResults) throws Exception {
		try {
			CalledMethodTarget target = findMethodFromRow(row,1, 2);
		    Object result = target.invoke(row.rowFrom(2),testResults,true);
		    row.addCell(target.getResultString(result));
		} catch (IgnoredException e) {
			// No result, so ignore
		}
	}
	/** Add a cell containing the result of the rest of the row,
     *  shown as a Dot graphic.
	 * @param testResults 
     */
	public void showDot(Row row, TestResults testResults) throws Exception {
		Parser adapter = new GraphicParser(new NonGenericTyped(ObjectDotGraphic.class));
		try {
		    Object result = callMethodInRow(row,testResults, true);
		    row.addCell(adapter.show(new ObjectDotGraphic(result)));
		} catch (IgnoredException e) { // No result, so ignore
		}
	}
	/** Checks that the action in the rest of the row succeeds.
     *  o If a boolean is returned, it must be true.
     *  o For other result types, no exception should be thrown.
     *  It's no longer needed, because the same result can now be achieved with a boolean method.
	 * @param testResults 
     */
	public void ensure(Row row, TestResults testResults) throws Exception {
		expectedResult = new Boolean(true);
		try {
		    Object result = callMethodInRow(row,testResults, true);
		    row.cell(0).passOrFail(testResults,((Boolean)result).booleanValue());
		} catch (IgnoredException e) {
			// No result, so ignore
		} catch (Exception e) {
		    row.cell(0).fail(testResults);
		}
	}
	/** The rest of the row is ignored. 
     */
	public void note(Row row, TestResults testResults) throws Exception {
		//		Nothing to do
	}
    /** To allow for DoTraverse to be used without writing any fixturing code.
     */
	public void start(Row row, TestResults testResults) throws Exception {
		String className = row.text(1,this);
		if (row.size() != 2)
		    throw new ExtraCellsException("DoTraverseStart");
		try {
		    setSystemUnderTest(ClassUtility.newInstance(className));
		} catch (Exception e) {
		    throw new FitLibraryExceptionWithHelp("Unknown class: "+className,
		            "UnknownClass.DoTraverseStart");
		}
	}
	/** To allow for a CalculateTraverse to be used for the rest of the table.
     */
	public CalculateTraverse calculate(Row row, TestResults testResults) throws Exception {
		if (row.size() != 1)
		    throw new ExtraCellsException("DoTraverseCalculate");
		CalculateTraverse traverse;
		if (this.getClass() == DoTraverse.class)
			traverse =  new CalculateTraverse(getTypedSystemUnderTest());
		else
			traverse = new CalculateTraverse(this);
		traverse.theSetUpTearDownAlreadyHandled();
		return traverse;
	}
	/** To allow for a ConstraintTraverse to be used for the rest of the table.
     */
	public ConstraintTraverse constraint(Row row, TestResults testResults) throws Exception {
		if (row.size() != 1)
		    throw new ExtraCellsException("DoTraverseConstraint");
		ConstraintTraverse traverse = new ConstraintTraverse(this);
		traverse.theSetUpTearDownAlreadyHandled();
		return traverse;
	}
	/** To allow for a failing ConstraintTraverse to be used for the rest of the table.
     */
	public ConstraintTraverse failingConstraint(Row row, TestResults testResults) throws Exception {
		if (row.size() != 1)
		    throw new ExtraCellsException("DoTraverseConstraint");
		ConstraintTraverse traverse = new ConstraintTraverse(this,false);
		traverse.theSetUpTearDownAlreadyHandled();
		return traverse;
	}
	/** To allow for example storytests in user guide to pass overall, even if they have failures within them. */
	public void expectedTestResults(Row row, TestResults testResults) throws Exception {
		if (testResults.matches(row.text(1,this),row.text(3,this),row.text(5,this),row.text(7,this))) {
			testResults.clear();
			row.cell(0).pass(testResults);
		} else {
			String results = testResults.toString();
			testResults.clear();
			row.cell(0).fail(testResults,results,this);
		}
	}
	public void oo(final Row row, TestResults testResults) throws Exception {
		if (row.size() < 3)
			throw new MissingCellsException("DoTraverseOO");
		String object = row.text(1,this);
		String className = getDynamicVariable(object+".class");
		if (className == null || "".equals(className))
			throw new FitLibraryException("Wiki object doesn't have a class defined");
		Row macroRow = row.rowFrom(2);
		new DefinedActionCaller(object,className,macroRow,this).run(row, testResults);
	}
}
