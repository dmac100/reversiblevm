package instruction;

import runtime.Runtime;

public class PopInstruction implements Instruction {
	public PopInstruction() {
	}
	
	public static Instruction Pop() {
		return new PopInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.getStack().popValue(true);
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "POP";
	}
}