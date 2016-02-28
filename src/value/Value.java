package value;

import java.util.HashSet;
import java.util.Set;

import runtime.HasState;

public abstract class Value implements HasState {
	public String toString() {
		return toString(new HashSet<Value>());
	}
	
	public String getState(String prefix, Set<Object> used) {
		return prefix + toString();
	}
	
	public abstract String toString(Set<Value> used);
}