package runtime;

import instruction.Instruction;
import instruction.viz.VizFilterInstruction;
import instruction.viz.VizIterateInstruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import callback.HasCallbacks;
import callback.ValueChangeCallback;
import callback.ValueReadCallback;

import value.FunctionValue;
import value.Value;

public class VizObjectInstructions implements ValueChangeCallback {
	private final Runtime runtime;
	private final List<Instruction> instructions;
	private final List<VizObject> vizObjects = new ArrayList<>();
	private final List<HasCallbacks<ValueChangeCallback>> activeCallbacks = new ArrayList<>();
	
	public VizObjectInstructions(Runtime runtime, List<Instruction> instructions) {
		this.runtime = runtime;
		this.instructions = new ArrayList<>(instructions);
	}

	public void updateObjects() {
		System.out.println("*** UPDATE OBJECTS ***");
		
		boolean undoStackEnabled = runtime.getUndoStack().isUndoEnabled();
		runtime.getUndoStack().setUndoEnabled(true);
		
		runtime.getCurrentVizObjects().clear();
		
		executeInstructions(runtime);
		
		runtime.getUndoStack().setUndoEnabled(undoStackEnabled);
		
		vizObjects.addAll(runtime.getCurrentVizObjects());
		runtime.getCurrentVizObjects().clear();
	}

	private void executeInstructions(final Runtime runtime) {
		List<Instruction> executedInstructions = new ArrayList<>();
		
		clear();
		
		final List<HasCallbacks<ValueChangeCallback>> valueChangeCallbacks = new ArrayList<>();
		
		runtime.addValueReadCallback(new ValueReadCallback() {
			public void onValueRead(HasCallbacks<ValueChangeCallback> hasValueChangeCallbacks) {
				valueChangeCallbacks.add(hasValueChangeCallbacks);
			}
		});
		
		executeInstructions(runtime, instructions, executedInstructions);
		
		runtime.clearValueReadCallbacks();
		
		Collections.reverse(executedInstructions);
		for(Instruction instruction:executedInstructions) {
			instruction.undo(runtime);
			runtime.getUndoStack().undoCommands();
		}
		
		for(HasCallbacks<ValueChangeCallback> hasValueChangeCallbacks:valueChangeCallbacks) {
			updateObjectsOnValueChange(hasValueChangeCallbacks);
		}
	}
	
	public void clear() {
		for(HasCallbacks<ValueChangeCallback> callback:activeCallbacks) {
			callback.removeCallback(this);
		}
		
		activeCallbacks.clear();
		vizObjects.clear();
	}

	private void updateObjectsOnValueChange(final HasCallbacks<ValueChangeCallback> hasValueChangeCallbacks) {
		hasValueChangeCallbacks.addCallback(this);
		activeCallbacks.add(hasValueChangeCallbacks);
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
				
				for(Value value:runtime.checkArrayValue(array).values(runtime)) {
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

	@Override
	public void onValueChanged() {
		updateObjects();
	}
}