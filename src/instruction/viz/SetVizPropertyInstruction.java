package instruction.viz;

import instruction.Instruction;
import runtime.Runtime;

public class SetVizPropertyInstruction implements Instruction {
	private final String name;
	
	public SetVizPropertyInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction SetVizPropertyInstruction(String name) {
		return new SetVizPropertyInstruction(name);
	}
	
	public void execute(Runtime runtime) {
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "SETVIZPROPERTY: " + name;
	}
}
