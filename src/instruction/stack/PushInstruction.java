package instruction.stack;

import instruction.Instruction;
import runtime.Runtime;
import value.Value;

public class PushInstruction implements Instruction {
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