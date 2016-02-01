package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;

public class DivideInstruction implements Instruction {
	public DivideInstruction() {
	}
	
	public static Instruction Divide() {
		return new DivideInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue());
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue());
		stack.push(DoubleValue.Value(value1.getValue() / value2.getValue()));
	}
	
	public String toString() {
		return "DIVIDE";
	}
}