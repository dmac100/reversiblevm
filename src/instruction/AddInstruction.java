package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;
import value.StringValue;
import value.Value;

public class AddInstruction implements Instruction {
	public AddInstruction() {
	}
	
	public static Instruction Add() {
		return new AddInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value2 = stack.popValue();
		Value value1 = stack.popValue();
		if(value1 instanceof StringValue || value2 instanceof StringValue) {
			String value = value1.toString() + value2.toString();
			stack.push(StringValue.Value(value));
		} else {
			double sum = runtime.checkDoubleValue(value1).getValue() + runtime.checkDoubleValue(value2).getValue();
			stack.push(DoubleValue.Value(sum));
		}
	}
	
	public String toString() {
		return "ADD";
	}
}