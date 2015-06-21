package fat;

import java.text.ParseException;
import fit.*;
import java.util.regex.*;

public class FixtureNameFixture extends ColumnFixture {
	public String Table;
	
	public String FixtureName() throws Exception {
		Parse tableParse = GenerateTableParse(Table);
//		Fixture fixture = Fixture.
		
//		return loadFixture(GenerateTableParse(Table).text()).toString();
		return dumpTables(GenerateTableParse(Table));
	}


	//***************

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
			result += "[" + cell.text() + "]";
			separator = " ";
			cell = cell.more;
		}
		return result;
	}
	//***************
	
	
	public String ValidFixture() {
		return "not implemented";
	}
	
	public String Error() {
		return "not implemented";
	}
	
	private Parse GenerateTableParse(String table) throws ParseException {
		String[] rows = table.split("\n");
		return new Parse("table", null, GenerateRowParses(rows, 0), null);
	}

	private Parse GenerateRowParses(String[] rows, int rowIndex) {
		if (rowIndex >= rows.length) return null;
		
		Matcher matcher = Pattern.compile("\\[(.*?)\\]").matcher(rows[rowIndex]);
		matcher.matches();
		String[] cells = new String[matcher.groupCount()];
		for (int i = 0; i < matcher.groupCount(); i++) {
			cells[i] = matcher.group(i + 1);
		}
		
		return new Parse("tr", null, GenerateCellParses(cells, 0), GenerateRowParses(rows, rowIndex+1));
	}		

	private Parse GenerateCellParses(String[] cells, int cellIndex) {
		if (cellIndex >= cells.length) return null;
		
		return new Parse("td", cells[cellIndex], null, GenerateCellParses(cells, cellIndex + 1));
	}
}
