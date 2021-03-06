package backend.value;

import java.util.HashSet;
import java.util.Set;

import backend.runtime.HasState;

public abstract class Value implements HasState {
	public String toString() {
		return toString(new HashSet<Value>());
	}
	
	public String inspect() {
		return inspect("", new HashSet<Value>());
	}
	
	public String inspect(String prefix, Set<Value> used) {
		return prefix + toString(used);
	}
	
	/**
	 * Returns a key that can be compared to see whether two values are equal.
	 */
	public abstract Object getKey();

	public String getState(String prefix, Set<Object> used) {
		return prefix + toString();
	}
	
	public abstract String toString(Set<Value> used);
}