package fitlibraryGeneric.specify.genericFinder;

import fitlibrary.traverse.DomainAdapter;

public class NonGenericFinderForGeneric implements DomainAdapter {
	private Pair<Integer,Integer> integerIntegerPair;
	
	public Pair<Integer,Integer> getIntegerIntegerPair() {
		return integerIntegerPair;
	}
	public void setIntegerIntegerPair(Pair<Integer,Integer> pair) {
		this.integerIntegerPair = pair;
	}
	public Pair findPair(String key) { 
		return new Pair<Integer,Integer>(1,2);
	}
	public Object getSystemUnderTest() {
		return null;
	}
}
