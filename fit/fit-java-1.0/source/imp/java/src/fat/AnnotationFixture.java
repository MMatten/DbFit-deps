package fat;

import fit.*;
import java.io.*;

public class AnnotationFixture extends ColumnFixture {
	public String Type;
	public String Text;
	public String OriginalCell = "Text";
	
	public String Output() {
		Parse parse = new Parse("td", OriginalCell, null, null);
		Fixture hack = new Fixture();
		
		if (Type.equals("none")) {
			// do nothing
		}
		else if (Type.equals("right")) {
			hack.right(parse);
		}
		else if (Type.equals("wrong")) {
			hack.wrong(parse, Text);
		}
		else if (Type.equals("error")) {
			return "not implemented";
		}
		else if (Type.equals("ignore")) {
			hack.ignore(parse);
		}
		else if (Type.equals("unchecked")) {
			return "not implemented";
		}
		else {
			return "unknown type: " + Type;
		}
		
		return GenerateOutput(parse); 
	}
	
	// code smell note: copied from ParseFixture	
	private String GenerateOutput(Parse parse) {
		StringWriter result = new StringWriter();
		parse.print(new PrintWriter(result));
		return result.toString().trim();
	}
}
