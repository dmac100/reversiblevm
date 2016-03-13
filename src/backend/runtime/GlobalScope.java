package backend.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import backend.observer.ValueObserverList;
import backend.observer.ValueReadObserver;
import backend.runtime.library.ArrayProto;
import backend.runtime.library.Global;
import backend.runtime.library.ObjectProto;
import backend.runtime.library.StringProto;
import backend.value.NativeFunctionValue;
import backend.value.NullValue;
import backend.value.ObjectValue;
import backend.value.Value;

public class GlobalScope implements Scope, HasState {
	private final UndoStack undoStack;
	private final Map<String, Value> values = new HashMap<>();
	private final ValueObserverList<String> valueObserverList = new ValueObserverList<>();
	
	public GlobalScope(UndoStack undoStack) {
		this.undoStack = undoStack;
		
		ObjectValue objectProto = new ObjectValue(undoStack);
		ObjectValue stringProto = new ObjectValue(undoStack);
		ObjectValue arrayProto = new ObjectValue(undoStack);
		
		addNativeFunctionsToObject(objectProto, ObjectProto.class);
		addNativeFunctionsToObject(stringProto, StringProto.class);
		addNativeFunctionsToObject(arrayProto, ArrayProto.class);
		
		addNativeFunctionsToMap(values, Global.class);
		
		values.put("ObjectProto", objectProto);
		values.put("StringProto", stringProto);
		values.put("ArrayProto", arrayProto);
	}
	
	/**
	 * Adds all methods in clz to object as native function values.
	 */
	private static void addNativeFunctionsToObject(ObjectValue object, Class<?> clz) {
		for(final Method method:clz.getMethods()) {
			object.set(method.getName(), createNativeFunctionValue(method));
		}
	}
	
	/**
	 * Adds all methods in clz to map as native function values.
	 */
	private static void addNativeFunctionsToMap(Map<String, Value> map, Class<?> clz) {
		for(final Method method:clz.getDeclaredMethods()) {
			map.put(method.getName(), createNativeFunctionValue(method));
		}
	}

	/**
	 * Creates a native function value that delegates to the given method.
	 */
	private static NativeFunctionValue createNativeFunctionValue(final Method method) {
		return new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				try {
					return (Value) method.invoke(null, runtime, stack, params);
				} catch(InvocationTargetException e) {
					if(e.getCause() instanceof ExecutionException) {
						throw (ExecutionException) e.getCause();
					} else {
						throw new ExecutionException("Error running native function: " + method.getName(), e);
					}
				} catch (ReflectiveOperationException e) {
					throw new ExecutionException("Error running native function: " + method.getName(), e);
				}
			}
		};
	}
	
	public Value get(final String name, ValueReadObserver valueReadObserver) {
		valueObserverList.onReadValue(valueReadObserver, name);
		
		if(values.containsKey(name)) {
			return values.get(name);
		} else {
			return new NullValue();
		}
	}
	
	public void set(final String name, final Value value) {
		if(values.containsKey(name)) {
			final Value oldValue = values.get(name);
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					values.put(name, oldValue);
					valueObserverList.onChangeValue(name);
				}
				
				public String toString() {
					return "[GLOBAL SET]";
				}
			});
		} else {
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					values.remove(name);
				}
				
				public String toString() {
					return "[GLOBAL SET]";
				}
			});
		}
		values.put(name, value);
		valueObserverList.onChangeValue(name);
	}
	
	public void create(final String name) {
		if(!values.containsKey(name)) {
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					values.remove(name);
				}
				
				public String toString() {
					return "[CREATE GLOBAL: " + name + "]";
				}
			});
			values.put(name, new NullValue());
		}
	}
	
	public Scope getParentScope() {
		return null;
	}
	
	public String toString() {
		return "[GLOBALS]";
	}
	
	@Override
	public String getState(String prefix, Set<Object> used) {
		StringBuilder s = new StringBuilder();
		for(String name:values.keySet()) {
			s.append(prefix + "Name: " + name).append("\n");
			s.append(prefix + "Value: " + values.get(name).toString()).append("\n");
			//s.append(values.get(name).getState(prefix + "  ", used)).append("\n");
		}
		
		return s.toString().replaceAll("\\s+$", "");
	}
}