package backend.instruction.stack;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.value.Value;

public class PushInstruction extends Instruction {
	private final Value value;

	public PushInstruction(Value value) {
		this.value = value;
	}
	
	public static Instruction Push(Value value) {
		return new PushInstruction(value);
	}
	
	public void execute(Runtime runtime) {
		runtime.getStack().push(value, false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return "PUSH: " + value;
	}
}