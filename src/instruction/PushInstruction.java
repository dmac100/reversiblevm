package instruction;

import runtime.Runtime;
import value.Value;

public class PushInstruction implements Instruction {
	private Value value;

	public PushInstruction(Value value) {
		this.value = value;
	}
	
	public static Instruction Push(Value value) {
		return new PushInstruction(value);
	}
	
	public void execute(Runtime runtime) {
		runtime.getStack().push(value);
	}
	
	public String toString() {
		return "PUSH: " + value;
	}
}