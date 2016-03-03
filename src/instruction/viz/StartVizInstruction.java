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
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "STARTVIZ";
	}
}
