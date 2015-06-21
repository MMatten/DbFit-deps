/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
 * Written: 3/11/2006
*/

package fitlibraryGeneric.typed;

import java.lang.reflect.Method;

import fitlibrary.typed.NonGenericTyped;
import fitlibrary.typed.Typed;
import fitlibrary.typed.TypedFactory;
import fitlibrary.typed.TypedObject;

public class GenericTypedFactory implements TypedFactory {
	public TypedObject asTypedObject(Object sut) {
		return new GenericTypedObject(sut);
	}
	public Typed asTyped(Class classType) {
		return new NonGenericTyped(classType);
	}
	public Typed asTyped(Method method) {
		return new GenericTyped(method.getGenericReturnType(),true);
	}
}
