/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
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
	@Override
	protected FitServerBridge createFitServer(String host, int port, boolean debug) {
		return new FitLibraryServer(host, port, debug);
	}
}
