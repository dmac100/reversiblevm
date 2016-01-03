package instruction;

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
		Value value1 = stack.popValue();
		Value value2 = stack.popValue();
		stack.push(value1);
		stack.push(value2);
	}
	
	public String toString() {
		return "SWAP";
	}
}