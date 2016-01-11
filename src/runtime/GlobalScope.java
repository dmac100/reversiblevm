package runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import value.NativeFunctionValue;
import value.NullValue;
import value.ObjectValue;
import value.StringValue;
import value.Value;

public class GlobalScope implements Scope {
	private Map<String, Value> values = new HashMap<>();
	
	public GlobalScope() {
		values.put("print", new NativeFunctionValue() {
			protected void execute(Runtime runtime, Stack stack, List<Value> params) {
				StringBuilder value = new StringBuilder();
				for(int i = 0; i < params.size(); i++) {
					if(i != 0) {
						value.append(" ");
					}
					value.append(params.get(i).toString());
				}
				runtime.print(value.toString());
				
				stack.push(new NullValue());
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
		return values.toString();
	}
}