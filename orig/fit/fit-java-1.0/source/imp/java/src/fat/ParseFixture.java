package fat;

import fit.*;
import java.text.*;
import java.io.*;

/** A fixture for discussing parsing. */
public class ParseFixture extends ColumnFixture {
	public String Html;
	public String TableCell;
	public String Entity;
	public String Note;  // unused
	
	private Parse GenerateParse() throws ParseException {
		int inputColumns = 0;
		String html = null;
		if (Html != null) {
			inputColumns++;
			html = Html;
		}
		if (TableCell != null) {
			inputColumns++;
			html = "<table><tr>" + TableCell + "</tr></table>";
		}
		if (Entity != null) {
			inputColumns++;
			html = "<table><tr><td>" + Entity + "</td></tr></table>";
		}
		
		if (inputColumns != 1) {
			throw new RuntimeException("Exactly ONE of the following columns is needed: 'Html', 'TableCell', or 'Entity'");
		}

		html = html.replaceAll("\\\\u00a0", "\u00a0");
		return new Parse(html);
	}
	
	public String Output() throws ParseException {
		return GenerateOutput(GenerateParse());
	}
	
	private String GenerateOutput(Parse parse) {
		StringWriter result = new StringWriter();
		parse.print(new PrintWriter(result));
		return result.toString();
	}
	
	public String Parse() throws ParseException {
		return dumpTables(GenerateParse());		
	}
		
	private String dumpTables(Parse table) {
		String result = "";
		String separator = "";
		while (table != null) {
			result += separator;
			result += dumpRows(table.parts);
			separator = "\n----\n";
			table = table.more;
		}
		return result;
	}
	
	private String dumpRows(Parse row) {
		String result = "";
		String separator = "";
		while (row != null) {
			result += separator;
			result += dumpCells(row.parts);
			separator = "\n";
			row = row.more;
		}
		return result;
	}
	
	private String dumpCells(Parse cell) {
		String result = "";
		String separator = "";
		while (cell != null) {
			result += separator;
			result += "[" + escapeAscii(cell.text()) + "]";
			separator = " ";
			cell = cell.more;
		}
		return result;
	}

	private String escapeAscii(String text) {
		text = text.replaceAll("\\x0a", "\\\\n");
		text = text.replaceAll("\\x0d", "\\\\r");
		text = text.replaceAll("\\xa0", "\\\\u00a0");
		return text;
	}
}
