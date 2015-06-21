package fitlibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import fitlibrary.dynamicVariable.DynamicVariables;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Traverse;
import fitlibrary.utility.ExtendedCamelCase;
import fitlibrary.utility.ParameterSubstitution;
import fitlibrary.utility.TestResults;

public class DefineAction extends Traverse {
	private static Map definedActionMap = new ConcurrentHashMap(); // <String,Macro>
	private static Map wikiClassMap = new ConcurrentHashMap(); // <String,Map<String,MacroSubstitution>>
	private String wikiClassName = "";
	
    public DefineAction() {
		//
	}
    public DefineAction(String className) {
		this.wikiClassName = className;
	}
    public Object interpretAfterFirstRow(Table table, TestResults testResults) {
    	if (table.size() < 2 || table.size() > 3)
    		throw new FitLibraryException("Table for DefineAction needs to be two or three rows.");
    	boolean hasClass = false;
    	int bodyRow = 1;
    	if (table.size() == 3) {
    		hasClass = true;
    		bodyRow = 2;
    	}
    	if (table.row(1).size() != 1)
    		throw new FitLibraryException("Second row of table for DefineAction needs to contain one cell.");
    	if (hasClass && table.row(2).size() != 1)
    		throw new FitLibraryException("Third row of table for DefineAction needs to contain one cell.");
    	if (!table.row(bodyRow).cell(0).hasEmbeddedTable())
    		throw new FitLibraryException("Second row of table for DefineAction needs to contain nested tables.");
    	if (hasClass)
    		wikiClassName = table.row(1).text(0,this);
    	processDefinition(table.row(1).cell(0).innerTables(), testResults);
    	return null;
    }
	private void processDefinition(Tables tables, TestResults testResults) {
		Row parametersRow = tables.table(0).row(0);
		List formalParameters = new ArrayList();
    	String definedActionName = getDefinedActionNameAndParameters(parametersRow,formalParameters,testResults);
		DefinedAction definedAction = new DefinedAction(ExtendedCamelCase.camel(definedActionName),formalParameters.size());
		Tables body = tables.followingTables();
		if (body.parse == null) {
			Row row = new Row();
			row.addCell("comment");
			body = new Tables(new Table(row));
		}
		ParameterSubstitution parameterSubstitution = new ParameterSubstitution(formalParameters,body.deepCopy(),this);
		getClassMap().put(definedAction,parameterSubstitution);
	}
	private String getDefinedActionNameAndParameters(Row parametersRow, List formalParameters, TestResults testResults) {
		String definedActionName = "";
    	if (wikiClassBased())
    		formalParameters.add("this");
    	for (int i = 0; i < parametersRow.size(); i += 2) {
    		definedActionName += " "+parametersRow.text(i,this);
    		if (i+1 < parametersRow.size())
    			formalParameters.add(parametersRow.text(i+1,this));
    	}
    	parametersRow.passKeywords(testResults);
		return definedActionName;
	}
	private Map getClassMap() {
		Map currentMap = definedActionMap;
    	if (wikiClassBased()) {
    		currentMap = (Map) wikiClassMap.get(wikiClassName);
    		if (currentMap == null) {
    			currentMap = new ConcurrentHashMap();
    			wikiClassMap.put(wikiClassName,currentMap);
    		}
    	}
		return currentMap;
	}
	private boolean wikiClassBased() {
		return !"".equals(wikiClassName);
	}
    public static ParameterSubstitution lookup(String name, List actualArgs) {
    	return (ParameterSubstitution) definedActionMap.get(new DefinedAction(name,actualArgs.size()));
    }
    public static ParameterSubstitution lookupByClass(String className, String name, List actualArgs, DynamicVariables variables) {
    	DefinedAction macro = new DefinedAction(name,actualArgs.size());
    	Map map = (Map) wikiClassMap.get(className);
    	if (map != null) {
    		ParameterSubstitution macroSubstitution = (ParameterSubstitution) map.get(macro);
    		if (macroSubstitution != null)
    			return macroSubstitution;
    	}
    	String superClass = variables.get(className+".super");
    	if (superClass != null && !"".equals(superClass))
    		return lookupByClass(superClass, name, actualArgs, variables);
    	return (ParameterSubstitution) definedActionMap.get(macro);
    }
    private static class DefinedAction {
    	private String name;
		private int formalArgs;

		public DefinedAction(String name, int formalArgs) {
    		this.name = name;
    		this.formalArgs = formalArgs;
    	}
		public boolean equals(Object obj) {
			if (!(obj instanceof DefinedAction))
				return false;
			DefinedAction other = (DefinedAction) obj;
			return name.equals(other.name) && formalArgs == other.formalArgs;
		}
		public int hashCode() {
			return name.hashCode() + formalArgs;
		}
		public String toString() {
			return "DefinedAction["+name+"/"+formalArgs+"]";
		}
    }
}
