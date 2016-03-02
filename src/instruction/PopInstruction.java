package instruction;

import runtime.Runtime;

public class PopInstruction implements Instruction {
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