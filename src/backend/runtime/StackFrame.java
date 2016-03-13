package backend.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import backend.instruction.viz.StartVizInstruction;
import backend.value.FunctionValue;

public class StackFrame implements HasState {
	private final FunctionValue function;
	private final Scope scope;
	private int instructionCounter = 0;
	private final Map<StartVizInstruction, List<VizObjectInstructions>> vizObjectInstructionsList = new LinkedHashMap<>();

	public StackFrame(FunctionValue function, Scope scope, UndoStack undoStack) {
		this.function = function;
		this.scope = scope;
	}
	
	public int getInstructionCounter() {
		return instructionCounter;
	}
	
	public void setInstructionCounter(int instructionCounter) {
		this.instructionCounter = instructionCounter;
	}
	
	public FunctionValue getFunction() {
		return function;
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public void addVizObjectInstructions(StartVizInstruction startVizInstruction, VizObjectInstructions vizObjectInstructions) {
		if(!vizObjectInstructionsList.containsKey(startVizInstruction)) {
			vizObjectInstructionsList.put(startVizInstruction, new ArrayList<VizObjectInstructions>());
		}
		vizObjectInstructionsList.get(startVizInstruction).add(vizObjectInstructions);
	}
	
	public void removeVizObjectInstructions(StartVizInstruction startVizInstruction, VizObjectInstructions vizObjectInstructions) {
		List<VizObjectInstructions> list = vizObjectInstructionsList.get(startVizInstruction);
		list.remove(list.size() - 1);
		if(list.isEmpty()) {
			vizObjectInstructionsList.remove(startVizInstruction);
		}
	}
	
	public void clearVizObjectInstructions() {
		for(List<VizObjectInstructions> vizObjectInstructions:vizObjectInstructionsList.values()) {
			vizObjectInstructions.get(0).clearObservers();
		}
	}
	
	public void updateVizObjects() {
		for(List<VizObjectInstructions> vizObjectInstructions:vizObjectInstructionsList.values()) {
			vizObjectInstructions.get(0).onValueChanged();
		}
	}
	
	public List<VizObject> getVizObjects() {
		List<VizObject> objects = new ArrayList<>();
		for(List<VizObjectInstructions> vizObjectInstructions:vizObjectInstructionsList.values()) {
			objects.addAll(vizObjectInstructions.get(0).getVizObjects());
		}
		return objects;
	}

	public String getState(String prefix, Set<Object> used) {
		StringBuilder s = new StringBuilder();
		s.append(prefix + "InstructionCounter: " + getInstructionCounter()).append("\n");
		s.append(prefix + "Function:").append("\n");
		s.append(getFunction().getState(prefix + "  ", new HashSet<>())).append("\n");
		s.append(prefix + "Scope:").append("\n");
		s.append(getScope().getState(prefix + "  ", new HashSet<>()));
		return s.toString();
	}
}