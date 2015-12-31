package runtime;

import java.util.HashMap;
import java.util.Map;

import value.NativeFunctionValue;
import value.NullValue;
import value.Value;

public class GlobalScope implements Scope {
	private Map<String, Value> values = new HashMap<>();
	
	public GlobalScope() {
		values.put("print", new NativeFunctionValue("print"));
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
}