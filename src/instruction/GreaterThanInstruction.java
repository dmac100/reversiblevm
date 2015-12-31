package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.BooleanValue;
import value.DoubleValue;

public class GreaterThanInstruction implements Instruction {
	public GreaterThanInstruction() {
	}
	
	public static Instruction GreaterThan() {
		return new GreaterThanInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.popCheckedDoubleValue();
		DoubleValue value1 = runtime.popCheckedDoubleValue();
		stack.push(BooleanValue.Value(value1.getValue() > value2.getValue()));
	}
	
	public String toString() {
		return "GREATERTHAN";
	}
}