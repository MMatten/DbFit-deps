package fitlibrary.specify.dynamicVariable;

import fitlibrary.DoFixture;

public class DynamicVariablesUnderTest extends DoFixture {
	private int count = 0;

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String get(String s) { 
		return s;
	}
}
