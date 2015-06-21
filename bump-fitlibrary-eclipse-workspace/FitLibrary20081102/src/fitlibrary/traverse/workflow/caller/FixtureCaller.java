package fitlibrary.traverse.workflow.caller;

import fit.Fixture;
import fitlibrary.table.Row;
import fitlibrary.traverse.workflow.DoCaller;
import fitlibrary.utility.TestResults;

public class FixtureCaller extends DoCaller {
	private Fixture fixtureByName;

	public FixtureCaller(Fixture fixtureByName) {
		this.fixtureByName = fixtureByName;
	}
	public boolean isValid() {
		return fixtureByName != null;
	}
	public Object run(Row row, TestResults testResults) throws Exception {
		return fixtureByName;
	}
	public String ambiguityErrorMessage() {
		return "fixture "+fixtureByName.getClass().getName();
	}
}
