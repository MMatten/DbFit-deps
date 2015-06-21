/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 27/08/2006
*/

package rent;

import rps.RentalItem;
import rps.time.Duration;

public class Rental {
	private RentalItem item;
	private int count;
	private Duration duration;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Duration getDuration() {
		return duration;
	}
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	public RentalItem getItem() {
		return item;
	}
	public void setItem(RentalItem item) {
		this.item = item;
	}
}
