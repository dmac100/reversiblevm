package backend.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backend.instruction.Instruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueChangeObserver;
import backend.observer.ValueReadObserver;
import backend.value.ImmutableValue;

public class VizObjectInstructions implements ValueChangeObserver {
	private final Runtime runtime;
	private final List<Instruction> instructions;
	private final List<VizObject> vizObjects = new ArrayList<>();
	private final List<ValueChangeObservable> activeObservers = new ArrayList<>();
	
	private List<VizObject> vizObjectFilters = new ArrayList<>();
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
		
		executeInstructions(runtime);
		
		runtime.getUndoStack().setUndoEnabled(undoStackEnabled);
		
		vizObjects.clear();
		vizObjects.addAll(runtime.getCurrentVizObjects());
		runtime.getCurrentVizObjects().clear();
		
		// Recreate all viz objects if we've created any filters with different values than last time.
		List<VizObject> lastVizObjectFilters = vizObjectFilters;
		vizObjectFilters = getVizObjectFilters(vizObjects);
		if(changed(lastVizObjectFilters, vizObjectFilters)) {
			runtime.markVizObjectsDirty();
		}
	}

	/**
	 * Returns whether the filters or values between vizObjects1 and vizObjects2 have changed.
	 */
	private boolean changed(List<VizObject> vizObjects1, List<VizObject> vizObjects2) {
		if(vizObjects1.size() != vizObjects2.size()) return true;
		for(int i = 0; i < vizObjects1.size(); i++) {
			VizObject vizObject1 = vizObjects1.get(i);
			VizObject vizObject2 = vizObjects2.get(i);
			if(changed(vizObject1.getValues(), vizObject2.getValues())) {
				return true;
			}
			if(changed(vizObject1.getFilters(), vizObject2.getFilters())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether the values between values1 and values2 have changed.
	 */
	private static boolean changed(Map<String, ImmutableValue> values1, Map<String, ImmutableValue> values2) {
		if(!values1.keySet().equals(values2.keySet())) return true;
		for(String key:values1.keySet()) {
			if(!values1.get(key).getKey().equals(values2.get(key).getKey())) {
				return true;
			}
		}
		return false;
	}

	private static List<VizObject> getVizObjectFilters(List<VizObject> vizObjects) {
		List<VizObject> vizObjectFilters = new ArrayList<>();
		for(VizObject vizObject:vizObjects) {
			if(vizObject.isFilterEnabled()) {
				vizObjectFilters.add(vizObject);
			}
		}
		return vizObjectFilters;
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