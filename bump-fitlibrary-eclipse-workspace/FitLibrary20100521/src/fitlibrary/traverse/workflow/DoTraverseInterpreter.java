/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse.workflow;

import java.util.ArrayList;
import java.util.List;

import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.closure.ICalledMethodTarget;
import fitlibrary.exception.AbandonException;
import fitlibrary.exception.FitLibraryExceptionInHtml;
import fitlibrary.exception.IgnoredException;
import fitlibrary.exception.method.AmbiguousActionException;
import fitlibrary.exception.method.AmbiguousNameException;
import fitlibrary.exception.method.MissingMethodException;
import fitlibrary.flow.DoAutoWrapper;
import fitlibrary.flow.IDoAutoWrapper;
import fitlibrary.global.PlugBoard;
import fitlibrary.runResults.TestResults;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.traverse.Traverse;
import fitlibrary.traverse.workflow.caller.CreateFromClassNameCaller;
import fitlibrary.traverse.workflow.caller.DefinedActionCaller;
import fitlibrary.traverse.workflow.caller.DoActionCaller;
import fitlibrary.traverse.workflow.caller.MultiDefinedActionCaller;
import fitlibrary.traverse.workflow.caller.PostFixSpecialCaller;
import fitlibrary.traverse.workflow.caller.SpecialCaller;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.ExtendedCamelCase;
import fitlibrary.utility.option.None;
import fitlibrary.utility.option.Option;
import fitlibrary.utility.option.Some;
import fitlibraryGeneric.typed.GenericTypedObject;

public abstract class DoTraverseInterpreter extends Traverse implements DoEvaluator {
	private IDoAutoWrapper doAutoWrapper = new DoAutoWrapper(this);
	protected boolean sequencing = false;

