package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.runtime.Runtime;

public class UnaryPlusInstruction extends Instruction {
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