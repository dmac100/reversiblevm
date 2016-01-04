package value;

import java.util.ArrayList;
import java.util.List;

import runtime.ExecutionException;

public class ArrayValue implements Value {
	private List<Value> values = new ArrayList<>();
	
	public ArrayValue() {
	}
	
	public Value get(DoubleValue indexValue) throws ExecutionException {
		int index = (int)indexValue.getValue();
		if(index < 0) throw new ExecutionException("Invalid index: " + index);
		if(index >= values.size()) {
			return new NullValue();
		} else {
			return values.get(index);
		}
	}
	
	public void set(DoubleValue indexValue, Value value) throws ExecutionException {
		int index = (int)indexValue.getValue();
		if(index < 0) throw new ExecutionException("Invalid index: " + index);
		while(values.size() <= index) {
			values.add(new NullValue());
		}
		values.set(index, value);
	}
	
	public void push(Value value) {
		values.add(value);
	}

	public static ArrayValue Value() {
		return new ArrayValue();
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(Value value:values) {
			if(s.length() != 0) {
				s.append(", ");
			}
			s.append(value);
		}
		return "[" + s.toString() + "]";
	}
}
