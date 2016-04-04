package backend.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import backend.instruction.viz.StartVizInstruction;
import backend.value.FunctionValue;
import backend.value.Identifier;
import backend.value.Value;

import com.google.common.base.Supplier;

public class StackFrame implements HasState {
	private final FunctionValue function;
	private final Scope scope;
	private int instructionCounter = 0;
	private final Map<StartVizInstruction, VizObjectInstructions> vizObjectInstructionsList = new LinkedHashMap<>();
	private final Map<Identifier, Supplier<Value>> valuesByIdentifier = new HashMap<>();

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
	
	public Map<StartVizInstruction, VizObjectInstructions> getVizObjectInstructions() {
		return Collections.unmodifiableMap(vizObjectInstructionsList);
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
	
	public List<VizObject> getVizObjects(boolean dirty) {
		List<VizObject> objects = new ArrayList<>();
		for(VizObjectInstructions vizObjectInstructions:vizObjectInstructionsList.values()) {
			if(dirty) {
				vizObjectInstructions.onValueChanged();
			}
			objects.addAll(vizObjectInstructions.getVizObjects());
		}
		return objects;
	}
	
	public void setIdentifierValue(Identifier identifier, Supplier<Value> valueSupplier) {
		valuesByIdentifier.put(identifier, valueSupplier);
	}
	
	public void clearIdentifierValues() {
		valuesByIdentifier.clear();
	}
	
	public Value getValueAt(int lineNumber, int columnNumber) {
		for(Identifier identifier:valuesByIdentifier.keySet()) {
			if(identifier.getLineNumber() == lineNumber) {
				int columnStart = identifier.getColumnNumber();
				int columnEnd = identifier.getColumnNumber() + identifier.getName().length();
				if(columnNumber >= columnStart && columnNumber <= columnEnd) {
					return valuesByIdentifier.get(identifier).get();
				}
			}
		}
		return null;
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