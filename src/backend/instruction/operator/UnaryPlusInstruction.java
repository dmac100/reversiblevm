package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.Stack;

public class UnaryPlusInstruction extends Instruction {
	public UnaryPlusInstruction() {
	}
	
	public static Instruction UnaryPlus() {
		return new UnaryPlusInstruction();
	}
	
	public Instruction copy() {
		return new UnaryPlusInstruction();
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		runtime.checkDoubleValue(stack.peekValue(0));
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "UNARYPLUS";
	}
}