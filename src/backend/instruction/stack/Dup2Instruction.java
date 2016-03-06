package backend.instruction.stack;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.Value;

public class Dup2Instruction implements Instruction {
	public Dup2Instruction() {
	}
	
	public static Instruction Dup2() {
		return new Dup2Instruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value1 = stack.popValue(false, false);
		Value value2 = stack.popValue(false, false);
		stack.push(value2, false);
		stack.push(value1, false);
		stack.push(value2, false);
		stack.push(value1, false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return "DUP2";
	}
}