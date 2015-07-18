package value;

public class DoubleValue implements Value {
	private double value;
	
	public DoubleValue(double value) {
		this.value = value;
	}
	
	public static DoubleValue Value(double value) {
		return new DoubleValue(value);
	}
	
	public double getValue() {
		return value;
	}
	
	public String toString() {
		int intValue = (int)value;
		if(intValue == value) {
			return String.valueOf(intValue);
		} else {
			return String.valueOf(value);
		}
	}
}