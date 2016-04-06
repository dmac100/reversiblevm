package backend.value;

import java.util.HashSet;
import java.util.Set;

import backend.runtime.UndoStack;

public class StringValue extends Value implements ImmutableValue, HasPropertiesObject {
	private final String value;
	
	public StringValue(String value) {
		this.value = value;
	}

	public static StringValue Value(String value) {
		return new StringValue(value);
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString(Set<Value> used) {
		return String.valueOf(value);
	}
	
	@Override
	public String inspect(String prefix, Set<Value> used) {
		return prefix + "'" + toString(used) + "'";
	}
	
	@Override
	public Object getKey() {
		return value;
	}

	@Override
	public ObjectValue getPropertiesObject() {
		// Return a temporary object to keep this string's own value immutable.
		return new ObjectValue(new UndoStack());
	}
}
