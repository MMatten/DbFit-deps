/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.traverse.workflow;

import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.closure.LookupMethodTargetStandard;
import fitlibrary.global.PlugBoard;
import fitlibrary.table.Row;

public class SequenceTraverse extends DoTraverse {
	public SequenceTraverse(Object sut) {
		super(sut);
	}
	@Override
	public CalledMethodTarget findMethodByActionName(Row row, int allArgs) throws Exception {
		return PlugBoard.lookupTarget.findTheMethodMapped(row.text(0,this), allArgs, this);
	}
}
