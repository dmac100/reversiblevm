package runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import value.NullValue;
import value.Value;
import callback.CanFireValueRead;
import callback.ValueChangeCallbacks;

public class NonGlobalScope implements Scope, HasState {
	private final Scope parentScope;
	private final UndoStack undoStack;
	private final Map<String, Value> values = new HashMap<>();
	private final ValueChangeCallbacks<String> valueChangeCallbacks = new ValueChangeCallbacks<>();

	public NonGlobalScope(Scope parentScope, UndoStack undoStack) {
		this.parentScope = parentScope;
		this.undoStack = undoStack;
	}

	public Value get(final String name, CanFireValueRead callbacks) {
		if(values.containsKey(name)) {
			valueChangeCallbacks.fireReadCallbacks(callbacks, name);
			return values.get(name);
		} else {
			return parentScope.get(name, callbacks);
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
			values.put(name, value);
			
			valueChangeCallbacks.fireWriteCallbacks(name);
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
