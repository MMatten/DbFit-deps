package fitlibrary.suite;

import fitlibrary.table.Tables;
import fitlibrary.utility.TableListener;

public class PageRunner {
	protected boolean abandoned = false;
	protected boolean stopOnError = false;

	public void setStopOnError(boolean stopOnError) {
		this.stopOnError = stopOnError;
	}
	public void setAbandon(boolean abandoned) {
		this.abandoned = abandoned;
	}
	public boolean ignored(Tables tables, int index, TableListener tableListener) {
		if (abandoned || (stopOnError && tableListener.getTestResults().problems())) {
			tables.table(index).ignore(tableListener.getTestResults());
			new IgnoreRunner().ignore(tables,index+1,tableListener);
			return true;
		}
		return false;
	}
}
