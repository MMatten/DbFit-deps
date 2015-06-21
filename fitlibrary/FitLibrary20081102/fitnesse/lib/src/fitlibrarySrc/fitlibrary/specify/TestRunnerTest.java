package fitlibrary.specify;

import fitlibrary.suite.TestRunner;

public class TestRunnerTest {
	public String run(String pageName) throws Exception {
		String[] args =  {"-v",
				"localhost","8980",pageName};
		TestRunner runner = new TestRunner();
		runner.run(args);
		return ""+runner.exitCode();
	}
}
