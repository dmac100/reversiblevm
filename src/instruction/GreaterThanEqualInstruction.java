package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.BooleanValue;
import value.DoubleValue;

public class GreaterThanEqualInstruction implements Instruction {
	public GreaterThanEqualInstruction() {
	}
	
	public static Instruction GreaterThanEqual() {
		return new GreaterThanEqualInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.popCheckedDoubleValue();
		DoubleValue value1 = runtime.popCheckedDoubleValue();
		stack.push(BooleanValue.Value(value1.getValue() >= value2.getValue()));
	}
	
	public String toString() {
		return "GREATERTHANEQUAL";
	}
}