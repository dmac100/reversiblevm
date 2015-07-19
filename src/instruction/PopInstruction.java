package instruction;

import runtime.Runtime;

public class PopInstruction implements Instruction {
	public PopInstruction() {
	}
	
	public static Instruction Pop() {
		return new PopInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.getStack().popValue();
	}
	
	public String toString() {
		return "POP";
	}
}