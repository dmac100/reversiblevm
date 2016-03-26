package backend.runtime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import backend.observer.ValueObserverList;
import backend.observer.ValueReadObserver;
import backend.value.NullValue;
import backend.value.Value;

public class NonGlobalScope implements Scope, HasState {
	private final Scope parentScope;
	private final UndoStack undoStack;
	private final Map<String, Value> values = new LinkedHashMap<>();
	private final ValueObserverList<String> valueObserverList = new ValueObserverList<>();

	public NonGlobalScope(Scope parentScope, UndoStack undoStack) {
		this.parentScope = parentScope;
		this.undoStack = undoStack;
	}

	public Value get(final String name, ValueReadObserver valueReadObserver) {
		if(values.containsKey(name)) {
			valueObserverList.onReadValue(valueReadObserver, name);
			return values.get(name);
		} else {
			return parentScope.get(name, valueReadObserver);
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
					return "[SET]";
				}
			});
			values.put(name, value);
			valueObserverList.onChangeValue(name);
		} else {
			parentScope.set(name, value);
		}
	}

	public void create(final String name) {
		if(!values.containsKey(name)) {
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					values.remove(name);
				}
				
				public String toString() {
					return "[CREATE: " + name + "]";
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
		if(used.contains(this)) return prefix + "[CYCLIC]";
		used.add(this);

		StringBuilder s = new StringBuilder();
		for(String name:values.keySet()) {
			s.append(prefix + "Name: " + name).append("\n");
			s.append(values.get(name).getState(prefix + "  ", used)).append("\n");
		}
		s.append(prefix + "ParentScope:").append("\n");
		s.append(parentScope.getState(prefix + "  ", used));

		used.remove(this);

		return s.toString().replaceAll("\\s+$", "");
	}
}
