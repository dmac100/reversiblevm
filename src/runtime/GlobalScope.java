package runtime;

import instruction.EqualInstruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import value.ArrayValue;
import value.BooleanValue;
import value.DoubleValue;
import value.NativeFunctionValue;
import value.NullValue;
import value.ObjectValue;
import value.StringValue;
import value.Value;

public class GlobalScope implements Scope, HasState {
	private Map<String, Value> values = new HashMap<>();
	
	public GlobalScope() {
		ObjectValue objectProto = new ObjectValue();
		ObjectValue stringProto = new ObjectValue();
		ObjectValue arrayProto = new ObjectValue();
		
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
				return new ArrayValue(list);
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
				List<String> keys = runtime.checkObjectValue(params.get(0)).keys();
				List<Value> list = new ArrayList<>();
				for(String key:keys) {
					if(!key.equals("prototype")) {
						list.add(new StringValue(key));
					}
				}
				return new ArrayValue(list);
			}
		});
		
		arrayProto.set("length", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				return array.length();
			}
		});
		
		arrayProto.set("concat", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				ArrayValue other = runtime.checkArrayValue(params.get(1));
				List<Value> list = new ArrayList<>();
				for(Value value:array.values()) {
					list.add(value);
				}
				for(Value value:other.values()) {
					list.add(value);
				}
				return new ArrayValue(list);
			}
		});
		
		arrayProto.set("indexOf", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				Value value = params.get(1);
				List<Value> values = array.values();
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
				List<Value> array = runtime.checkArrayValue(params.get(0)).values();
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
				List<Value> values = array.values();
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
				List<Value> array = runtime.checkArrayValue(params.get(0)).values();
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
				return new ArrayValue(list);
			}
		});
		
		arrayProto.set("sort", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				for(int i = 0; i < array.length().getValue(); i++) {
					for(int j = i + 1; j < array.length().getValue(); j++) {
						String value1 = array.get(new DoubleValue(i)).toString();
						String value2 = array.get(new DoubleValue(j)).toString();
						if(value1.compareTo(value2) > 0) {
							Value t = array.get(new DoubleValue(i));
							array.set(new DoubleValue(i), array.get(new DoubleValue(j)));
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
				List<Value> values = array.values();
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
				List<Value> values = array.values();
				values.add(value);
				array.setValues(values);
				return value;
			}
		});
		
		arrayProto.set("reverse", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				List<Value> values = array.values();
				Collections.reverse(values);
				array.setValues(values);
				return array;
			}
		});
		
		arrayProto.set("shift", new NativeFunctionValue() {
			protected Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException {
				ArrayValue array = runtime.checkArrayValue(params.get(0));
				List<Value> values = array.values();
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
				List<Value> values = array.values();
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
				List<Value> values = array.values();
				for(int i = 0; i < count; i++) {
					if(index < values.size()) {
						removed.add(values.remove(index));
					}
				}
				array.setValues(values);
				return new ArrayValue(removed);
			}
		});
	}
	
	public Value get(String name) {
		if(values.containsKey(name)) {
			return values.get(name);
		} else {
			return new NullValue();
		}
	}

	public void set(String name, Value value) {
		values.put(name, value);
	}
	
	public void create(String name) {
		if(!values.containsKey(name)) {
			values.put(name, new NullValue());
		}
	}
	
	public Scope getParentScope() {
		return null;
	}
	
	public String toString() {
		return "{GLOBALS}";
		//return values.toString();
	}
	
	@Override
	public String getState(String prefix, Set<Object> used) {
		StringBuilder s = new StringBuilder();
		for(String name:values.keySet()) {
			s.append(prefix + "Name: " + name).append("\n");
			s.append(values.get(name).getState(prefix + "  ", used)).append("\n");
		}
		
		return s.toString().replaceAll("\\s+$", "");
	}
}