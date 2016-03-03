package instruction.viz;

import instruction.Instruction;
import runtime.Runtime;

public class EndVizInstruction implements Instruction {
	public EndVizInstruction() {
	}
	
	public static Instruction EndVizInstruction() {
		return new EndVizInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.setInVizInstruction(false);
	}
	
	public void undo(Runtime runtime) {
		runtime.setInVizInstruction(true);
	}
	
	public String toString() {
		return "ENDVIZ";
	}
}
