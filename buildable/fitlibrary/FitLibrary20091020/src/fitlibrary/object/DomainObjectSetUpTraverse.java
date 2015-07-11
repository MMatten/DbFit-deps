/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.object;

import fitlibrary.closure.CalledMethodTarget;
import fitlibrary.closure.LookupMethodTargetStandard;
import fitlibrary.exception.IgnoredException;
import fitlibrary.exception.classes.ConstructorNotVisible;
import fitlibrary.exception.classes.NoNullaryConstructor;
import fitlibrary.exception.classes.NotSubclassFromClassFactoryMethod;
import fitlibrary.exception.classes.NullFromClassFactoryMethod;
import fitlibrary.global.PlugBoard;
import fitlibrary.table.Cell;
import fitlibrary.table.Row;
import fitlibrary.table.Table;
import fitlibrary.traverse.Evaluator;
import fitlibrary.traverse.Traverse;
import fitlibrary.typed.Typed;
import fitlibrary.typed.TypedObject;
import fitlibrary.utility.ClassUtility;
import fitlibrary.utility.TestResults;

public class DomainObjectSetUpTraverse extends Traverse {
	private Class<?> type;
	
	public DomainObjectSetUpTraverse(Object sut) {
		super(sut);
	}
	public DomainObjectSetUpTraverse(TypedObject typedObject, Typed typed) {
		super(typedObject);
		type = typed.asClass();
	}
	@Override
	public Object interpretAfterFirstRow(Table table, TestResults testResults) {
		if (type != null)
			createObjectOfSpecifiedType(table,testResults);
		for (int rowNo = 1; rowNo < table.size(); rowNo++)
			processRow(table.row(rowNo),testResults);
		try {
			callEndCreatingObjectMethod(getTypedSystemUnderTest());
		} catch (Exception e) {
			table.error(testResults, e);
		}
		return getSystemUnderTest();
	}
	private void createObjectOfSpecifiedType(Table table, TestResults testResults) {
		for (int rowNo = 1; rowNo < table.size(); rowNo++) {
			Row row = table.row(rowNo);
			for (int i = 0; i < row.size(); i += 2) {
				if (givesClass(row.cell(i),this)) {
					createSystemUnderTest(row.cell(i+1), testResults);
					return;
				}
			}
		}
		if (getSystemUnderTest() == null)
			throw new NoNullaryConstructor(type);
	}
	@SuppressWarnings("null")
	private void createSystemUnderTest(Cell cell, TestResults testResults) {
		Class<?> sutClass = null;
		try {
			String typeName = cell.text(this);
			sutClass = PlugBoard.lookupTarget.findClassFromFactoryMethod(this, type, typeName);
			if (sutClass == null)
				throw new NullFromClassFactoryMethod(typeName);
			if (!type.isAssignableFrom(sutClass))
				throw new NotSubclassFromClassFactoryMethod(sutClass,type);
			Object newInstance = ClassUtility.createElement(sutClass,this);
			setSystemUnderTest(newInstance);
			callStartCreatingObjectMethod(newInstance);
		} catch (IllegalAccessException e) {
			cell.error(testResults, new ConstructorNotVisible(sutClass.getName()));
		} catch (NoSuchMethodException e) {
			cell.error(testResults, new NoNullaryConstructor(sutClass.getName()));
		} catch (Exception e) {
			cell.error(testResults, e);
		}
	}
	public void processRow(Row row, TestResults testResults) {
		for (int i = 0; i < row.size(); i += 2) {
			Cell cell = row.cell(i);
			if (!DomainObjectSetUpTraverse.givesClass(cell,this)) {
				if (getSystemUnderTest() == null) {
					cell.ignore(testResults);
				} else {
					try {
						CalledMethodTarget target = PlugBoard.lookupTarget.findSetter(cell.text(this), this);
						callSetter(target, row.cell(i+1), testResults);
					} catch (Exception e) {
						cell.error(testResults,e);
					}
				}
			}
		}
	}
    public static boolean givesClass(Cell cell, Evaluator evaluator) {
        return cell.isBlank(evaluator) && !cell.hasEmbeddedTable();
    }
	private static void callSetter(CalledMethodTarget target, Cell nextCell, TestResults testResults) {
		try {
			target.invoke(nextCell,testResults);
		} catch (IgnoredException e) {
			//
		} catch (Exception e) {
			nextCell.error(testResults,e);
		}
	}
}
