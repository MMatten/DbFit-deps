package fitlibrary.traverse.workflow.caller;

import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.closure.LookupMethodTarget;
import fitlibrary.table.Row;
import fitlibrary.traverse.workflow.DoCaller;
import fitlibrary.traverse.workflow.DoTraverseInterpreter;
import fitlibrary.utility.TestResults;

public class SpecialCaller extends DoCaller {
	private String methodName;
	private CalledMethodTarget specialMethod;

	public SpecialCaller(Row row, DoTraverseInterpreter switchSetUp) {
		methodName = row.text(0,switchSetUp);
		specialMethod = LookupMethodTarget.findSpecialMethod(switchSetUp, methodName);
	}
	public boolean isValid() {
		return specialMethod != null;
	}
	public Object run(Row row, TestResults testResults) throws Exception {
		return specialMethod.invoke(new Object[] { row, testResults });
	}
	public String ambiguityErrorMessage() {
		return 	methodName+"(Row,TestResults)";
	}
}
