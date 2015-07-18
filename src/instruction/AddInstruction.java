package instruction;

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
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Value value2 = stack.popValue();
		Value value1 = stack.popValue();
		if(value1 instanceof DoubleValue && value2 instanceof DoubleValue) {
			double x = ((DoubleValue)value1).getValue();
			double y = ((DoubleValue)value2).getValue();
			stack.push(DoubleValue.Value(x + y));
		} else {
			runtime.throwError("TypeError: Not a double");
		}
	}
	
	public String toString() {
		return "ADD";
	}
}