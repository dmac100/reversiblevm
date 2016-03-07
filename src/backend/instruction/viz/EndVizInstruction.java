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
		runtime.setInVizInstruction(false);
		
		FunctionValue function = runtime.getCurrentStackFrame().getFunction();
		VizObjectInstructions vizObjectInstructions = new VizObjectInstructions(runtime, runtime.getCurrentVizInstructions());
		function.addVizObjectInstructions(vizObjectInstructions);
		vizObjectInstructions.updateObjects();
	}
	
	public void undo(Runtime runtime) {
		runtime.setInVizInstruction(true);
	}
	
	public String toString() {
		return "ENDVIZ";
	}
}