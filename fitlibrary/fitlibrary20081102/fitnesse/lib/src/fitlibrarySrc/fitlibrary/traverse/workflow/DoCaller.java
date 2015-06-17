package fitlibrary.traverse.workflow;

import fitlibrary.table.Row;
import fitlibrary.traverse.Evaluator;
import fitlibrary.utility.ExtendedCamelCase;
import fitlibrary.utility.TestResults;

public abstract class DoCaller {
	private Exception problem = null;
	
	public abstract boolean isValid();
	public abstract Object run(Row row, TestResults testResults) throws Exception;
	public abstract String ambiguityErrorMessage();

	protected static String methodName(Row row, Evaluator evaluator) {
		int parms = (row.size()-1) / 2 + 1;
		String name = row.text(0,evaluator);
		for (int i = 1; i < parms; i++)
			name += " "+row.text(i*2,evaluator);
		return ExtendedCamelCase.camel(name);
	}
	public Exception problem() {
		return problem;
	}
	public boolean isProblem() {
		return problem != null;
	}
	protected void setProblem(Exception exception) {
		problem = exception;
	}
}