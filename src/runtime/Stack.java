package runtime;

import java.util.ArrayList;
import java.util.List;

import value.DoubleValue;
import value.NativeFunctionValue;
import value.Value;

public class Stack {
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
	
	public DoubleValue popDoubleValue() {
		return (DoubleValue) popValue();
	}
	
	public NativeFunctionValue popFunctionValue() {
		return (NativeFunctionValue) popValue();
	}
	
	public String toString() {
		return stack.toString();
	}
}