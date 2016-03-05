package instruction.viz;

import instruction.Instruction;
import runtime.Runtime;
import runtime.VizObjectInstructions;
import value.FunctionValue;

public class EndVizInstruction implements Instruction {
	public EndVizInstruction() {
	}
	
	public static Instruction EndVizInstruction() {
		return new EndVizInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.setInVizInstruction(false);
		
		FunctionValue function = runtime.getCurrentStackFrame().getFunction();
		VizObjectInstructions vizObjectInstructions = new VizObjectInstructions(runtime.getCurrentVizInstructions());
		function.addVizObjectInstructions(vizObjectInstructions);
		vizObjectInstructions.updateObjects(runtime);
	}
	
	public void undo(Runtime runtime) {
		runtime.setInVizInstruction(true);
	}
	
	public String toString() {
		return "ENDVIZ";
	}
}
