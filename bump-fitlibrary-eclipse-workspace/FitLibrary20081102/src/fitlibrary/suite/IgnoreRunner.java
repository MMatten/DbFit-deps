package fitlibrary.suite;

import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.utility.TableListener;

public class IgnoreRunner extends PageRunner {
	public void ignore(Tables tables, int index, TableListener tableListener) {
		for (int t = index; t < tables.size(); t++) {
			Table table = tables.table(t);
			table.ignore(tableListener.getTestResults());
			tableListener.tableFinished(table);
		}
	}
}
