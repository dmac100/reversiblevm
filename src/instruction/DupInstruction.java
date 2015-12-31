package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.Value;

public class DupInstruction implements Instruction {
	public DupInstruction() {
	}
	
	public static Instruction Dup() {
		return new DupInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value = stack.popValue();
		stack.push(value);
		stack.push(value);
	}
	
	public String toString() {
		return "DUP";
	}
}