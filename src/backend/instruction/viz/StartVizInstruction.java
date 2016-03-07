package backend.instruction.viz;

import backend.instruction.Instruction;
import backend.runtime.Runtime;

public class StartVizInstruction extends Instruction {
	public StartVizInstruction() {
	}
	
	public static Instruction StartVizInstruction() {
		return new StartVizInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.setInVizInstruction(true);
		runtime.getCurrentVizInstructions().clear();
	}
	
	public void undo(Runtime runtime) {
		runtime.setInVizInstruction(false);
	}
	
	public String toString() {
		return "STARTVIZ";
	}
}
