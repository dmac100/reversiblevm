package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;
import value.Value;

public class AddInstruction implements Instruction {
	public AddInstruction() {
	}
	
	public static Instruction Add() {
		return new AddInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.popCheckedDoubleValue();
		DoubleValue value1 = runtime.popCheckedDoubleValue();
		stack.push(DoubleValue.Value(value1.getValue() + value2.getValue()));
	}
	
	public String toString() {
		return "ADD";
	}
}