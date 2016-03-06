package value;

import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import runtime.HasState;
import runtime.Scope;
import runtime.VizObject;
import runtime.VizObjectInstructions;

public class FunctionValue extends Value implements HasState {
	private final List<Instruction> instructions = new ArrayList<>();
	private final int paramCount;
	private final List<VizObjectInstructions> vizObjectInstructionsList = new ArrayList<>();
	
	private Scope parentScope;
	
	public FunctionValue(Scope parentScope, int paramCount, List<Instruction> instructions) {
		this.parentScope = parentScope;
		this.paramCount = paramCount;
		this.instructions.addAll(instructions);
	}
	
	public FunctionValue(Scope parentScope, int paramCount) {
		this.parentScope = parentScope;
		this.paramCount = paramCount;
	}
	
	public Scope getParentScope() {
		return parentScope;
	}
	
	public void addInstruction(Instruction instruction) {
		instructions.add(instruction);
	}
	
	public List<Instruction> getInstructions() {
		return instructions;
	}
	
	public int getParamCount() {
		return paramCount;
	}
	
	public void addVizObjectInstructions(VizObjectInstructions vizObjectInstructions) {
		vizObjectInstructionsList.add(vizObjectInstructions);
	}
	
	public void clearVizObjectInstructions() {
		for(VizObjectInstructions vizObjectInstructions:vizObjectInstructionsList) {
			vizObjectInstructions.clear();
		}
		vizObjectInstructionsList.clear();
	}
	
	public List<VizObject> getVizObjects() {
		List<VizObject> objects = new ArrayList<>();
		for(VizObjectInstructions vizObjectInstructions:vizObjectInstructionsList) {
			objects.addAll(vizObjectInstructions.getVizObjects());
		}
		return objects;
	}
	
	public String toString(Set<Value> used) {
		return "[Function]";
		//return "[" + instructions + "]";
	}
	
	public String getState(String prefix, Set<Object> used) {
		if(used.contains(this)) return prefix + "[CYCLIC]";
		used.add(this);
		
		StringBuilder s = new StringBuilder();
		s.append(prefix + "Instructions: " + instructions).append("\n");
		s.append(prefix + "ParentScope: ").append("\n");
		s.append(parentScope.getState(prefix + "  ", used));
		
		used.remove(this);
		
		return s.toString();
	}
}
