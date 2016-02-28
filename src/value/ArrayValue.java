package value;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import runtime.ExecutionException;
import runtime.HasState;

public class ArrayValue extends Value implements HasState {
	private List<Value> values = new ArrayList<>();
	
	public ArrayValue() {
	}
	
	public ArrayValue(List<Value> values) {
		this.values = values;
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
	
	public DoubleValue length() {
		return new DoubleValue(values.size());
	}
	
	public void setValues(List<Value> values) {
		this.values = values;
	}
	
	public List<Value> values() {
		return new ArrayList<>(values);
	}

	public static ArrayValue Value() {
		return new ArrayValue();
	}
	
	public String toString(Set<Value> used) {
		if(used.contains(this)) return "[CYCLIC]";
		used.add(this);
		
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < values.size(); i++) {
			if(i > 0) {
				s.append(", ");
			}
			s.append(values.get(i).toString(used));
		}
		
		used.remove(this);
		
		return "[" + s.toString() + "]";
	}
	
	public String getState(String prefix, Set<Object> used) {
		if(used.contains(this)) return prefix + "[CYCLIC]";
		used.add(this);
		
		StringBuilder s = new StringBuilder();
		s.append(prefix + "Array:").append("\n");
		for(int i = 0; i < values.size(); i++) {
			s.append(values.get(i).getState(prefix + "  ", used)).append("\n");
		}
		
		used.remove(this);
		
		return s.toString().replaceAll("\\s+$", "");
	}
}
