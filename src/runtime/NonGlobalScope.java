package runtime;

import java.util.HashMap;
import java.util.Map;

import value.NullValue;
import value.Value;

public class NonGlobalScope implements Scope {
	private final Scope parentScope;
	private final Map<String, Value> values = new HashMap<>();
	
	public NonGlobalScope(Scope parentScope) {
		this.parentScope = parentScope;
	}
	
	public Value get(String name) {
		if(values.containsKey(name)) {
			return values.get(name);
		} else {
			return parentScope.get(name);
		}
	}

	public void set(String name, Value value) {
		if(values.containsKey(name)) {
			values.put(name, value);
		} else {
			parentScope.set(name, value);
		}
	}

	public void create(String name) {
		if(!values.containsKey(name)) {
			values.put(name, new NullValue());
		}
	}
	
	public Scope getParentScope() {
		return parentScope;
	}
	
	public String toString() {
		return values + " -> " + parentScope;
	}
}
