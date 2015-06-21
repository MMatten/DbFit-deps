/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package fitlibrary.tableProxy;

public interface CellProxy {
	void pass();
	void fail(String msg);
	void error(String msg);
}
