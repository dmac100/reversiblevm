package backend.instruction.stack;

import backend.instruction.Instruction;
import backend.instruction.variable.StoreInstruction;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.Value;

public class Swap2Instruction extends Instruction {
	public Swap2Instruction() {
	}
	
	public static Instruction Swap2() {
		return new Swap2Instruction();
	}
	
	public Instruction copy() {
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