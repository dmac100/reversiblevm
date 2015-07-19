package value;

public class NullValue implements Value {
	public static Value NullValue() {
		return new NullValue();
	}
	
	public String toString() {
		return "null";
	}
}