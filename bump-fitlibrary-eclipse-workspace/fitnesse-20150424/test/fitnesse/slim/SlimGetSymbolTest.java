// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.slim;

import org.junit.Before;

public class SlimGetSymbolTest extends SlimGetSymbolTestBase {

  @Before
  @Override
  public void setUp() throws Exception {
    caller = new StatementExecutor();
  }

}
