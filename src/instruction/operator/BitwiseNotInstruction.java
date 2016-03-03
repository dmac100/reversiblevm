package instruction.operator;

import instruction.Instruction;
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
		DoubleValue value = runtime.checkDoubleValue(stack.popValue(false, false));
		stack.push(DoubleValue.Value(~(int)value.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		Stack stack = runtime.getStack();
		DoubleValue value = runtime.checkDoubleValue(stack.popValue(false, false));
		stack.push(DoubleValue.Value(~(int)value.getValue()), false);
	}
	
	public String toString() {
		return "BITWISENOT";
	}
}