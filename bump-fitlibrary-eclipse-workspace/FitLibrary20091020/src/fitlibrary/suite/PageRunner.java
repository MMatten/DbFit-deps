/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.suite;

import fitlibrary.table.Tables;
import fitlibrary.utility.TableListener;
import fitlibrary.utility.TestResults;

public class PageRunner {
	private final TestResults runContext;

	public PageRunner(TestResults runContext) {
		this.runContext = runContext;
	}
	public boolean ignored(Tables tables, int index, TableListener tableListener) {
		if (runContext.isAbandoned() || (runContext.isStopOnError() && tableListener.getTestResults().problems())) {
			tables.table(index).ignore(tableListener.getTestResults());
			new IgnoreRunner(tableListener.getTestResults()).ignore(tables,index+1,tableListener);
			return true;
		}
		return false;
	}
}
