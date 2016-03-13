package backend.instruction.viz;

import java.util.List;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.StackFrame;
import backend.runtime.VizObjectInstructions;

public class StartVizInstruction extends Instruction {
	public StartVizInstruction() {
	}
	
	public static Instruction StartVizInstruction() {
		return new StartVizInstruction();
	}
	
	public Instruction copy() {
		return new StartVizInstruction();
	}
	
	public void execute(Runtime runtime) {
		List<Instruction> instructions = runtime.getInstructionsUpTo(StartVizInstruction.class, EndVizInstruction.class);
		final StackFrame stackFrame = runtime.getCurrentStackFrame();
		final VizObjectInstructions vizObjectInstructions = new VizObjectInstructions(runtime, instructions);

		if(!stackFrame.containsVizObjectInstructionsFor(this)) {
			stackFrame.addVizObjectInstructions(this, vizObjectInstructions);
			runtime.getUndoStack().addCommandUndo(new Runnable() {
				public void run() {
					stackFrame.removeVizObjectInstructions(StartVizInstruction.this, vizObjectInstructions);
					stackFrame.updateVizObjects();
				}
			});
		}
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "STARTVIZ";
	}
}
