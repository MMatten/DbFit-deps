package fitlibrary.specify.dynamicVariable;

import java.io.File;

import fitlibrary.DoFixture;

public class RecordingDynamicVariables extends DoFixture {
	public void removeFile(String fileName) {
		new File(fileName).delete();
	}
}
