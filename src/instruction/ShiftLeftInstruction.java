package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;

public class ShiftLeftInstruction implements Instruction {
	public ShiftLeftInstruction() {
	}
	
	public static Instruction ShiftLeft() {
		return new ShiftLeftInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.popCheckedDoubleValue();
		DoubleValue value1 = runtime.popCheckedDoubleValue();
		stack.push(DoubleValue.Value((int)value1.getValue() << (int)value2.getValue()));
	}
	
	public String toString() {
		return "SHIFTLEFT";
	}
}