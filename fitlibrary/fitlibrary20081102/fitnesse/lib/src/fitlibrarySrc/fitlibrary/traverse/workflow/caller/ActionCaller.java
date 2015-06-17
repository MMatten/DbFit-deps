package fitlibrary.traverse.workflow.caller;

import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.table.Row;
import fitlibrary.traverse.workflow.DoCaller;
import fitlibrary.traverse.workflow.DoTraverseInterpreter;
import fitlibrary.utility.TestResults;

public class ActionCaller extends DoCaller {
	private CalledMethodTarget target;
	private String methodName;

	public ActionCaller(Row row, DoTraverseInterpreter switchSetUp) {
		methodName = methodName(row,switchSetUp);
		try {
			target = switchSetUp.findMethodByActionName(row,row.size()-1);
		} catch (Exception e) {
			setProblem(e);
		}
	}
	public boolean isValid() { // This has to be the last one, which is always run
		return target != null;
	}
	public Object run(Row row, TestResults testResults) throws Exception {
		Object result = target.invokeAndWrap(row.rowFrom(1),testResults);
		if (result instanceof Boolean)
			target.color(row,((Boolean)result).booleanValue(),testResults);
		return result;
	}
	public String ambiguityErrorMessage() {
		return methodName+"()";
	}
}
