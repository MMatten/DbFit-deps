package fitlibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fitlibrary.exception.FitLibraryException;
import fitlibrary.traverse.workflow.DoEvaluator;
import fitlibrary.utility.ClassUtility;

public class SelectFixture extends DoFixture {
	private Map nametoFixture = new HashMap();
	
	public void addAs(String doFixtureClassName, String name) {
		Object instance;
		try {
			instance = ClassUtility.newInstance(doFixtureClassName);
			if (instance instanceof DoEvaluator) {
				DoEvaluator doEval = (DoEvaluator) instance;
				doEval.setUp();
				add(doEval,name);
			} else
				throw new FitLibraryException("Class must be a DoFixture or a DoTraverse");
		} catch (FitLibraryException e) {
			throw e;
		} catch (Exception e) {
			throw new FitLibraryException(e);
		}
	}
	public void select(String name) {
		Object fixture = nametoFixture.get(name);
		if (fixture == null)
			throw new FitLibraryException("Unknown name");
		setSystemUnderTest(fixture);
	}
	protected void add(DoEvaluator eval, String name) {
		nametoFixture.put(name,eval);
	}
	public void tearDown() {
		List errors = new ArrayList();
		for (Iterator it = nametoFixture.values().iterator(); it.hasNext();) {
			try {
				((DoEvaluator)it.next()).tearDown();
			} catch (Exception e) {
				errors.add(e);
			}
		}
		if (!errors.isEmpty())
			throw new FitLibraryException("tearDown errors: "+errors);
	}
	public List methodsThatAreVisible() {
		List list = new ArrayList(super.methodsThatAreVisible());
		list.add("addAs/2");
		list.add("select/1");
		list.add("tearDown/0");
		return list;
	}
}
