package backend.instruction.stack;

import backend.instruction.Instruction;
import backend.runtime.Runtime;

public class PopInstruction extends Instruction {
	public PopInstruction() {
	}
	
	public static Instruction Pop() {
		return new PopInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.getStack().popValue(false, true);
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "POP";
	}
}