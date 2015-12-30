package value;

public class BooleanValue implements Value {
	private boolean value;
	
	public BooleanValue(boolean value) {
		this.value = value;
	}
	
	public static BooleanValue Value(boolean value) {
		return new BooleanValue(value);
	}
	
	public boolean getValue() {
		return value;
	}
	
	public String toString() {
		return value ? "true" : "false";
	}
}