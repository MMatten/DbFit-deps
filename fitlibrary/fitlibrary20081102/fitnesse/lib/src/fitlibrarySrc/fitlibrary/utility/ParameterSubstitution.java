package fitlibrary.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fit.exception.FitFailureException;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Evaluator;

public class ParameterSubstitution {
	private Tables tables;

	public ParameterSubstitution(List formalParameters, Tables tables, Evaluator evaluator) {
		this.tables = tables;
		Map mapToRef = new HashMap();
		for (int c = 0; c < formalParameters.size(); c++) {
			Object formal = formalParameters.get(c);
			if (mapToRef.get(formal) != null)
				throw new FitFailureException("Duplicated parameter: "+formal);
			mapToRef.put(formal,paramRef(c));
		}
		macroReplace(tables,mapToRef,evaluator);
	}
	public Tables substitute(List actualParameters, Evaluator evaluator) {
		Tables copy = tables.deepCopy();
		Map mapFromRef = new HashMap();
		for (int i = 0; i < actualParameters.size(); i++)
			mapFromRef.put(paramRef(i), actualParameters.get(i));
		macroReplace(copy, mapFromRef,evaluator);
		return copy;
	}
	private static void macroReplace(Tables tables, Map mapToRef, Evaluator evaluator) {
		List reverseSortOrder = new ArrayList(mapToRef.keySet());
		Collections.sort(reverseSortOrder,new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return ((String)arg1).compareTo(arg0);
			}
		});
		for (Iterator it = reverseSortOrder.iterator(); it.hasNext(); ) {
			String key = (String)it.next();
			macroReplaceTables(tables, key, mapToRef.get(key),evaluator);
		}
	}
	private static void macroReplaceTables(Tables tables, String key, Object value, Evaluator evaluator) {
		for (int t = 0; t < tables.size(); t++) {
			Table table = tables.table(t);
			for (int r = 0 ; r < table.size(); r++) {
				Row row = table.row(r);
				for (int c = 0; c < row.size(); c++)
					macroReplaceCell(row.cell(c), key, value,evaluator);
			}
		}
	}
	private static void macroReplaceCell(Cell cell, String key, Object value, Evaluator evaluator) {
		if (cell.hasEmbeddedTable())
			macroReplaceTables(cell.getEmbeddedTables(),key,value,evaluator);
		String text = cell.text(evaluator);
		if (value instanceof String) {
			text = text.replaceAll(key,(String)value);
			if (!text.equals(cell.text(evaluator)))
				cell.setText(text);
		} else { // Embedded tables: Just replace once
			Tables valueTables = (Tables) value;
			int at = text.indexOf(key);
			if (at < 0)
				return;
			Tables addedTables = valueTables.deepCopy();
			if (cell.hasEmbeddedTable())
				cell.getEmbeddedTables().parse.last().more = addedTables.parse;
			else
				cell.setInnerTables(addedTables);
			cell.getEmbeddedTables().parse.leader = text.substring(0,at);
			cell.getEmbeddedTables().parse.last().trailer = text.substring(at+key.length());
		}
	}
	private static String paramRef(int c) {
		return "%__%"+c+"%__%";
	}
	public String toString() {
		return "MacroSubstitution["+tables.toString()+"]";
	}
}
