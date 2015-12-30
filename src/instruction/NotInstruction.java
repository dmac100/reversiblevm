package instruction;

import runtime.Runtime;

public class NotInstruction implements Instruction {
	public NotInstruction() {
	}
	
	public static Instruction Not() {
		return new NotInstruction();
	}
	
	public void execute(Runtime runtime) {
	}
	
	public String toString() {
		return "NOT";
	}
}