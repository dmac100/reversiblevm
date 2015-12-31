package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;

public class BitwiseNotInstruction implements Instruction {
	public BitwiseNotInstruction() {
	}
	
	public static Instruction BitwiseNot() {
		return new BitwiseNotInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value = runtime.popCheckedDoubleValue();
		stack.push(DoubleValue.Value(~(int)value.getValue()));
	}
	
	public String toString() {
		return "BITWISENOT";
	}
}