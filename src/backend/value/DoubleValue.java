package backend.value;

import java.util.Set;

public class DoubleValue extends Value {
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
	
	public String toString(Set<Value> used) {
		int intValue = (int)value;
		if(intValue == value) {
			return String.valueOf(intValue);
		} else {
			return String.valueOf(value);
		}
	}
}