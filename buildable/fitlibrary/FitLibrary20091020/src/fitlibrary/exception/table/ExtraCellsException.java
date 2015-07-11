/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.exception.table;

import fitlibrary.exception.FitLibraryExceptionWithHelp;

public class ExtraCellsException extends FitLibraryExceptionWithHelp {
    public ExtraCellsException(String link) {
        super("Extra table cells","ExtraCells."+link);
    }
}
