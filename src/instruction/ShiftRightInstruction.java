package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;

public class ShiftRightInstruction implements Instruction {
	public ShiftRightInstruction() {
	}
	
	public static Instruction ShiftRight() {
		return new ShiftRightInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue());
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue());
		stack.push(DoubleValue.Value((int)value1.getValue() >> (int)value2.getValue()));
	}
	
	public String toString() {
		return "SHIFTRIGHT";
	}
}