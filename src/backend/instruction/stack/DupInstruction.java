package backend.instruction.stack;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.Value;

public class DupInstruction implements Instruction {
	public DupInstruction() {
	}
	
	public static Instruction Dup() {
		return new DupInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value = stack.popValue(false, false);
		stack.push(value, false);
		stack.push(value, false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return "DUP";
	}
}