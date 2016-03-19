package backend.value;

import java.util.Set;

public class NullValue extends Value implements ImmutableValue {
	private static final Object key = new Object();
	
	public static Value NullValue() {
		return new NullValue();
	}
	
	public String toString(Set<Value> used) {
		return "null";
	}
	
	@Override
	public Object getKey() {
		return key;
	}
}