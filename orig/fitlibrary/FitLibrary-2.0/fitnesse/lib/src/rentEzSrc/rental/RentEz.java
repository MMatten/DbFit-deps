/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 27/08/2006
*/

package rental;

import java.util.ArrayList;

import fitlibrary.traverse.FitLibrarySelector;
import fitlibrary.traverse.workflow.DoTraverse;

public class RentEz extends DoTraverse {
	rps.RentEz rentEz = new rps.RentEz();

	public RentEz() {
//		setSetUpTraverse(new GeneralSetUp(rentEz));
		setSystemUnderTest(rentEz);
	}
	public Object setUpRentalItemTypes() {
		return FitLibrarySelector.selectCollectionSetUp(
				new ArrayList<Object>(rentEz.getRentalItemTypes().values()));
	}
}
