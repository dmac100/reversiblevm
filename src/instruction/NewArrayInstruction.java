package instruction;

import runtime.Runtime;
import value.ArrayValue;

public class NewArrayInstruction implements Instruction {
	public NewArrayInstruction() {
	}
	
	public static Instruction NewArray() {
		return new NewArrayInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.getStack().push(new ArrayValue(runtime.getUndoStack()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false);
	}
	
	public String toString() {
		return "NEWARRAY";
	}
}