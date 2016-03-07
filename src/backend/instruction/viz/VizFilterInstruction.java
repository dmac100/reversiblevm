package backend.instruction.viz;

import backend.instruction.Instruction;
import backend.runtime.Runtime;

public class VizFilterInstruction extends Instruction {
	public VizFilterInstruction() {
	}
	
	public static Instruction VizFilterInstruction() {
		return new VizFilterInstruction();
	}
	
	public void execute(Runtime runtime) {
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "VIZFILTER";
	}
}
