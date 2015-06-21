package fitlibrary.specify.workflow;

import fitlibrary.DoFixture;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.exception.FitLibraryResultsException;
import fitlibrary.exception.FitLibraryResultsException.ErrorResult;
import fitlibrary.exception.FitLibraryResultsException.ExceptionResult;
import fitlibrary.exception.FitLibraryResultsException.FailResult;
import fitlibrary.exception.FitLibraryResultsException.IgnoreResult;
import fitlibrary.exception.FitLibraryResultsException.PassResult;
import fitlibrary.exception.FitLibraryResultsException.Result;
import fitlibrary.exception.FitLibraryResultsException.ShowResult;

public class ExceptionAddsCells extends DoFixture {
	public boolean addCellsTo(String s, String t, String u) { 
		Result[] results = {
				new ExceptionResult(0,new FitLibraryException("whoops")),
				new ErrorResult(1,"msg"),
				new IgnoreResult(2),
				new PassResult(3),
				new FailResult(5,"other"), 
				new FailResult(6,"<i>added:</i>"+s+"<hr/>next line"), 
				new ShowResult("added: "+t+"<hr>next line")
		};
		throw new FitLibraryResultsException(results);
	}
}
 	