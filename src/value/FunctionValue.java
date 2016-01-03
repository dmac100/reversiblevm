package value;

import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

import runtime.NonGlobalScope;
import runtime.Scope;

public class FunctionValue implements Value {
	private final List<Instruction> instructions = new ArrayList<>();
	
	private Scope parentScope;
	
	public FunctionValue(Scope parentScope) {
		this.parentScope = parentScope;
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
	
	public String toString() {
		return "[Function]";
		//return "[" + instructions + "]";
	}
}
