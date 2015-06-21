/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 23/09/2006
*/

package fitlibraryGeneric.specify.collections;

import java.util.HashMap;
import java.util.Map;

import fitlibrary.specify.eg.Colour;

public class GenericMaps {
	private Map<Colour,Colour> aMap = new HashMap<Colour, Colour>();
	private Map<Colour,Map<Colour,Colour>> aMapOfMaps  = new HashMap<Colour,Map<Colour,Colour>>();

	public GenericMaps() {
		aMap.put(Colour.GREEN, Colour.BLUE);
		aMap.put(Colour.BLACK, Colour.YELLOW);		
		
		cyclicMaps(Colour.RED, Colour.GREEN);
		cyclicMaps(Colour.WHITE,Colour.BLACK);
		cyclicMaps(Colour.BLUE, Colour.YELLOW);
	}
	private void cyclicMaps(Colour colour1, Colour colour2) {
		Map<Colour,Colour> map1 = new HashMap<Colour,Colour>();
		map1.put(colour2,colour1);
		aMapOfMaps.put(colour1,map1);
	}
	public Map<Colour, Map<Colour, Colour>> getAMapOfMaps() {
		return aMapOfMaps;
	}
	public void setAMapOfMaps(Map<Colour, Map<Colour, Colour>> mapOfMaps) {
		aMapOfMaps = mapOfMaps;
	}
	public Map<Colour, Colour> getAMap() {
		return aMap;
	}
	public void setAMap(Map<Colour, Colour> map) {
		aMap = map;
	}
}
