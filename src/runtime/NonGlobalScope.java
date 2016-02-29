package runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import value.NullValue;
import value.Value;

public class NonGlobalScope implements Scope, HasState {
	private final Scope parentScope;
	private final UndoStack undoStack;
	private final Map<String, Value> values = new HashMap<>();
	
	public NonGlobalScope(Scope parentScope, UndoStack undoStack) {
		this.parentScope = parentScope;
		this.undoStack = undoStack;
	}
	
	public Value get(String name) {
		if(values.containsKey(name)) {
			return values.get(name);
		} else {
			return parentScope.get(name);
		}
	}

	public void set(final String name, final Value value) {
		if(values.containsKey(name)) {
			final Value oldValue = values.get(name);
			undoStack.add(new Runnable() {
				public void run() {
					values.put(name, oldValue);
				}
			});
			values.put(name, value);
		} else {
			parentScope.set(name, value);
		}
	}

	public void create(final String name) {
		if(!values.containsKey(name)) {
			undoStack.add(new Runnable() {
				public void run() {
					values.remove(name);
				}
			});
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
