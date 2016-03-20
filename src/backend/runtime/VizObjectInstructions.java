package backend.runtime;

import java.util.ArrayList;
import java.util.List;

import backend.instruction.Instruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueChangeObserver;
import backend.observer.ValueReadObserver;

public class VizObjectInstructions implements ValueChangeObserver {
	private final Runtime runtime;
	private final List<Instruction> instructions;
	private final List<VizObject> vizObjects = new ArrayList<>();
	private final List<ValueChangeObservable> activeObservers = new ArrayList<>();
	
	private boolean dirty = true;
	
	public VizObjectInstructions(Runtime runtime, List<Instruction> instructions) {
		this.runtime = runtime;
		this.instructions = new ArrayList<>(instructions);
	}

	protected void updateObjects() {
		boolean undoStackEnabled = runtime.getUndoStack().isUndoEnabled();
		runtime.getUndoStack().setUndoEnabled(true);
		
		runtime.getCurrentVizObjects().clear();
		runtime.getCurrentVizObjectKey().clear();
		runtime.getCurrentVizObjectKey().add(this);
		
		executeInstructions(runtime);
		
		runtime.getUndoStack().setUndoEnabled(undoStackEnabled);
		
		vizObjects.clear();
		vizObjects.addAll(runtime.getCurrentVizObjects());
		runtime.getCurrentVizObjects().clear();
	}

	private void executeInstructions(final Runtime runtime) {
		clearObservers();
		
		final List<ValueChangeObservable> valueChangeObservables = new ArrayList<>();
		
		runtime.addValueReadObserver(new ValueReadObserver() {
			public void onValueRead(ValueChangeObservable valueChangeObservable) {
				valueChangeObservables.add(valueChangeObservable);
			}
		});
		
		try {
			runtime.runAndUndoInstructions(instructions);
		} finally {
			runtime.clearValueReadObservers();
		}
		
		for(ValueChangeObservable valueChangeObservable:valueChangeObservables) {
			valueChangeObservable.addObserver(this);
			activeObservers.add(valueChangeObservable);
		}
	}
	
	public void clearObservers() {
		for(ValueChangeObservable valueChangeObservable:activeObservers) {
			valueChangeObservable.removeObserver(this);
		}
		
		activeObservers.clear();
	}

	public List<VizObject> getVizObjects() {
		if(dirty) {
			updateObjects();
			dirty = false;
		}
		return vizObjects;
	}

	@Override
	public void onValueChanged() {
		dirty = true;
	}
}