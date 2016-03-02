package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.BooleanValue;
import value.DoubleValue;

public class LessThanInstruction implements Instruction {
	public LessThanInstruction() {
	}
	
	public static Instruction LessThan() {
		return new LessThanInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue(true));
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue(true));
		stack.push(BooleanValue.Value(value1.getValue() < value2.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false);
	}
	
	public String toString() {
		return "LESSTHAN";
	}
}