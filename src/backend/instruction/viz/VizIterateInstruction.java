package backend.instruction.viz;

import backend.instruction.Instruction;
import backend.runtime.Runtime;

public class VizIterateInstruction extends Instruction {
	private final String name;
	
	public VizIterateInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction VizIterateInstruction(String name) {
		return new VizIterateInstruction(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void execute(Runtime runtime) {
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "VIZITERATE: " + name;
	}
}
