package backend.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import backend.value.FunctionValue;

public class StackFrame implements HasState {
	private final FunctionValue function;
	private final Scope scope;
	private int instructionCounter = 0;
	private final List<VizObjectInstructions> vizObjectInstructionsList = new ArrayList<>();

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
	
	public void addVizObjectInstructions(VizObjectInstructions vizObjectInstructions) {
		vizObjectInstructionsList.add(vizObjectInstructions);
	}
	
	public void removeVizObjectInstructions(VizObjectInstructions vizObjectInstructions) {
		vizObjectInstructionsList.remove(vizObjectInstructions);
	}
	
	public void clearVizObjectInstructions() {
		for(VizObjectInstructions vizObjectInstructions:vizObjectInstructionsList) {
			vizObjectInstructions.clearObservers();
		}
	}
	
	public void updateVizObjects() {
		for(VizObjectInstructions vizObjectInstructions:vizObjectInstructionsList) {
			vizObjectInstructions.updateObjects();
		}
	}
	
	public List<VizObject> getVizObjects() {
		List<VizObject> objects = new ArrayList<>();
		for(VizObjectInstructions vizObjectInstructions:vizObjectInstructionsList) {
			objects.addAll(vizObjectInstructions.getVizObjects());
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