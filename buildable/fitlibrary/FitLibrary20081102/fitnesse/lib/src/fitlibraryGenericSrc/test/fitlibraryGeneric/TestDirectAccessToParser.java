package test.fitlibraryGeneric;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import fitlibrary.DoFixture;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.exception.parse.BadNumberException;
import fitlibraryGeneric.generic.LocalParameterizedType;
import fitlibraryGeneric.parser.ParserSelector2;

public class TestDirectAccessToParser extends TestCase {
	public void testParseInt() throws Exception {
		assertEquals(3,ParserSelector2.evaluate(new MyFixture(), int.class, "3"));
	}
	public void testParseIntFails() throws Exception {
		try {
			ParserSelector2.evaluate(new MyFixture(), int.class, "three");
			fail();
		} catch (BadNumberException e) {
		}
	}
	public void testParseWithFinder() throws Exception {
		assertEquals(new X("3"),ParserSelector2.evaluate(new MyFixture(), X.class, "3"));
	}
	public void testParseWithOutFinder() throws Exception {
		try {
			ParserSelector2.evaluate(new MyFixture(), Y.class, "3");
			fail();
		} catch (FitLibraryException e) {
		}
	}
	public void testParseWithEnumFinder() throws Exception {
		assertEquals(En.A,ParserSelector2.evaluate(new MyFixture(),En.class, "a"));
	}
	public void testParseWithGenericFinder() throws Exception {
		LocalParameterizedType type = new LocalParameterizedType(TestDirectAccessToParser.class, Gen.class, Integer.class);
		assertEquals(new Gen<Integer>(3),ParserSelector2.evaluate(new MyFixture(),type, "3"));
	}
	public void testParseWithGenericEnumFinder() throws Exception {
		LocalParameterizedType type = new LocalParameterizedType(TestDirectAccessToParser.class, Gen.class, En.class);
		assertEquals(new Gen<En>(En.A),ParserSelector2.evaluate(new MyFixture(),type, "A"));
	}
	public void testParseWithGenericListEnumFinder() throws Exception {
		LocalParameterizedType innerType = new LocalParameterizedType(TestDirectAccessToParser.class, List.class, En.class);
		LocalParameterizedType type = new LocalParameterizedType(TestDirectAccessToParser.class, Gen.class, innerType);
		List<En> expectedList = new ArrayList<En>();
		expectedList.add(En.A);
		expectedList.add(En.B);
		assertEquals(new Gen<List<En>>(expectedList),ParserSelector2.evaluate(new MyFixture(),type, "a, b"));
	}
	
	public static class MyFixture extends DoFixture {
		public X findX(String s) {
			return new X(s);
		}
		@SuppressWarnings("unchecked")
		public Gen findGen(String key, Type type) throws Exception {
			Type innerType = ((ParameterizedType)type).getActualTypeArguments()[0];
			if (innerType == Integer.class)
				return new Gen<Integer>(Integer.valueOf(key));
			if (innerType == En.class)
				return new Gen<En>((En) ParserSelector2.evaluate(this,En.class,key));
			if (((ParameterizedType)innerType).getRawType() == List.class && 
					((ParameterizedType)innerType).getActualTypeArguments()[0] == En.class)
				return new Gen<List<En>>((List<En>) ParserSelector2.evaluate(this,innerType,key));
			throw new RuntimeException();
		}
	}
	public static class X {
		private String s;

		public X(String s) {
			this.s = s;
		}
		@Override
		public boolean equals(Object obj) {
			return s.equals(((X)obj).s);
		}
	}
	public static class Y {
		
	}
	public static class Gen<T> {
		private T t;

		public Gen(T t) {
			this.t = t;
		}
		@Override
		public boolean equals(Object obj) {
			return t.equals(((Gen)obj).t);
		}
	}
	public static enum En {
		A, B
	}
}
