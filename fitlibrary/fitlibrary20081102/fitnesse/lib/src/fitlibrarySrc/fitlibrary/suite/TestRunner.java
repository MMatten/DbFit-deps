package fitlibrary.suite;

import java.io.PrintStream;

import fit.FitServerBridge;
import fitnesse.TestRunnerBridge;

public class TestRunner extends TestRunnerBridge {
	public static void main(String[] args) throws Exception {
		TestRunner runner = new TestRunner();
		runner.run(args);
		System.exit(runner.exitCode());
	}
	
	public TestRunner() throws Exception {
		super();
	}
	public TestRunner(PrintStream output) throws Exception {
		super(output);
	}
	protected FitServerBridge createFitServer(String host, int port, boolean debug) {
		return new FitLibraryServer(host, port, debug);
	}
}
