/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse.workflow.caller;

import fitlibrary.definedAction.DefinedActionsRepositoryStandard;
import fitlibrary.definedAction.MultiParameterSubstitution;
import fitlibrary.global.TemporaryPlugBoardForRuntime;
import fitlibrary.table.Row;
import fitlibrary.traverse.workflow.DoCaller;
import fitlibrary.traverse.workflow.DoTraverseInterpreter;
import fitlibrary.utility.TestResults;

public class MultiDefinedActionCaller extends DoCaller {
	private final DoTraverseInterpreter doTraverse;
	private final String methodName;
	private final MultiParameterSubstitution multiParameterSubstitution;
	private final boolean furtherRows;

	public MultiDefinedActionCaller(Row row, DoTraverseInterpreter doTraverse) {
		this.doTraverse = doTraverse;
		furtherRows = row.hasFurtherRows();
		methodName = row.text(0,doTraverse);
		multiParameterSubstitution = TemporaryPlugBoardForRuntime.definedActionsRepository().lookupMulti(methodName);
	}
	@Override
	public String ambiguityErrorMessage() {
		return "multi defined action "+methodName;
	}
	@Override
	public boolean isValid() {
		return furtherRows && multiParameterSubstitution != null;
	}
	@Override
	@SuppressWarnings("unused")
	public Object run(Row row, TestResults testResults) throws Exception {
		return new MultiDefinedActionTraverse(multiParameterSubstitution,doTraverse);
	}
}