	public DoTraverseInterpreter() {
		//
	}
	public DoTraverseInterpreter(Object sut) {
		super(sut);
	}
	public DoTraverseInterpreter(TypedObject typedObject) {
		super(typedObject);
	}
	@Override
	public Object interpretAfterFirstRow(Table table, TestResults testResults) {
		// Now handled by DoFlow
		return null;
	}
    // @Overridden in CollectionSetUpTraverse
    public Object interpretInFlow(Table table, TestResults testResults) {
    	return null; // Leave it here, as override it.
    }
    public TypedObject interpretRow(Row row, TestResults testResults) {
    	return doAutoWrapper.wrap(interpretRowBeforeWrapping(row,testResults));
    }
    public TypedObject interpretRowBeforeWrapping(Row row, TestResults testResults) {
    	try {
    		DoCaller[] actions = createDoCallers(row);
    		Option<TypedObject> result = interpretSimpleRow(row,testResults,actions);
    		if (result.isSome())
    			return result.get();
    		methodsAreMissing(actions,possibleSeq(row));
    	} catch (IgnoredException ex) {
    		//
    	} catch (AbandonException e) {
    		row.ignore(testResults);
    	} catch (Exception ex) {
    		row.error(testResults, ex);
    	}
    	return GenericTypedObject.NULL;
    }
    protected Option<TypedObject> interpretSimpleRow(Row row, TestResults testResults, DoCaller[] actions) throws Exception {
		Option<TypedObject> result = pickCaller(actions, row, testResults);
		if (result.isSome())
			return result;
		if (row.size() > 2) {
			Option<TypedObject> seqResult = trySequenceCall(row, testResults);
			if (seqResult.isSome())
				return seqResult;
		}
		return None.none();
    }
    protected Option<TypedObject> trySequenceCall(Row row, TestResults testResults) throws Exception {
    	if (sequencing)
    		return None.none();
    	sequencing = true;
    	try {
    		return interpretSimpleRow(row, testResults, createDoCallers(row));
    	} finally {
    		sequencing = false;
    	}
    }
    private Option<TypedObject> pickCaller(DoCaller[] actions, Row row, TestResults testResults) throws Exception {
		for (int i = 0; i < actions.length; i++)
			if (actions[i].isValid()) {
				TypedObject result = actions[i].run(row, testResults);
				if (getRuntimeContext().isAbandoned(testResults) && !testResults.problems())
					row.ignore(testResults);
				return new Some<TypedObject>(result);
			}
		return None.none();
    }
	public DoCaller[] createDoCallers(Row row) {
		DoCaller[] actions = { 
				new DefinedActionCaller(row, getRuntimeContext()),
				new MultiDefinedActionCaller(row, getRuntimeContext()),
				new SpecialCaller(row,this,PlugBoard.lookupTarget),
				new PostFixSpecialCaller(row,this),
				new CreateFromClassNameCaller(row,this),
				new DoActionCaller(row,this) };
		checkForAmbiguity(actions);
		return actions;
	}
	private String possibleSeq(Row row) {
		if (row.size() < 3)
			return "";
		String result = "public Type "+ExtendedCamelCase.camel(row.text(0, this))+"(";
		if (row.size() > 0)
			result += "Type p1";
		for (int i = 2; i < row.size(); i++)
			result += ", Type p"+i;
		return result+") {}";
	}
	public ICalledMethodTarget findMethodFromRow(Row row, int from, int extrasCellsOnEnd) throws Exception {
		return findMethodByActionName(row.fromAt(from), row.size() - from - extrasCellsOnEnd - 1);
	}
	public ICalledMethodTarget findMethodFromRow222(Row row, int from, int less) throws Exception {
		return findMethodFromRow(row,from,less-from-1);
	}
	public CalledMethodTarget findMethodByActionName(Row row, int allArgs) throws Exception {
		if (sequencing)
			return PlugBoard.lookupTarget.findTheMethodMapped(row.text(0,this), allArgs, this);
		return PlugBoard.lookupTarget.findMethodInEverySecondCell(this, row, allArgs);
	}
	private static void checkForAmbiguity(DoCaller[] actions) {
		final String AND = " AND ";
		String message = "";
		List<String> valid = new ArrayList<String>();
		for (int i = 0; i < actions.length; i++) {
			if (actions[i].isValid()) {
				String ambiguityErrorMessage = actions[i].ambiguityErrorMessage();
				valid.add(ambiguityErrorMessage);
				message += AND+ambiguityErrorMessage;
			}
		}
		if (valid.size() > 1)
			throw new AmbiguousActionException(message.substring(AND.length()));
	}
	private void methodsAreMissing(DoCaller[] actions, String possibleSequenceCall) {
		List<String> missingMethods = new ArrayList<String>();
		List<Class<?>> possibleClasses = new ArrayList<Class<?>>();
		String ambiguousMethods = "";
		for (int i = 0; i < actions.length; i++)
			if (actions[i].isProblem()) {
				Exception exception = actions[i].problem();
				if (exception instanceof MissingMethodException) {
					MissingMethodException missingMethodException = (MissingMethodException) exception;
					missingMethods.addAll(missingMethodException.getMethodSignature());
					for (Class<?> c : missingMethodException.getClasses())
						if (!possibleClasses.contains(c))
							possibleClasses.add(c);
				} else if (exception instanceof AmbiguousNameException) {
					AmbiguousNameException ambiguousNameException = (AmbiguousNameException) exception;
					ambiguousMethods += "<li>"+ambiguousNameException.getMessage()+"</li>";
				} else if (exception instanceof ClassNotFoundException) {
					ClassNotFoundException cnf = (ClassNotFoundException) exception;
					if (cnf.getCause() != null) {
						System.out.println("methodsAreMissing(): CNFE: "+exception.getMessage()+": "+cnf.getCause().getMessage());
					} else
						System.out.println("methodsAreMissing(): CNFE: "+exception.getMessage());
					missingMethods.add(exception.getMessage());
				} else
					missingMethods.add(exception.getMessage());
			}
		if (!missingMethods.isEmpty() && !possibleSequenceCall.isEmpty())
			missingMethods.add(possibleSequenceCall);
		String message = "";
		if (ambiguousMethods.isEmpty())
			message += "Missing class or ";
		if (!missingMethods.isEmpty())
			message += "Missing method. Possibly:"+MissingMethodException.htmlListOfSignatures(missingMethods);
		if (!ambiguousMethods.isEmpty())
			message += "<ul>"+ambiguousMethods+"</ul>";
		if (!possibleClasses.isEmpty())
			message += "<hr/>Possibly in class:"+MissingMethodException.htmlListOfClassNames(possibleClasses);
		throw new FitLibraryExceptionInHtml(message.trim());
	}
	public DoTraverseInterpreter switchSetUp() {
		return this;
	}
	protected Object callMethodInRow(Row row, TestResults testResults, boolean catchError, Cell operatorCell) throws Exception {
		return findMethodFromRow222(row,1, 2).invokeForSpecial(row.fromAt(2),testResults,catchError,operatorCell);
	}
}
