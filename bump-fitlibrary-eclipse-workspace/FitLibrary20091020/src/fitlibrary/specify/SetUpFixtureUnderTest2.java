/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.specify;

import fitlibrary.SetUpFixture;

public class SetUpFixtureUnderTest2 extends SetUpFixture {
    private boolean setup = false;
    @Override
	public void setUp() {
        setup = true; 
    }
	public void aPercent(@SuppressWarnings("unused") int a, @SuppressWarnings("unused") int b) {
		if (!setup)
		    throw new RuntimeException("not setup");
	}
    @Override
	public void tearDown() {
        throw new RuntimeException("teardown");
    }
    public SetUpFixtureUnderTest2 doNotDoAgain() {
        return this;
    }
    public SetUpFixtureUnderTest2 doAgain() {
        return new SetUpFixtureUnderTest2();
    }
}
