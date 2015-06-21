package fitlibraryGeneric.object;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import fitlibrary.closure.Closure;
import fitlibrary.closure.LookupMethodTarget;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.object.Finder;
import fitlibrary.ref.EntityReference;
import fitlibrary.traverse.Evaluator;
import fitlibrary.utility.ExtendedCamelCase;
import fitlibraryGeneric.typed.GenericTyped;

public class GenericFinder implements Finder {
	public static final String FIND = "find";
	public static final String SHOW = "show";
	private GenericTyped typed;
    private String findExceptionMessage, showExceptionMethod;
    private Closure findIntMethod, findStringMethod;
	private Closure showMethod;
	private Closure genericFindStringMethod, genericShowMethod;
    private EntityReference referenceParser;

	public GenericFinder(GenericTyped typed, Evaluator evaluator) {
		this.typed = typed;
    	String shortClassName = typed.simpleClassName();
		referenceParser = EntityReference.create(shortClassName.toLowerCase());
		
		final Class[] intArg = { int.class };
		final Class[] stringArg = { String.class };
		final Class[] showArg = { typed.asClass() };
		final String findName = ExtendedCamelCase.camel(FIND+" "+shortClassName);
		String findMethodSignature = "public "+shortClassName+" find"+shortClassName+"(String key) { } ";
		String genericFindMethodSignature = "public "+shortClassName+" find"+shortClassName+"(String key, Type type) { } ";
		final String showMethodName = ExtendedCamelCase.camel(SHOW+" "+shortClassName);
		final String showMethodSignature = showMethodName+"("+shortClassName+" arg) { }";
		final String genericShowMethodSignature = showMethodName+"("+shortClassName+" arg, Type type) { }";
		String potentialClasses = LookupMethodTarget.identifiedClassesInOutermostContext(evaluator, true);
		
		findExceptionMessage = "EITHER "+shortClassName+
			" is (1) a Value Object. So missing parse method: "+
			"public static "+shortClassName+" parse(String s) { } in class "+typed.getClassName()+
			"; OR (2) an Entity. So missing finder method: "+findMethodSignature;
		if (typed.isGeneric())
			findExceptionMessage += " or missing generic finder method: "+genericFindMethodSignature;
		findExceptionMessage += "in "+potentialClasses;
		
		showExceptionMethod = "Missing show method: public String "+showMethodSignature;
		if (typed.isGeneric())
			showExceptionMethod += " or missing generic show method: public String "+genericShowMethodSignature;
		showExceptionMethod  += " in "+potentialClasses;
		
		findIntMethod = LookupMethodTarget.findFixturingMethod(evaluator,findName,intArg);
		findStringMethod = LookupMethodTarget.findFixturingMethod(evaluator,findName,stringArg);
		showMethod = LookupMethodTarget.findFixturingMethod(evaluator,showMethodName,showArg);
		if (typed.isGeneric()) {
			final Class[] genericStringArg = { String.class, Type.class };
			final Class[] genericShowArg = { typed.asClass(), Type.class };
			genericFindStringMethod = LookupMethodTarget.findFixturingMethod(evaluator,findName,genericStringArg);
			genericShowMethod = LookupMethodTarget.findFixturingMethod(evaluator,showMethodName,genericShowArg);
		}
	}
	private Object callFindStringMethod(String text) throws Exception {
        if (genericFindStringMethod != null)
            return genericFindStringMethod.invoke(new Object[]{ text,typed.asType() });
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
		if (genericShowMethod != null) {
			Object[] args = new Object[]{ result, typed.asType() };
            return genericShowMethod.invoke(args).toString();
		}
		if (showMethod != null) {
			Object[] args = new Object[]{ result };
            return showMethod.invoke(args).toString();
		}
		throw new FitLibraryException(showExceptionMethod);
	}
	public boolean hasFinderMethod() {
		return findIntMethod != null || findStringMethod != null;
	}
}
