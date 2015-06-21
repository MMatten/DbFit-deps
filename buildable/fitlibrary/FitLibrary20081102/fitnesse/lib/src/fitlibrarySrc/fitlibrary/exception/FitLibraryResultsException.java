package fitlibrary.exception;

import fitlibrary.table.Cell;
import fitlibrary.utility.TestResults;

public class FitLibraryResultsException extends FitLibraryException {
	private Result[] results;
	
	/** Any Result with an index beyond the end of the row leads to an extra Cell being added.
	 */
	public FitLibraryResultsException(Result[] results) {
		super("Only works with DoFixture actions");
		this.results = results;
	}
	public FitLibraryResultsException(Result result) {
		this(new Result[]{ result });
	}
	public Result[] getResults() {
		return results;
	}
	public static FitLibraryResultsException pass(int cellIndex) {
		return new FitLibraryResultsException(new PassResult(cellIndex));
	}
	public static FitLibraryResultsException fail(int cellIndex, String result) {
		return new FitLibraryResultsException(new FailResult(cellIndex,"<hr/>"+result));
	}
	public static FitLibraryResultsException error(int cellIndex, String msg) {
		return new FitLibraryResultsException(new ErrorResult(cellIndex,"<hr/>"+msg));
	}
	public static FitLibraryResultsException exception(int cellIndex, Exception e) {
		return new FitLibraryResultsException(new ExceptionResult(cellIndex,e));
	}
	public static FitLibraryResultsException ignore(int cellIndex) {
		return new FitLibraryResultsException(new IgnoreResult(cellIndex));
	}
	public static FitLibraryResultsException show(String htmlString) {
		return new FitLibraryResultsException(new ShowResult(htmlString));
	}
	
	public static abstract class Result {
		private int cellIndex;

		public Result(int cellIndex) {
			this.cellIndex = cellIndex;
		}
		public int getPosition() {
			return cellIndex;
		}
		public abstract void decorate(Cell cell, TestResults testResults);
	}
	public static class PassResult extends Result {
		public PassResult(int cellIndex) {
			super(cellIndex);
		}
		public void decorate(Cell cell, TestResults testResults) {
			cell.pass(testResults);
		}
	}
	public static class FailResult extends Result {
		protected String htmlString;

		public FailResult(int cellIndex, String htmlString) {
			super(cellIndex);
			this.htmlString = htmlString;
		}
		public void decorate(Cell cell, TestResults testResults) {
			if ("".equals(htmlString))
				cell.fail(testResults);
			else
				cell.fail(testResults, htmlString);
		}
	}
	public static class ExceptionResult extends Result {
		private Throwable throwable;
		
		public ExceptionResult(int cellIndex, Throwable throwable) {
			super(cellIndex);
			this.throwable = throwable;
		}
		public void decorate(Cell cell, TestResults testResults) {
			cell.error(testResults, throwable);
		}
	}
	public static class ErrorResult extends FailResult {
		public ErrorResult(int cellIndex, String htmlString) {
			super(cellIndex,htmlString);
		}
		public void decorate(Cell cell, TestResults testResults) {
			cell.error(testResults, htmlString);
		}
	}
	public static class IgnoreResult extends Result {
		public IgnoreResult(int cellIndex) {
			super(cellIndex);
		}
		public void decorate(Cell cell, TestResults testResults) {
			cell.ignore(testResults);
		}
	}
	public static class ShowResult extends FailResult {
		public ShowResult(String htmlString) {
			super(100,htmlString);
		}
		public void decorate(Cell cell, TestResults testResults) {
			cell.setText(htmlString);
		}
	}
}
