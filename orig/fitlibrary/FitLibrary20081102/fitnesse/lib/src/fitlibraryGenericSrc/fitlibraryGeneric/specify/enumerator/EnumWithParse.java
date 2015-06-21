package fitlibraryGeneric.specify.enumerator;

public class EnumWithParse {
	private ColourEnum enumeration;
	
	public static enum ColourEnum {
		RED, GREEN, BLUE, LIGHTRED;
		public static Object parse(String s) {
			if (s.startsWith("l"))
				return LIGHTRED;
			return RED;
		}
	}

	public ColourEnum getEnumeration() {
		return enumeration;
	}
	public void setEnumeration(ColourEnum enumeration) {
		this.enumeration = enumeration;
	}
}
