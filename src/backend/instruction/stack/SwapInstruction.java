package backend.instruction.stack;

import backend.instruction.Instruction;
import backend.instruction.operator.UnsignedShiftRightInstruction;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.Value;

public class SwapInstruction extends Instruction {
	public SwapInstruction() {
	}
	
	public static Instruction Swap() {
		return new SwapInstruction();
	}
	
	public Instruction copy() {
		return new SwapInstruction();
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Value value1 = stack.popValue(false, false);
		Value value2 = stack.popValue(false, false);
		stack.push(value1, false);
		stack.push(value2, false);
	}
	
	public void undo(Runtime runtime) {
		Stack stack = runtime.getStack();
		Value value1 = stack.popValue(false, false);
		Value value2 = stack.popValue(false, false);
		stack.push(value1, false);
		stack.push(value2, false);
	}
	
	public String toString() {
		return "SWAP";
	}
}