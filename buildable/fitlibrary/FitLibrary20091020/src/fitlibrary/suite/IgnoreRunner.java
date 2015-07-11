/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.suite;

import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.utility.TableListener;
import fitlibrary.utility.TestResults;

public class IgnoreRunner extends PageRunner {
	public IgnoreRunner(TestResults runContext) {
		super(runContext);
	}
	public void ignore(Tables tables, int index, TableListener tableListener) {
		for (int t = index; t < tables.size(); t++) {
			Table table = tables.table(t);
			table.ignore(tableListener.getTestResults());
			tableListener.tableFinished(table);
		}
	}
}
