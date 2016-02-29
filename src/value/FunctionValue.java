package value;

import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import runtime.HasState;
import runtime.NonGlobalScope;
import runtime.Scope;

public class FunctionValue extends Value implements HasState {
	private final List<Instruction> instructions = new ArrayList<>();
	private final int paramCount;
	
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
