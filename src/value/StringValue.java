package value;

public class StringValue implements Value {
	private String value;
	
	public StringValue(String value) {
		this.value = value;
	}

	public static StringValue Value(String value) {
		return new StringValue(value);
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString() {
		return String.valueOf(value);
	}
}
