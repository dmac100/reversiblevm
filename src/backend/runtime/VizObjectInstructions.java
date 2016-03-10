package backend.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import backend.instruction.Instruction;
import backend.instruction.viz.VizFilterInstruction;
import backend.instruction.viz.VizIterateInstruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueChangeObserver;
import backend.observer.ValueReadObserver;
import backend.value.ArrayValue;
import backend.value.BooleanValue;
import backend.value.FunctionValue;
import backend.value.Value;

public class VizObjectInstructions implements ValueChangeObserver {
	private final Runtime runtime;
	private final List<Instruction> instructions;
	private final List<VizObject> vizObjects = new ArrayList<>();
	private final List<ValueChangeObservable> activeObservers = new ArrayList<>();
	
	public VizObjectInstructions(Runtime runtime, List<Instruction> instructions) {
		this.runtime = runtime;
		this.instructions = new ArrayList<>(instructions);
	}

	public void updateObjects() {
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
		
		final List<ValueChangeObservable> valueChangeObservables = new ArrayList<>();
		
		runtime.addValueReadObserver(new ValueReadObserver() {
			public void onValueRead(ValueChangeObservable valueChangeObservable) {
				valueChangeObservables.add(valueChangeObservable);
			}
		});
		
		try {
			executeInstructions(runtime, instructions, executedInstructions);
		} catch(ExecutionException e) {
			runtime.getUndoStack().undoCommands();
			throw e;
		} finally {
			runtime.clearValueReadObservers();
		
			Collections.reverse(executedInstructions);
			for(Instruction instruction:executedInstructions) {
				instruction.undo(runtime);
				runtime.getUndoStack().undoCommands();
			}
		}
		
		for(ValueChangeObservable valueChangeObservable:valueChangeObservables) {
			valueChangeObservable.addObserver(this);
			activeObservers.add(valueChangeObservable);
		}
	}
	
	public void clear() {
		for(ValueChangeObservable valueChangeObservable:activeObservers) {
			valueChangeObservable.removeObserver(this);
		}
		
		activeObservers.clear();
		vizObjects.clear();
	}

	private void executeInstructions(Runtime runtime, List<Instruction> instructions, List<Instruction> executedInstructions) {
		for(int i = 0; i < instructions.size(); i++) {
			Instruction instruction = instructions.get(i);
			runtime.getUndoStack().saveUndoPoint();

			if(instruction instanceof VizIterateInstruction) {
				String name = ((VizIterateInstruction)instruction).getName();
				
				runtime.checkArrayValue(runtime.getStack().peekValue(0));
				ArrayValue array = runtime.checkArrayValue(runtime.getStack().popValue(false, true));
				
				runtime.addStackFrame(new FunctionValue(runtime.getScope(), 0, new ArrayList<Instruction>()));
				runtime.getScope().create(name);
				
				executedInstructions.add(instruction);
				
				for(Value value:array.values(runtime)) {
					runtime.getScope().set(name, value);
					executeInstructions(runtime, instructions.subList(i + 1, instructions.size()), executedInstructions);
				}
				
				return;
			}
			
			if(instruction instanceof VizFilterInstruction) {
				runtime.checkBooleanValue(runtime.getStack().peekValue(0));
				BooleanValue condition = runtime.checkBooleanValue(runtime.getStack().popValue(false, true));
				
				if(!condition.getValue()) {
					executedInstructions.add(instruction);
					return;
				}
			}

			instruction.execute(runtime);
			executedInstructions.add(instruction);
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