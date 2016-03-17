package backend.value;

import java.util.Set;

public class BooleanValue extends Value implements ImmutableValue {
	private final boolean value;
	
	public BooleanValue(boolean value) {
		this.value = value;
	}
	
	public static BooleanValue Value(boolean value) {
		return new BooleanValue(value);
	}
	
	public boolean getValue() {
		return value;
	}
	
	public String toString(Set<Value> used) {
		return value ? "true" : "false";
	}
}