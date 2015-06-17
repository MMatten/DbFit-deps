package fitlibrary.object;

import java.lang.reflect.InvocationTargetException;

import fitlibrary.closure.Closure;
import fitlibrary.closure.LookupMethodTarget;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.ref.EntityReference;
import fitlibrary.traverse.Evaluator;
import fitlibrary.typed.Typed;
import fitlibrary.utility.ExtendedCamelCase;

public class NonGenericFinder implements Finder {
	public static final String FIND = "find";
	public static final String SHOW = "show";
    private String findExceptionMessage, showExceptionMethod;
    private Closure findIntMethod, findStringMethod;
	private Closure showMethod;
    private EntityReference referenceParser;

	public NonGenericFinder(Typed typed, Evaluator evaluator) {
    	String shortClassName = typed.simpleClassName();
		referenceParser = EntityReference.create(shortClassName.toLowerCase());
		
		final Class[] intArg = { int.class };
		final Class[] stringArg = { String.class };
		final Class[] showArg = { typed.asClass() };
		final String findName = ExtendedCamelCase.camel(FIND+" "+shortClassName);
		final String showMethodName = ExtendedCamelCase.camel(SHOW+" "+shortClassName);
		final String showMethodSignature = showMethodName+"("+shortClassName+" arg) { }";
		String potentialClasses = LookupMethodTarget.identifiedClassesInOutermostContext(evaluator, true);
		
		findExceptionMessage = "EITHER "+shortClassName+
			" is (1) a Value Object. So missing parse method: "+
			"public static "+shortClassName+" parse(String s) { } in class "+typed.getClassName()+
			"; OR (2) an Entity. So missing finder method: "+
			"public "+shortClassName+" find"+shortClassName+"(String key) { } in "+potentialClasses;
		showExceptionMethod = "Missing show method: public String "+showMethodSignature+" in "+potentialClasses;
		
		findIntMethod = LookupMethodTarget.findFixturingMethod(evaluator,findName,intArg);
		findStringMethod = LookupMethodTarget.findFixturingMethod(evaluator,findName,stringArg);
		showMethod = LookupMethodTarget.findFixturingMethod(evaluator,showMethodName,showArg);
	}
	private Object callFindStringMethod(String text) throws Exception {
        if (findStringMethod != null)
            return findStringMethod.invoke(new String[]{ text });
        if ("".equals(text))
        	return null;
        throw new FitLibraryException(findExceptionMessage);
    }
	public Object find(final String text) throws Exception, IllegalAccessException, InvocationTargetException {
		if (findIntMethod != null) {
            int index = 0;
            try {
                index = referenceParser.getIndex(text);
            } catch (FitLibraryException e) {
                return callFindStringMethod(text);
            }
			return findIntMethod.invoke(new Integer[]{ new Integer(index) });
        }
        return callFindStringMethod(text);
	}
	public String show(Object result) throws Exception {
        Object[] args = new Object[]{ result };
		if (showMethod != null)
            return showMethod.invoke(args).toString();
		throw new FitLibraryException(showExceptionMethod);
	}
	public boolean hasFinderMethod() {
		return findIntMethod != null || findStringMethod != null;
	}
}
