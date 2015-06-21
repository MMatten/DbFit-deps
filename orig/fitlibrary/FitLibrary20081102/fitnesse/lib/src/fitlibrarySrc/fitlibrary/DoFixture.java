/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary;

import java.util.List;

import fit.Parse;
import fitlibrary.suite.FlowControl;
import fitlibrary.suite.InFlowPageRunner;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.workflow.DoEvaluator;
import fitlibrary.traverse.workflow.DoTraverse;
import fitlibrary.utility.TableListener;
import fitlibrary.utility.TestResults;

/** An alternative to fit.ActionFixture
	@author rick mugridge, july 2003
  * 
  * See the specifications for examples
*/
public class DoFixture extends FitLibraryFixture implements DoEvaluator {
	private DoTraverse doTraverse = new DoTraverse(this);
	private InFlowPageRunner inFlow = new InFlowPageRunner(this,false);
	
	public DoFixture() {
    	setTraverse(doTraverse);
	}
	public DoFixture(Object sut) {
		this();
	    setSystemUnderTest(sut);
	}

    protected void setTraverse(DoTraverse traverse) {
    	this.doTraverse = traverse;
    	super.setTraverse(traverse);
    }
    // Dispatched to from Fixture when a DoFixture is the first fixture in a storytest
    final public void interpretTables(Parse tables) {
		inFlow.run(new Tables(tables),0,new TableListener(listener,testResults()));
    }
    // Dispatched to from Fixture when Fixture is doTabling the tables one by one (not in flow)
	final public Object interpretAfterFirstRow(Table table, TestResults testResults) {
    	return ((DoTraverse)traverse()).interpretInFlow(table,testResults);
    }
	/**
	 * if (stopOnError) then we don't continue intepreting a table
	 * as there's been a problem
	 */
	public void setStopOnError(boolean stopOnError) {
		inFlow.setStopOnError(stopOnError);
	}
	protected void abandon() {
		inFlow.setAbandon(true);
		doTraverse.abandonStorytest();
	}
	protected Object getExpectedResult() {
		return doTraverse.getExpectedResult();
	}
	protected void setExpandDefinedActions(boolean expandDefinedActions) {
		doTraverse.setExpandDefinedActions(expandDefinedActions);
	}
	public Object interpretInFlow(Table firstTable, TestResults testResults) {
		return doTraverse.interpretInFlow(firstTable,testResults);
	}
	final public Object interpretWholeTable(Table table, TableListener tableListener) {
		return doTraverse.interpretWholeTable(table,tableListener);
	}
	public void setUp(Table firstTable, TestResults testResults) {
		doTraverse.setUp(firstTable,testResults);
	}
	public void setUp() throws Exception {
		doTraverse.setUp();
	}
	public void tearDown(Table firstTable, TestResults testResults) {
		doTraverse.tearDown(firstTable,testResults);
	}
	public void tearDown() throws Exception {
		doTraverse.tearDown();
	}
	// --------- Interpretation ---------------------------------------
	protected void setGatherExpectedForGeneration(boolean gatherExpectedForGeneration) {
		doTraverse.setGatherExpectedForGeneration(gatherExpectedForGeneration);
	}
	public void setSetUpFixture(SetUpFixture setUpFixture) {
		doTraverse.setSetUpTraverse(setUpFixture.getSetUpTraverse());
	}
	void finishSettingUp() {
		doTraverse.setSettingUp(false);
	}
	public void registerFlowControl(FlowControl flowControl) {
		doTraverse.registerFlowControl(flowControl);
	}
	public List methodsThatAreVisible() {
		return doTraverse.methodsThatAreVisible();
	}
}
