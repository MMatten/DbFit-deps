/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.closure;

import fitlibrary.collection.CollectionTraverse;
import fitlibrary.collection.array.ArrayParser;
import fitlibrary.collection.list.ListParser;
import fitlibrary.collection.map.MapParser;
import fitlibrary.collection.set.SetParser;
import fitlibrary.exception.IgnoredException;
import fitlibrary.exception.parse.NoValueProvidedException;
import fitlibrary.parser.Parser;
import fitlibrary.parser.lookup.GetterParser;
import fitlibrary.parser.lookup.ParseDelegation;
import fitlibrary.parser.lookup.ResultParser;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.Traverse;
import fitlibrary.traverse.workflow.DoTraverse;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.TestResults;

/**
 * Manages calling a method on row cells, and possibly checking the result against a cell.
 * It constructs Parsers to use for getting cell values, comparisons, etc.
 */
public class CalledMethodTarget implements MethodTarget {
	final private Closure closure;
	final private Evaluator evaluator;
	private Parser[] parameterParsers;
	protected ResultParser resultParser = null;
	final private Object[] args;
	private String repeatString = null;
    private String exceptionString = null;
	private boolean everySecond = false;

    public CalledMethodTarget(Closure method, Evaluator evaluator) {
		this.closure = method;
		this.evaluator = evaluator;
		args = new Object[getParameterTypes().length];
		parameterParsers = closure.parameterParsers(evaluator);
		resultParser = closure.resultParser(evaluator);
   }
	public CalledMethodTarget(Evaluator evaluator) {
		this.evaluator = evaluator;
		parameterParsers = new Parser[0];
		args = new Object[0];
		this.closure = null;
		resultParser = null;
	}
	public boolean isValid() {
		return closure != null;
	}
	private Class getReturnType() {
		return closure.getReturnType();
	}
	public Class[] getParameterTypes() {
		return closure.getParameterTypes();
	}
	public Object invoke(Object[] arguments) throws Exception {
		return closure.invoke(arguments);
	}
	public TypedObject invokeTyped(Object[] arguments) throws Exception {
		return closure.invokeTyped(arguments);
	}
    public Object invoke(Cell cell, TestResults testResults) throws Exception {
    	collectCell(cell,0,cell.text(evaluator),testResults,true);
    	return invoke(args);
    }
    public TypedObject invokeTyped(Row row, TestResults testResults, boolean catchParseError) throws Exception {
		try {
			if (everySecond)
				collectCells(row,2,testResults,catchParseError);
			else
				collectCells(row,1,testResults,catchParseError);
		} catch (Exception e) {
			throw new IgnoredException(e); // Unable to call
		}
		return invokeTyped(args);
	}
    public Object invoke(Row row, TestResults testResults, boolean catchParseError) throws Exception {
		try {
			if (everySecond)
				collectCells(row,2,testResults,catchParseError);
			else
				collectCells(row,1,testResults,catchParseError);
		} catch (Exception e) {
			throw new IgnoredException(e); // Unable to call
		}
		return invoke(args);
	}
    private void collectCells(Row row, int step, TestResults testResults, boolean catchParseError) throws Exception {
		for (int argNo = 0; argNo < args.length; argNo++) {
			Cell cell = row.cell(argNo*step);
			collectCell(cell, argNo,cell.text(evaluator),testResults,catchParseError);
		}
	}
	private void collectCell(Cell cell, int argNo, String text, TestResults testResults, boolean catchParseError) throws Exception {
		try {
			if (!text.equals(repeatString))
				args[argNo] = parameterParsers[argNo].parseTyped(cell,testResults).getSubject();
		} catch (Exception e) {
			if (catchParseError) {
				cell.error(testResults,e);
				throw new IgnoredException();
			}
			throw e;
		}
	}
    public void invokeAndCheck(Row row, Cell expectedCell, TestResults testResults, boolean handleSubtype) {
        Object result = null;
        boolean exceptionExpected = exceptionString != null &&
                exceptionString.equals(expectedCell.text(evaluator));
        try {
            result = invoke(row,testResults,true);
            if (exceptionExpected) {
                expectedCell.fail(testResults);
                return;
            }
        } catch (IgnoredException ex) {
            return;
        } catch (Exception e) {
           if (exceptionExpected)
                expectedCell.pass(testResults);
            else
                expectedCell.error(testResults,e);
            return;
        }
        checkResult(expectedCell,result,true,handleSubtype,testResults);
    }
	public String getResult() throws Exception {
		return resultParser.show(invoke());
	}
	public boolean invokeAndCheckCell(Cell expectedCell, boolean matchedAlready, TestResults testResults) {
        try {
			return checkResult(expectedCell,invoke(),matchedAlready,false,testResults);
		} catch (Exception e) {
			expectedCell.error(testResults,e);
			return false;
		}
	}
	public Object invoke() throws Exception {
		return closure.invoke();
	}
	public boolean matches(Cell expectedCell, TestResults testResults) {
		try {
			return resultParser.matches(expectedCell,invoke(),testResults);
		} catch (Exception e) {
			return false;
		}
	}
	public boolean checkResult(Cell expectedCell, Object result, boolean showWrongs, boolean handleSubtype, TestResults testResults) {
		ResultParser valueParser = resultParser;
		if (handleSubtype && closure != null)
			valueParser = closure.specialisedResultParser(resultParser,result,evaluator);
		try {
			if (valueParser == null)
				throw new NoValueProvidedException();
			if (valueParser.isShowAsHtml()) {
				if (valueParser.matches(expectedCell,result,testResults)) {
					expectedCell.pass(testResults);
					return true;
				}
				expectedCell.wrongHtml(testResults,valueParser.show(result));
				return false;
			}
			if (valueParser.matches(expectedCell,result,testResults)) {
				expectedCell.passIfNotEmbedded(testResults);
				return true;
			}
			if (showWrongs && (result == null || !expectedCell.hasEmbeddedTable())) {
				expectedCell.fail(testResults,valueParser.show(result),evaluator);
			}
			return false;
		} catch (Exception e) {
			expectedCell.error(testResults,e);
			return false;
		}
	}
	public Object getResult(Cell expectedCell, TestResults testResults) {
		try {
			return resultParser.parseTyped(expectedCell,testResults).getSubject();
		} catch (Exception e) {
			return null;
		}
	}
	public void color(Row row, boolean right, TestResults testResults) throws Exception {
		if (!everySecond && row.cellExists(0))
			row.cell(0).passOrFail(testResults,right);
		else
			for (int i = 0; i < row.size(); i += 2)
				row.cell(i).passOrFail(testResults,right);
	}
	/** Defines the Strings that signifies that the value in the row above is
	 *  to be used again. Eg, it could be set to "" or to '"".
	 */
	public void setRepeatAndExceptionString(String repeatString, String exceptionString) {
		this.repeatString = repeatString;
		this.exceptionString = exceptionString;
	}
	public void setEverySecond(boolean everySecond) {
		this.everySecond = everySecond;
	}
	private Object wrapObjectWithTraverse(TypedObject typedResult) {
		Object result = typedResult.getSubject();
		if (isPrimitiveReturnType())
		    return result;
		if (result instanceof String)
		    return result;
		if (result instanceof Evaluator) {
			Evaluator resultEvaluator = (Evaluator)result;
			if (resultEvaluator != evaluator && resultEvaluator.getNextOuterContext() == null)
				return withOuter(resultEvaluator);
		    return result;
		}
		if (Traverse.getAlienTraverseHandler().isAlienTraverse(result))
		    return result;
		
		Class returnType = result.getClass();
		if (MapParser.applicableType(returnType) || ArrayParser.applicableType(returnType))
			return withOuter(typedResult.traverse(evaluator));
		if (SetParser.applicableType(returnType) || ListParser.applicableType(returnType)) {
			CollectionTraverse traverse = (CollectionTraverse) typedResult.traverse(evaluator);
			traverse.setActualCollection(result);
			return withOuter(traverse);
		}
		if (ParseDelegation.hasParseMethod(returnType))
		    return result;
		return withOuter(new DoTraverse(typedResult));
	}
	private Object withOuter(Evaluator inner) {
		inner.setOuterContext(evaluator);
		return inner;
	}
	private boolean isPrimitiveReturnType() {
	    return getReturnType().isPrimitive();
	}
	public Object invokeAndWrap(Row row, TestResults testResults) throws Exception {
        return wrapObjectWithTraverse(invokeTyped(row,testResults,true));
    }
	public String getResultString(Object result) throws Exception {
		if (getReturnType() == String.class)
			return (String)result;
		return resultParser.show(result);
	}
	public String toString() {
		return "MethodTarget["+closure+"]";
	}
    public Parser getResultParser() { // TEMP while adding FitLibrary2
        return resultParser;
    }
    public void setResultParser(GetterParser resultAdapter) { // TEMP while adding FitLibrary2
        this.resultParser = resultAdapter;
    }
    public Parser[] getParameterParsers() {
        return parameterParsers;
    }
    public void setParameterParsers(Parser[] parameterAdapters) {
        this.parameterParsers = parameterAdapters;
    }
    public void setTypedSubject(TypedObject typedObject) {
    	closure.setTypedSubject(typedObject);
    }
	public boolean returnsVoid() {
		return getReturnType() == void.class;
	}
	public boolean returnsBoolean() {
		return getReturnType() == boolean.class;
	}
}
