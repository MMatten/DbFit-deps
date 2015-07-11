/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitnesse;

//Copyright (C) 2003,2004,2005 by Object Mentor, Inc. All rights reserved.
//Released under the terms of the GNU General Public License version 2 or later.
// Altered to allow for a subclass to use FitServerBridge instead of FitServer. Rick Mugridge, www.rimuresearch.com
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import util.CommandLine;
import fit.Counts;
import fit.FitServer;
import fit.FitServerBridge;
import fit.FixtureListener;
import fit.Parse;
import fitnesse.responders.run.TestSummary;
import fitnesse.runner.CachingResultFormatter;
import fitnesse.runner.FormattingOption;
import fitnesse.runner.PageResult;
import fitnesse.runner.StandardResultHandler;

@SuppressWarnings("unchecked")
public abstract class TestRunnerBridge {
	private String host;
	private int port;
	private String pageName;
	private FitServerBridge fitServer;
	public TestRunnerFixtureListener2 fixtureListener;
	public CachingResultFormatter handler;
	private PrintStream output;
	public List formatters = new LinkedList();
	private boolean debug;
	public boolean verbose;
	public boolean usingDownloadedPaths = true;
	private String suiteFilter;

	public TestRunnerBridge() throws Exception
	{
		this(System.out);
	}

	public TestRunnerBridge(PrintStream output) throws Exception
	{
		this.output = output;
		handler = new CachingResultFormatter();
	}

	public void args(String[] args) throws Exception
	{
		CommandLine commandLine = new CommandLine("[-debug] [-v] [-results file] [-html file] [-xml file] [-nopath] [-suiteFilter filter] host port pageName");
		if(!commandLine.parse(args))
			usage();

		host = commandLine.getArgument("host");
		port = Integer.parseInt(commandLine.getArgument("port"));
		pageName = commandLine.getArgument("pageName");

		if(commandLine.hasOption("debug"))
			debug = true;
		if(commandLine.hasOption("v"))
		{
			verbose = true;
			handler.addHandler(new StandardResultHandler(output));
		}
		if(commandLine.hasOption("nopath"))
			usingDownloadedPaths = false;
		if(commandLine.hasOption("results"))
			formatters.add(new FormattingOption("raw", commandLine.getOptionArgument("results", "file"), output, host, port, pageName));
		if(commandLine.hasOption("html"))
			formatters.add(new FormattingOption("html", commandLine.getOptionArgument("html", "file"), output, host, port, pageName));
		if(commandLine.hasOption("xml"))
			formatters.add(new FormattingOption("xml", commandLine.getOptionArgument("xml", "file"), output, host, port, pageName));
		if(commandLine.hasOption("suiteFilter"))
			suiteFilter = commandLine.getOptionArgument("suiteFilter", "filter");
	}

	protected void usage()
	{
		System.out.println("usage: java fitlibrary.suite.TestRunner [options] host port page-name");
		System.out.println("\t-v \tverbose: prints test progress to stdout");
		System.out.println("\t-results <filename|'stdout'>\tsave raw test results to a file or dump to standard output");
		System.out.println("\t-html <filename|'stdout'>\tformat results as HTML and save to a file or dump to standard output");
		System.out.println("\t-debug \tprints FitServer protocol actions to stdout");
		System.out.println("\t-nopath \tprevents downloaded path elements from being added to classpath");
		System.exit(-1);
	}

	public void run(String[] args) throws Exception
	{
		args(args);
		fitServer = createFitServer(host, port, debug);
		fixtureListener = new TestRunnerFixtureListener2(this);
		fitServer.fixtureListener = fixtureListener;
		fitServer.establishConnection(makeHttpRequest());
		fitServer.validateConnection();
		if(usingDownloadedPaths)
			processClasspathDocument();
		fitServer.process();
		finalCount();
		fitServer.closeConnection();
		fitServer.exit();
		doFormatting();
		handler.cleanUp();
	}

	protected abstract FitServerBridge createFitServer(String host1, int port1, boolean debug1);

	private void processClasspathDocument() throws Exception
	{
		String classpathItems = fitServer.readDocument();
		if(verbose)
			output.println("Adding to classpath: " + classpathItems);
		addItemsToClasspath(classpathItems);
	}

	private void finalCount() throws Exception
	{
		Counts counts = fitServer.getCounts();
		handler.acceptFinalCount(new TestSummary(counts.right, counts.wrong, counts.ignores, counts.exceptions));
	}

	public int exitCode()
	{
		return fitServer == null ? -1 : fitServer.exitCode();
	}

	public String makeHttpRequest()
	{
		String request = "GET /" + pageName + "?responder=fitClient";
		if(usingDownloadedPaths)
			request += "&includePaths=yes";
		if(suiteFilter != null)
			request += "&suiteFilter=" + suiteFilter;
		return request + " HTTP/1.1\r\n\r\n";
	}

	public Counts getCounts()
	{
		return fitServer.getCounts();
	}

	public void acceptResults(PageResult results) throws Exception
	{
		TestSummary summary = results.testSummary();
		fitServer.writeCounts(new Counts(summary.right,summary.wrong,summary.ignores,summary.exceptions));
		handler.acceptResult(results);
	}

	public void doFormatting() throws Exception
	{
		for(Iterator iterator = formatters.iterator(); iterator.hasNext();)
		{
			FormattingOption option = (FormattingOption) iterator.next();
			if(verbose)
				output.println("Formatting as " + option.format + " to " + option.filename);
			option.process(handler.getResultStream(), handler.getByteCount());
		}
	}

	@SuppressWarnings("deprecation")
	public static void addItemsToClasspath(String classpathItems) throws Exception
	{
		String[] items = classpathItems.split(System.getProperty("path.separator"));
		for(int i = 0; i < items.length; i++)
		{
			String item = items[i];
			addUrlToClasspath(new File(item).toURL());
		}
	}

	public static void addUrlToClasspath(URL u) throws Exception
	{
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
		Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
		method.setAccessible(true);
		method.invoke(sysloader, new Object[]{u});
	}

	public static class TestRunnerFixtureListener2 implements FixtureListener
	{
		public Counts counts = new Counts();
		private boolean atStartOfResult = true;
		private PageResult currentPageResult;
		private TestRunnerBridge runner;

		public TestRunnerFixtureListener2(TestRunnerBridge runner)
		{
			this.runner = runner;
		}

		public void tableFinished(Parse table)
		{
			try
			{
				String data = new String(FitServer.readTable(table), "UTF-8");
				if(atStartOfResult)
				{
					int indexOfFirstLineBreak = data.indexOf("\n");
					String pageTitle = data.substring(0, indexOfFirstLineBreak);
					data = data.substring(indexOfFirstLineBreak + 1);
					currentPageResult = new PageResult(pageTitle);
					atStartOfResult = false;
				}
				currentPageResult.append(data);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		public void tablesFinished(Counts count)
		{
			try
			{
				currentPageResult.setTestSummary(new TestSummary(count.right,count.wrong,count.ignores,count.exceptions));
				runner.acceptResults(currentPageResult);
				atStartOfResult = true;
				counts.tally(count);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
