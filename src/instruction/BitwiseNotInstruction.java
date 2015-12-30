package instruction;

import runtime.Runtime;

public class BitwiseNotInstruction implements Instruction {
	public BitwiseNotInstruction() {
	}
	
	public static Instruction BitwiseNot() {
		return new BitwiseNotInstruction();
	}
	
	public void execute(Runtime runtime) {
	}
	
	public String toString() {
		return "BITWISENOT";
	}
}