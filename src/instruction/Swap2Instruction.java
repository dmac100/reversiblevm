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
		Value value1 = stack.popValue();
		Value value2 = stack.popValue();
		Value value3 = stack.popValue();
		stack.push(value1);
		stack.push(value3);
		stack.push(value2);
	}
	
	public String toString() {
		return "SWAP2";
	}
}