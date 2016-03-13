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
	private final Map<StartVizInstruction, VizObjectInstructions> vizObjectInstructionsList = new LinkedHashMap<>();

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
		vizObjectInstructionsList.put(startVizInstruction, vizObjectInstructions);
	}
	
	public boolean containsVizObjectInstructionsFor(StartVizInstruction startVizInstruction) {
		return vizObjectInstructionsList.containsKey(startVizInstruction);
	}
	
	public void removeVizObjectInstructions(StartVizInstruction startVizInstruction, VizObjectInstructions vizObjectInstructions) {
		vizObjectInstructionsList.remove(startVizInstruction);
	}
	
	public void clearVizObjectInstructions() {
		for(VizObjectInstructions vizObjectInstructions:vizObjectInstructionsList.values()) {
			vizObjectInstructions.clearObservers();
		}
	}
	
	public void updateVizObjects() {
		for(VizObjectInstructions vizObjectInstructions:vizObjectInstructionsList.values()) {
			vizObjectInstructions.onValueChanged();
		}
	}
	
	public List<VizObject> getVizObjects() {
		List<VizObject> objects = new ArrayList<>();
		for(VizObjectInstructions vizObjectInstructions:vizObjectInstructionsList.values()) {
			try {
				objects.addAll(vizObjectInstructions.getVizObjects());
			} catch(ExecutionException e) {
				System.err.println("Error getting viz object: " + e);
			}
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