package backend.instruction.viz;

import java.util.List;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.VizObjectInstructions;
import backend.value.FunctionValue;

public class StartVizInstruction extends Instruction {
	public StartVizInstruction() {
	}
	
	public static Instruction StartVizInstruction() {
		return new StartVizInstruction();
	}
	
	public void execute(Runtime runtime) {
		List<Instruction> instructions = runtime.getInstructionsUpTo(StartVizInstruction.class, EndVizInstruction.class);
		FunctionValue function = runtime.getCurrentStackFrame().getFunction();
		
		VizObjectInstructions vizObjectInstructions = new VizObjectInstructions(runtime, instructions);
		function.addVizObjectInstructions(vizObjectInstructions);
		vizObjectInstructions.updateObjects();
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "STARTVIZ";
	}
}
