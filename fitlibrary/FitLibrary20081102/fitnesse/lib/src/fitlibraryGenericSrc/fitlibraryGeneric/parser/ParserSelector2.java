/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibraryGeneric.parser;

import java.lang.reflect.Type;

import fitlibrary.parser.Parser;
import fitlibrary.parser.lookup.ParserFactory;
import fitlibrary.parser.lookup.ParserSelector;
import fitlibrary.table.Cell;
import fitlibrary.traverse.Evaluator;
import fitlibrary.typed.Typed;
import fitlibrary.utility.TestResults;
import fitlibraryGeneric.list.ListParser2;
import fitlibraryGeneric.map.MapParser2;
import fitlibraryGeneric.set.SetParser2;
import fitlibraryGeneric.typed.GenericTyped;

public class ParserSelector2 extends ParserSelector {
    @Override
    protected ParserFactory listParserFactory() {
    	return ListParser2.parserFactory();
    }
    @Override
	protected ParserFactory mapParserFactory() {
    	return MapParser2.parserFactory();
    }
    @Override
    protected ParserFactory setParserFactory() {
    	return SetParser2.parserFactory();
    }
    @Override
    protected boolean isExtraFactoryFor(Typed typed) {
    	GenericTyped generic = (GenericTyped)typed;
    	return generic.isEnum();
    }
    @Override
    protected ParserFactory extraFactory() {
    	return EnumParser.parserFactory();
    }
	public static Object evaluate(Evaluator evaluator, Type type, String text) throws Exception {
		Parser parserFor = new ParserSelector2().parserFor(evaluator,new GenericTyped(type),false);
		TestResults testResults = new TestResults();
		Cell cell = new Cell(text);
		Object subject = parserFor.parseTyped(cell,testResults).getSubject();
		if (testResults.problems())
			throw new RuntimeException("Unable to parse '"+text+"' as a "+type+": "+cell.fullText());
		return subject;
	}
}
