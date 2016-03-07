package backend.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import backend.instruction.operator.EqualInstruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueObserverList;
import backend.observer.ValueReadObserver;
import backend.value.ArrayValue;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.NativeFunctionValue;
import backend.value.NullValue;
import backend.value.ObjectValue;
import backend.value.StringValue;
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
		
		values.put("ObjectProto", objectProto);
		values.put("StringProto", stringProto);
		values.put("ArrayProto", arrayProto);
		
		values.put("print", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) {
				StringBuilder value = new StringBuilder();
				for(int i = 1; i < params.size(); i++) {
					if(i != 1) {
						value.append(" ");
					}
					value.append(params.get(i).toString());
				}
				runtime.print(value.toString());
				return new NullValue();
			}
		});
		
		values.put("range", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) {
				while(params.size() <= 1) params.add(new NullValue());
				int max = (int) runtime.checkDoubleValue(params.get(1)).getValue();
				List<Value> list = new ArrayList<>();
				for(int i = 0; i < max; i++) {
					list.add(new DoubleValue(i));
				}
				return new ArrayValue(list, runtime.getUndoStack());
			}
		});
		
		stringProto.set("length", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				return new DoubleValue(string.length());
			}
		});
		
		stringProto.set("charAt", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				int index = (int)runtime.checkDoubleValue(params.get(1)).getValue();
				return new StringValue(String.valueOf(string.charAt(index)));
			}
		});
		
		stringProto.set("concat", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				String other = runtime.checkStringValue(params.get(1)).getValue();
				return new StringValue(string + other);
			}
		});
		
		stringProto.set("endsWith", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				String other = runtime.checkStringValue(params.get(1)).getValue();
				return new BooleanValue(string.endsWith(other));
			}
		});
		
		stringProto.set("indexOf", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				String other = runtime.checkStringValue(params.get(1)).getValue();
				return new DoubleValue(string.indexOf(other));
			}
		});
		
		stringProto.set("lastIndexOf", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				String other = runtime.checkStringValue(params.get(1)).getValue();
				return new DoubleValue(string.lastIndexOf(other));
			}
		});
		
		stringProto.set("repeat", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				int count = (int)runtime.checkDoubleValue(params.get(1)).getValue();
				StringBuilder s = new StringBuilder();
				for(int i = 0; i < count; i++) {
					s.append(string);
				}
				return new StringValue(s.toString());
			}
		});
		
		stringProto.set("substring", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				int start = (int)runtime.checkDoubleValue(params.get(1)).getValue();
				int end = (int)runtime.checkDoubleValue(params.get(2)).getValue();
				start = Math.max(start, 0);
				start = Math.min(start, string.length());
				end = Math.max(end, 0);
				end = Math.min(end, string.length());
				return new StringValue(string.substring(start, end));
			}
		});
		
		stringProto.set("split", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				String separator = runtime.checkStringValue(params.get(1)).getValue();
				List<Value> list = new ArrayList<>();
				for(String value:string.split(separator)) {
					list.add(new StringValue(value));
				}
				if(string.endsWith(separator)) {
					list.add(new StringValue(""));
				}
				return new ArrayValue(list, runtime.getUndoStack());
			}
		});
		
		stringProto.set("startsWith", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				String other = runtime.checkStringValue(params.get(1)).getValue();
				return new BooleanValue(string.startsWith(other));
			}
		});
		
		stringProto.set("toLowerCase", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				return new StringValue(string.toLowerCase());
			}
		});
		
		stringProto.set("toUpperCase", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				return new StringValue(string.toUpperCase());
			}
		});
		
		stringProto.set("trim", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				String string = runtime.checkStringValue(params.get(0)).getValue();
				return new StringValue(string.trim());
			}
		});
		
		objectProto.set("keys", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				List<String> keys = runtime.checkObjectValue(params.get(0)).keys(runtime);
				List<Value> list = new ArrayList<>();
				for(String key:keys) {
					if(!key.equals("prototype")) {
						list.add(new StringValue(key));
					}
				}
				return new ArrayValue(list, runtime.getUndoStack());
			}
		});
		
		objectProto.set("values", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ObjectValue object = runtime.checkObjectValue(params.get(0));
				List<String> keys = object.keys(runtime);
				List<Value> list = new ArrayList<>();
				for(String key:keys) {
					if(!key.equals("prototype")) {
						list.add(object.get(key, runtime));
					}
				}
				return new ArrayValue(list, runtime.getUndoStack());
			}
		});
		
		arrayProto.set("length", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				return array.length(runtime);
			}
		});
		
		arrayProto.set("concat", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				ArrayValue other = runtime.checkArrayValue(params.get(1));
				List<Value> list = new ArrayList<>();
				for(Value value:array.values(runtime)) {
					list.add(value);
				}
				for(Value value:other.values(runtime)) {
					list.add(value);
				}
				return new ArrayValue(list, runtime.getUndoStack());
			}
		});
		
		arrayProto.set("indexOf", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				Value value = params.get(1);
				List<Value> values = array.values(runtime);
				for(int i = 0; i < values.size(); i++) {
					if(EqualInstruction.equals(values.get(i), value)) {
						return new DoubleValue(i);
					}
				}
				return new DoubleValue(-1);
			}
		});
		
		arrayProto.set("join", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				List<Value> array = runtime.checkArrayValue(params.get(0)).values(runtime);
				String separator = runtime.checkStringValue(params.get(1)).getValue();
				StringBuilder s = new StringBuilder();
				for(int i = 0; i < array.size(); i++) {
					if(i > 0) {
						s.append(separator);
					}
					s.append(array.get(i));
				}
				return new StringValue(s.toString());
			}
		});
		
		arrayProto.set("lastIndexOf", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				Value value = params.get(1);
				List<Value> values = array.values(runtime);
				for(int i = values.size() - 1; i >= 0; i--) {
					if(EqualInstruction.equals(values.get(i), value)) {
						return new DoubleValue(i);
					}
				}
				return new DoubleValue(-1);
			}
		});
		
		arrayProto.set("slice", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				List<Value> array = runtime.checkArrayValue(params.get(0)).values(runtime);
				int start = (int)runtime.checkDoubleValue(params.get(1)).getValue();
				int end = (int)runtime.checkDoubleValue(params.get(2)).getValue();
				start = Math.max(start, 0);
				start = Math.min(start, array.size());
				end = Math.max(end, 0);
				end = Math.min(end, array.size());
				List<Value> list = new ArrayList<>();
				for(int i = start; i < end; i++) {
					list.add(array.get(i));
				}
				return new ArrayValue(list, runtime.getUndoStack());
			}
		});
		
		arrayProto.set("sort", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				for(int i = 0; i < array.length(runtime).getValue(); i++) {
					for(int j = i + 1; j < array.length(runtime).getValue(); j++) {
						String value1 = array.get(new DoubleValue(i), runtime).toString();
						String value2 = array.get(new DoubleValue(j), runtime).toString();
						if(value1.compareTo(value2) > 0) {
							Value t = array.get(new DoubleValue(i), runtime);
							array.set(new DoubleValue(i), array.get(new DoubleValue(j), runtime));
							array.set(new DoubleValue(j), t);
						}
					}
				}
				return array;
			}
		});
		
		arrayProto.set("pop", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				List<Value> values = array.values(runtime);
				if(values.isEmpty()) {
					return new NullValue();
				} else {
					Value value = values.remove(values.size() - 1);
					array.setValues(values);
					return value;
				}
			}
		});
		
		arrayProto.set("push", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				Value value = params.get(1);
				List<Value> values = array.values(runtime);
				values.add(value);
				array.setValues(values);
				return value;
			}
		});
		
		arrayProto.set("reverse", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				List<Value> values = array.values(runtime);
				Collections.reverse(values);
				array.setValues(values);
				return array;
			}
		});
		
		arrayProto.set("shift", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				List<Value> values = array.values(runtime);
				if(values.isEmpty()) {
					return new NullValue();
				} else {
					Value value = values.remove(0);
					array.setValues(values);
					return value;
				}
			}
		});
		
		arrayProto.set("unshift", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				Value value = params.get(1);
				List<Value> values = array.values(runtime);
				values.add(0, value);
				array.setValues(values);
				return value;
			}
		});
		
		arrayProto.set("splice", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				int index = (int)runtime.checkDoubleValue(params.get(1)).getValue();
				int count = (int)runtime.checkDoubleValue(params.get(2)).getValue();
				List<Value> removed = new ArrayList<>();
				List<Value> values = array.values(runtime);
				for(int i = 0; i < count; i++) {
					if(index < values.size()) {
						removed.add(values.remove(index));
					}
				}
				array.setValues(values);
				return new ArrayValue(removed, runtime.getUndoStack());
			}
		});

		arrayProto.set("keys", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				List<Value> list = new ArrayList<>();
				for(int i = 0; i < array.length(runtime).getValue(); i++) {
					list.add(new DoubleValue(i));
				}
				return new ArrayValue(list, runtime.getUndoStack());
			}
		});
		
		arrayProto.set("values", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				List<Value> list = new ArrayList<>();
				for(int i = 0; i < array.length(runtime).getValue(); i++) {
					list.add(array.get(new DoubleValue(i), runtime));
				}
				return new ArrayValue(list, runtime.getUndoStack());
			}
		});
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
				}
			});
		} else {
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					values.remove(name);
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