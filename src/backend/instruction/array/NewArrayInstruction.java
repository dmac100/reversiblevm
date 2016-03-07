package backend.instruction.array;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.value.ArrayValue;

public class NewArrayInstruction extends Instruction {
	public NewArrayInstruction() {
	}
	
	public static Instruction NewArray() {
		return new NewArrayInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.getStack().push(new ArrayValue(runtime.getUndoStack()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return "NEWARRAY";
	}
}