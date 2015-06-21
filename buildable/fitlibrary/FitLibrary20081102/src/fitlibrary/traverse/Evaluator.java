/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse;

import fitlibrary.dynamicVariable.DynamicVariables;
import fitlibrary.table.Table;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.TestResults;

public interface Evaluator extends DomainAdapter {
	Object getOutermostContext();
	Evaluator getNextOuterContext();
	void setOuterContext(Evaluator outerContext);
	Object interpretAfterFirstRow(Table table, TestResults testResults);
	TypedObject getTypedSystemUnderTest();
    DynamicVariables getDynamicVariables();
	void setDynamicVariables(DynamicVariables dynamicVariables);
	void setUp(Table firstTable, TestResults testResults);
	void tearDown(Table firstTable, TestResults testResults);
	void setUp() throws Exception;
	void tearDown() throws Exception;
}
