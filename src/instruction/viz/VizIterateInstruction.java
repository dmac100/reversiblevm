package instruction.viz;

import instruction.Instruction;
import runtime.Runtime;

public class VizIterateInstruction implements Instruction {
	private final String name;
	
	public VizIterateInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction VizIterateInstruction(String name) {
		return new VizIterateInstruction(name);
	}
	
	public void execute(Runtime runtime) {
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "VIZITERATE: " + name;
	}
}
