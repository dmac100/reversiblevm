package instruction;

import runtime.Runtime;

public class UnaryPlusInstruction implements Instruction {
	public UnaryPlusInstruction() {
	}
	
	public static Instruction UnaryPlus() {
		return new UnaryPlusInstruction();
	}
	
	public void execute(Runtime runtime) {
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "UNARYPLUS";
	}
}