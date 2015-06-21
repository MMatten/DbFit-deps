package rps;

public class ItemRestriction {
	private String restrictID;
	
	public ItemRestriction(int restrictID, String constraint) {
		this(String.valueOf(restrictID), constraint);
	}
	@SuppressWarnings("unused")
	public ItemRestriction(String restrictID, String constraint) {
		this.restrictID = restrictID;
	}
	public String getID() {
		return restrictID;
	}
}
