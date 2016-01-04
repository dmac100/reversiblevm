package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.Value;

public class Dup2Instruction implements Instruction {
	public Dup2Instruction() {
	}
	
	public static Instruction Dup2() {
		return new Dup2Instruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value1 = stack.popValue();
		Value value2 = stack.popValue();
		stack.push(value2);
		stack.push(value1);
		stack.push(value2);
		stack.push(value1);
	}
	
	public String toString() {
		return "DUP2";
	}
}