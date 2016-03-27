package backend.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import backend.instruction.function.CallInstruction;
import backend.observer.ValueObserverList;
import backend.observer.ValueReadObserver;
import backend.runtime.library.ArrayProto;
import backend.runtime.library.Global;
import backend.runtime.library.MathStatic;
import backend.runtime.library.ObjectProto;
import backend.runtime.library.StringProto;
import backend.value.ArrayValue;
import backend.value.DoubleValue;
import backend.value.NativeFunctionValue;
import backend.value.NullValue;
import backend.value.ObjectValue;
import backend.value.Value;

public class GlobalScope implements Scope, HasState {
	private final UndoStack undoStack;
	private final Map<String, Value> values = new LinkedHashMap<>();
	private final ValueObserverList<String> valueObserverList = new ValueObserverList<>();
	
	public GlobalScope(UndoStack undoStack) {
		this.undoStack = undoStack;
		
		ObjectValue objectProto = new ObjectValue(undoStack);
		ObjectValue stringProto = new ObjectValue(undoStack);
		ObjectValue arrayProto = new ObjectValue(undoStack);
		ObjectValue functionProto = new ObjectValue(undoStack);
		ObjectValue mathStatic = new ObjectValue(undoStack);
		
		addNativeFunctionsToObject(objectProto, ObjectProto.class);
		addNativeFunctionsToObject(stringProto, StringProto.class);
		addNativeFunctionsToObject(arrayProto, ArrayProto.class);
		addNativeFunctionsToObject(mathStatic, MathStatic.class);
		addFunctionPrototypeProperties(functionProto);
		
		addNativeFunctionsToMap(values, Global.class);
		
		values.put("ObjectProto", objectProto);
		values.put("StringProto", stringProto);
		values.put("ArrayProto", arrayProto);
		values.put("FunctionProto", functionProto);
		values.put("Math", mathStatic);
		
		mathStatic.set("E", new DoubleValue(Math.E));
		mathStatic.set("LN2", new DoubleValue(Math.log(2)));
		mathStatic.set("LN10", new DoubleValue(Math.log(10)));
		mathStatic.set("LOG2E", new DoubleValue(Math.log(Math.exp(1)) / Math.log(2)));
		mathStatic.set("LOG10E", new DoubleValue(Math.log10(Math.exp(1))));
		mathStatic.set("PI", new DoubleValue(Math.PI));
		mathStatic.set("SQRT1_2", new DoubleValue(1 / Math.sqrt(2)));
		mathStatic.set("SQRT2", new DoubleValue(Math.sqrt(2)));
		
		values.put("Infinity", new DoubleValue(Double.POSITIVE_INFINITY));
		values.put("NaN", new DoubleValue(Double.NaN));
		values.put("undefined", new NullValue());
	}
	
	private void addFunctionPrototypeProperties(ObjectValue functionProto) {
		functionProto.set("apply", new NativeFunctionValue(undoStack) {
			public Object getKey() {
				return this;
			}
			
			public void execute(Runtime runtime) throws ExecutionException {
				Stack stack = runtime.getStack();
				List<Value> params = getParams(runtime, stack);
				while(params.size() <= 2) params.add(new NullValue());
				
				// Read [function, this, [params...]] from stack and push [this, params, paramsLength, function].
				Value functionValue = params.get(0);
				Value thisValue = params.get(1);
				ArrayValue paramsValue = runtime.checkArrayValue(params.get(2));
				
				stack.push(thisValue, true);
				for(int i = 0; i < (int) paramsValue.length(runtime).getValue(); i++) {
					stack.push(paramsValue.get(new DoubleValue(i), runtime), true);
				}
				stack.push(new DoubleValue(paramsValue.length(runtime).getValue() + 1), true);
				stack.push(functionValue, true);
				
				// Execute the function.
				new CallInstruction().execute(runtime);
			}
			
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) {
				throw new IllegalStateException();
			}
		});
		
		functionProto.set("call", new NativeFunctionValue(undoStack) {
			public Object getKey() {
				return this;
			}
			
			public void execute(Runtime runtime) throws ExecutionException {
				Stack stack = runtime.getStack();
				List<Value> params = getParams(runtime, stack);
				while(params.size() <= 0) params.add(new NullValue());
				
				// Read [function, this, params...] from stack and push [this, params, paramsLength, function].
				Value functionValue = params.get(0);
				for(int i = 1; i < params.size(); i++) {
					stack.push(params.get(i), true);
				}
				stack.push(new DoubleValue(params.size() - 1), true);
				stack.push(functionValue, true);
				
				// Execute the function.
				new CallInstruction().execute(runtime);
			}
			
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) {
				throw new IllegalStateException();
			}
		});
	}

	/**
	 * Adds all methods in clz to object as native function values.
	 */
	private void addNativeFunctionsToObject(ObjectValue object, Class<?> clz) {
		for(final Method method:clz.getMethods()) {
			object.set(method.getName(), createNativeFunctionValue(method));
		}
	}
	
	/**
	 * Adds all methods in clz to map as native function values.
	 */
	private void addNativeFunctionsToMap(Map<String, Value> map, Class<?> clz) {
		for(final Method method:clz.getDeclaredMethods()) {
			map.put(method.getName(), createNativeFunctionValue(method));
		}
	}

	/**
	 * Creates a native function value that delegates to the given method.
	 */
	private NativeFunctionValue createNativeFunctionValue(final Method method) {
		return new NativeFunctionValue(undoStack) {
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
			
			public Object getKey() {
				return this;
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