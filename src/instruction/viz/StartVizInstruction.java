package instruction.viz;

import instruction.Instruction;
import runtime.Runtime;

public class StartVizInstruction implements Instruction {
	public StartVizInstruction() {
	}
	
	public static Instruction StartVizInstruction() {
		return new StartVizInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.setInVizInstruction(true);
	}
	
	public void undo(Runtime runtime) {
		runtime.setInVizInstruction(false);
	}
	
	public String toString() {
		return "STARTVIZ";
	}
}
