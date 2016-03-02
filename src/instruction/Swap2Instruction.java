package instruction;

import runtime.Runtime;
import runtime.Stack;
import value.Value;

public class Swap2Instruction implements Instruction {
	public Swap2Instruction() {
	}
	
	public static Instruction Swap2() {
		return new Swap2Instruction();
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Value value1 = stack.popValue(false, false);
		Value value2 = stack.popValue(false, false);
		Value value3 = stack.popValue(false, false);
		stack.push(value1, false);
		stack.push(value3, false);
		stack.push(value2, false);
	}
	
	public void undo(Runtime runtime) {
		Stack stack = runtime.getStack();
		Value value1 = stack.popValue(false, false);
		Value value2 = stack.popValue(false, false);
		Value value3 = stack.popValue(false, false);
		stack.push(value2, false);
		stack.push(value1, false);
		stack.push(value3, false);
	}
	
	public String toString() {
		return "SWAP2";
	}
}