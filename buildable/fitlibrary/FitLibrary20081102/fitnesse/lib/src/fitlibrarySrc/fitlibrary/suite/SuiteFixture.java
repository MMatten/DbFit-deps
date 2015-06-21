/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.suite;

import java.util.HashSet;
import java.util.Set;

import fitlibrary.DoFixture;
import fitlibrary.dynamicVariable.DynamicVariables;

public class SuiteFixture extends DoFixture implements SuiteEvaluator {
    private Set keys = new HashSet();
	private DynamicVariables propertyValues = new DynamicVariables();
    
    public boolean selectOr(String[] keywords) {
        for (int i = 0; i < keywords.length; i++)
            keys.add(keywords[i]);
        return true;
    }
    public void keywords(String[] keywords) {
        if (keys.isEmpty())
            return;
        boolean selected = false;
        for (int i = 0; i < keywords.length; i++)
            selected = selected || keys.contains(keywords[i]);
        if (!selected)
        	abandon();
    }
    public DynamicVariables getDynamicVariables() {
		return propertyValues ;
	}
	public void setDynamicVariables(DynamicVariables propertyValues) {
		this.propertyValues = propertyValues;
	}
	public DynamicVariables getCopyOfPropertyValues() {
		return new DynamicVariables(propertyValues);
	}
}
