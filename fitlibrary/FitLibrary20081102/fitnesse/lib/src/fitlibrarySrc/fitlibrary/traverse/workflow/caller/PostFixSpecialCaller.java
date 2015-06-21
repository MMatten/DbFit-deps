package fitlibrary.traverse.workflow.caller;

import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.closure.LookupMethodTarget;
import fitlibrary.table.Row;
import fitlibrary.traverse.workflow.DoCaller;
import fitlibrary.traverse.workflow.DoTraverseInterpreter;
import fitlibrary.utility.TestResults;

public class PostFixSpecialCaller extends DoCaller {
	private String methodName;
	private CalledMethodTarget specialMethod;

	public PostFixSpecialCaller(Row row, DoTraverseInterpreter switchSetUp) {
		if (row.size() >= 2) {
			methodName = row.text(row.size()-2,switchSetUp);
			specialMethod = LookupMethodTarget.findPostfixSpecialMethod(switchSetUp, methodName);
			if (specialMethod != null) {
				try {
					switchSetUp.findMethodFromRow(row,0,3);
				} catch (Exception e) {
					setProblem(e);
				}
			}
		}
	}
	public boolean isValid() {
		return specialMethod != null && !isProblem();
	}
	public Object run(Row row, TestResults testResults) throws Exception {
		return specialMethod.invoke(new Object[] { testResults, row });
	}
	public String ambiguityErrorMessage() {
		return 	methodName+"(TestResults,Row)";
	}
}
