package backend.value;

import java.util.Set;

public class StringValue extends Value {
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
}
