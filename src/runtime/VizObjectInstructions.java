package runtime;

import instruction.Instruction;
import instruction.viz.VizFilterInstruction;
import instruction.viz.VizIterateInstruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import value.ArrayValue;
import value.FunctionValue;
import value.Value;

public class VizObjectInstructions {
	private final List<Instruction> instructions;
	private final List<VizObject> vizObjects = new ArrayList<>();
	
	public VizObjectInstructions(List<Instruction> instructions) {
		this.instructions = new ArrayList<>(instructions);
	}

	public void updateObjects(Runtime runtime) {
		runtime.getCurrentVizObjects().clear();
		
		List<Instruction> executedInstructions = new ArrayList<>();
		
		executeInstructions(runtime, instructions, executedInstructions);
		
		Collections.reverse(executedInstructions);
		for(Instruction instruction:executedInstructions) {
			instruction.undo(runtime);
			runtime.getUndoStack().undoCommands();
		}
		
		vizObjects.addAll(runtime.getCurrentVizObjects());
		
		runtime.getCurrentVizObjects().clear();
	}

	private void executeInstructions(Runtime runtime, List<Instruction> instructions, List<Instruction> executedInstructions) {
		for(int i = 0; i < instructions.size(); i++) {
			Instruction instruction = instructions.get(i);
			executedInstructions.add(instruction);
			runtime.getUndoStack().saveUndoPoint();

			if(instruction instanceof VizIterateInstruction) {
				String name = ((VizIterateInstruction)instruction).getName();
				Value array = runtime.getStack().popValue(false, true);
				
				runtime.addStackFrame(new FunctionValue(runtime.getScope(), 0));
				runtime.getScope().create(name);
				
				for(Value value:runtime.checkArrayValue(array).values()) {
					runtime.getScope().set(name, value);
					executeInstructions(runtime, instructions.subList(i + 1, instructions.size()), executedInstructions);
				}
				
				return;
			}
			
			if(instruction instanceof VizFilterInstruction) {
				Value condition = runtime.getStack().popValue(false, true);
				if(!runtime.checkBooleanValue(condition).getValue()) {
					return;
				}
			}

			instruction.execute(runtime);
		}
	}

	public List<VizObject> getVizObjects() {
		return vizObjects;
	}
}