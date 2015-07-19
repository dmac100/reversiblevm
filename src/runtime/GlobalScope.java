package runtime;

import java.util.HashMap;
import java.util.Map;

import value.NativeFunctionValue;
import value.NullValue;
import value.Value;

public class GlobalScope implements Scope {
	private Map<String, Value> values = new HashMap<>();
	
	public Value get(String name) {
		if(name.equals("print")) {
			return new NativeFunctionValue("print");
		} else {
			if(values.containsKey(name)) {
				return values.get(name);
			} else {
				return new NullValue();
			}
		}
	}

	public void set(String name, Value value) {
		values.put(name, value);
	}
}