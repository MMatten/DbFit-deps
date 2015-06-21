package fat;

import fit.*;
import java.io.*;

public class OutputFixture extends ColumnFixture {
	public String Text;

	public String CellOutput() {
		Parse cell = new Parse("td", "", null, null);
		cell.leader = "";
		cell.body = Fixture.escape(unescape(Text));
		return GenerateOutput(cell);
	}

	private String unescape(String text) {
		text = text.replaceAll("\\\\n", "\n");
		text = text.replaceAll("\\\\r", "\r");
		return text;
	}
	
	private String GenerateOutput(Parse parse) {
		StringWriter result = new StringWriter();
		parse.print(new PrintWriter(result));
		return result.toString();
	}
}
