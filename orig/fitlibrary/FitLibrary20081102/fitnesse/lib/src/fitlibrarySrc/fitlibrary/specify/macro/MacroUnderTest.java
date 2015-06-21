package fitlibrary.specify.macro;

import fitlibrary.DoFixture;

public class MacroUnderTest extends DoFixture {
	public boolean withEnterText(String location, String s) {
		return !(location.indexOf("password") >= 0 && !s.equals(""));
	}
	public boolean submit(String location) {
		return true;
	}
	public boolean birds(Ab ab) {
		return ab.getA() == 1;
	}
	public static class Ab {
		private int a;

		public int getA() {
			return a;
		}
		public void setA(int a) {
			this.a = a;
		}
	}
}
