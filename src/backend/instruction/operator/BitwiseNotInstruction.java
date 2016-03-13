package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.DoubleValue;

public class BitwiseNotInstruction extends Instruction {
	public BitwiseNotInstruction() {
	}
	
	public static Instruction BitwiseNot() {
		return new BitwiseNotInstruction();
	}
	
	public Instruction copy() {
		return new BitwiseNotInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkDoubleValue(stack.peekValue(0));
		DoubleValue value = runtime.checkDoubleValue(stack.popValue(false, false));
		stack.push(DoubleValue.Value(~(int)value.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		Stack stack = runtime.getStack();
		DoubleValue value = runtime.checkDoubleValue(stack.popValue(false, false));
		stack.push(DoubleValue.Value(~(int)value.getValue()), false);
	}
	
	public String toString() {
		return super.toString() + "BITWISENOT";
	}
}