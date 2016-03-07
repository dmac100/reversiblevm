package backend.instruction.viz;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.VizObjectInstructions;
import backend.value.FunctionValue;

public class EndVizInstruction extends Instruction {
	public EndVizInstruction() {
	}
	
	public static Instruction EndVizInstruction() {
		return new EndVizInstruction();
	}
	
	public void execute(Runtime runtime) {
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "ENDVIZ";
	}
}
