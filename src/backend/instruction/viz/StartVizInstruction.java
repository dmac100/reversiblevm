package backend.instruction.viz;

import java.util.List;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.StackFrame;
import backend.runtime.VizObject;
import backend.runtime.VizObjectInstructions;
import backend.value.Identifier;

import com.google.common.base.Supplier;

public class StartVizInstruction extends Instruction {
	private final Identifier identifier;
	
	public StartVizInstruction(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public static Instruction StartVizInstruction(Identifier identifier) {
		return new StartVizInstruction(identifier);
	}
	
	public Instruction copy() {
		return new StartVizInstruction(identifier);
	}
	
	public void execute(final Runtime runtime) {
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
				
				public String toString() {
					return "[STARTVIZINSTRUCTION]";
				}
			});
		}
		
		runtime.getCurrentStackFrame().setIdentifierValue(identifier, new Supplier<String>() {
			public String get() {
				StringBuilder s = new StringBuilder();
				runtime.refreshVizObjects(false);
				for(VizObject vizObject:vizObjectInstructions.getVizObjects()) {
					s.append(vizObject.toString()).append("\n");
				}
				return s.toString().trim();
			}
		});
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "STARTVIZ";
	}
}
