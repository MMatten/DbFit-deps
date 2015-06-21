/*
 * Copyright (c) 2010 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/

package rent;

import static fitlibrary.utility.ExtendedCamelCase.camel;

import java.io.File;
import java.io.IOException;

import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.traverse.Evaluator;
import fitlibrary.utility.FileHandler;

public class Generator {
	private static StringBuilder sb = new StringBuilder();
	private static String to = "C:/Documents and Settings/RimuResearch/My Documents/temp/generated";
	
	public static void start() {
		sb = new StringBuilder();
	}
	public static void writeTo(String pageName) {
		if (pageName.isEmpty())
			return;
		String fileName = to+"/"+pageName.replaceAll("\\.","/");
		new File(fileName).mkdirs();
		FileHandler f = new FileHandler(fileName+"/content.txt");
		try {
			f.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void act(String... ss) {
		act0(ss);
		println();
	}
	public static void act0(String... ss) {
		print("|");
		for (int i = 0; i < ss.length; i++)
			if ((i % 2) == 1)
				print(ss[i]+"|");
			else
				print("''"+ss[i]+"''|");
		println();
	}
	public static void actIs(String... ss) {
		actIs0(ss);
		println();
	}
	public static void actIs0(String... ss) {
		print("|");
		for (int i = 0; i < ss.length-1; i++)
			if ((i % 2) == 1)
				print(ss[i]+"|");
			else
				print("''"+ss[i]+"''|");
		print("'''is'''|"+ss[ss.length-1]+"|\n");
	}
	
	public static void click(String id) {
		act("click",id("a",id));
	}
	public static void clickRadio(String id) {
		act("click",id("input",id));
	}
	public static void withAddText(String id, String s) {
		act("with",id("input",id),"add text",s);
	}
	public static void titleIs(String s) {
		actIs("title",s);
	}
	public static void textOfIs0(String id, String s) {
		actIs0("text of",id("input",id),s);
	}
	public static void textOfIs(String id, String s) {
		textOfIs0(id,s);
		println();
	}
	public static void elementExists(String id) {
		act("element",id("td",id),"exists");
	}
	public static void ok(boolean ok, String s) {
        if (ok)
        	noErrorMessage();
        else
        	errorMessage(s);
	}
	private static String id(String tag, String id) {
		if (id.startsWith("//"))
			return id;
		return "//"+tag+"[@id='"+camel(id)+"']";
	}
	public static void checkTableSubset(Evaluator evaluator, String tableId) {
    	Table table = evaluator.getRuntimeContext().currentTable();
    	Row header = table.at(1);
    	for (int r = 2; r < table.size(); r++) {
    		Row row = table.at(r);
			act0("find element from",id("table",tableId),"with tag","tr","where");
			textOfIs0(col("td",header.at(0)),row.at(0).text());
			act0("select");
			for (int c = 1; c < table.at(r).size(); c++)
				textOfIs0(col("td",header.at(c)),row.at(c).text());
			println();
    	}
	}
	private static String col(String tag, Cell id) {
		return "//"+tag+"[@columnName='"+camel(id.text())+"']";
	}
	public static void checkTable(Evaluator evaluator, String tableId) {
    	Table table = evaluator.getRuntimeContext().currentTable();
    	act0("table values",id("table",tableId));
    	for (int r = 1; r < table.size(); r++) {
    		Row row = table.at(r);
			for (int c = 0; c < row.size(); c++) {
	    		print("|");
	    		print(row.at(c).text());
			}
			println("|");
    	}
    	println();
	}
	public static void errorMessage(String s) {
		textOfIs(id("div","error"),s);
	}
	public static void noErrorMessage() {
		act("element",id("div","error"),"does not exist");
	}
	public static void header(String s) {
		println(s);
		for (int i = 0; i < s.length(); i++)
			print("-");
		println();
	}
	public static void notCovered() {
		throw new RuntimeException("Not covered by generator");
	}
	private static void print(String s) {
		sb.append(s);
	}
	private static void println(String s) {
		print(s);
		println();
	}
	private static void println() {
		sb.append("\n");
	}
}
