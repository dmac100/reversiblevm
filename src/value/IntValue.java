package value;

public class IntValue implements Value {
	private int value;
	
	public IntValue(int value) {
		this.value = value;
	}
	
	public static IntValue Value(int value) {
		return new IntValue(value);
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString() {
		return String.valueOf(value);
	}
}