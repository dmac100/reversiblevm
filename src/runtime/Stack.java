package runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import value.Value;

public class Stack implements HasState {
	private List<Value> stack = new ArrayList<>();

	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	public void push(Value value) {
		stack.add(value);
	}
	
	public Value popValue() {
		Value value = stack.remove(stack.size() - 1);
		return (Value) value;
	}
	
	public String toString() {
		return stack.toString();
	}

	public String getState(String prefix, Set<Object> used) {
		StringBuilder s = new StringBuilder();
		for(Value value:stack) {
			s.append(value.getState(prefix, used)).append("\n");
		}
		return s.toString();
	}
}