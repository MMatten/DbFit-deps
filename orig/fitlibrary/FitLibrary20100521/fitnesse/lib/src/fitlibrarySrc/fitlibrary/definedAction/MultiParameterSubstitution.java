/*
 * Copyright (c) 2009 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.definedAction;

import java.util.HashSet;
import java.util.List;

import fitlibrary.dynamicVariable.DynamicVariables;
import fitlibrary.dynamicVariable.LocalDynamicVariables;
import fitlibrary.exception.FitLibraryException;
import fitlibrary.table.Row;
import fitlibrary.table.Tables;
import fitlibrary.traverse.Evaluator;

public class MultiParameterSubstitution {
	private List<String> formalParameters;
	private Tables tables;
	private String absoluteFileName;

	public MultiParameterSubstitution(List<String> formalParameters, Tables tables, String absoluteFileName) {
		this.formalParameters = formalParameters;
		this.tables = tables;
		this.absoluteFileName = absoluteFileName;
	}
	public void createMappingsForCall(List<String> actuals, LocalDynamicVariables vars) {
		if (actuals.size() != formalParameters.size())
			throw new RuntimeException("Formals and actuals don't match");
		for (int i = 0; i < actuals.size(); i++)
			vars.putParameter(formalParameters.get(i), actuals.get(i));
	}
	public Tables getCopyOfBody() {
		return tables.deepCopy();
	}
	public String getAbsoluteFileName() {
		return absoluteFileName;
	}
	public void verifyParameters(Row row, Evaluator evaluator) {
		if (row.size() != formalParameters.size())
			throw new FitLibraryException("Expected "+formalParameters.size()+" parameters but there were "+row.size());
		HashSet<String> set = new HashSet<String>();
		for (int c = 0; c < row.size(); c++) {
			String actualName = row.text(c, evaluator);
			if (!formalParameters.contains(actualName))
				throw new FitLibraryException("Unknown parameter: '"+actualName+"'");
			if (set.contains(actualName))
				throw new FitLibraryException("Duplicate parameter: '"+actualName+"'");
			set.add(actualName);
		}
	}
	public void bind(Row parameterRow, Row row, DynamicVariables dynamicVariables,
			Evaluator evaluator) {
		if (row.size() != formalParameters.size())
			throw new FitLibraryException("Expected "+formalParameters.size()+" parameters but there were "+row.size());
		for (int c = 0; c < row.size(); c++) {
			String parameter = parameterRow.text(c, evaluator);
			String actual = row.text(c, evaluator);
			dynamicVariables.putParameter(parameter, actual);
		}
	}
}
