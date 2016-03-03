package instruction.stack;

import instruction.Instruction;
import runtime.Runtime;
import runtime.Stack;
import value.Value;

public class SwapInstruction implements Instruction {
	public SwapInstruction() {
	}
	
	public static Instruction Swap() {
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