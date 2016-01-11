package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;

public class UnsignedShiftRightInstruction implements Instruction {
	public UnsignedShiftRightInstruction() {
	}
	
	public static Instruction UnsignedShiftRight() {
		return new UnsignedShiftRightInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue());
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue());
		stack.push(DoubleValue.Value((int)value1.getValue() >>> (int)value2.getValue()));
	}
	
	public String toString() {
		return "UNSIGNEDSHIFTRIGHT";
	}
}