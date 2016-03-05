package instruction.viz;

import instruction.Instruction;
import runtime.Runtime;

public class VizFilterInstruction implements Instruction {
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
