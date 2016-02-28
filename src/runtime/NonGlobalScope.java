package runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import value.NullValue;
import value.Value;

public class NonGlobalScope implements Scope, HasState {
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

	@Override
	public String getState(String prefix, Set<Object> used) {
		StringBuilder s = new StringBuilder();
		for(String name:values.keySet()) {
			s.append(prefix + "Name: " + name).append("\n");
			s.append(values.get(name).getState(prefix + "  ", new HashSet<>())).append("\n");
		}
		s.append(prefix + "ParentScope:").append("\n");
		s.append(parentScope.getState(prefix + "  ", used));
		
		return s.toString().replaceAll("\\s+$", "");
	}
}
