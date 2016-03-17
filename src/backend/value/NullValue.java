package backend.value;

import java.util.Set;

public class NullValue extends Value implements ImmutableValue {
	public static Value NullValue() {
		return new NullValue();
	}
	
	public String toString(Set<Value> used) {
		return "null";
	}
}