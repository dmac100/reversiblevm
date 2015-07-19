package value;

import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

import runtime.NonGlobalScope;
import runtime.Scope;

public class FunctionValue implements Value {
	private final Scope scope;
	private final List<Instruction> instructions = new ArrayList<>();
	
	public FunctionValue(Scope parentScope) {
		scope = new NonGlobalScope(parentScope);
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public void addInstruction(Instruction instruction) {
		instructions.add(instruction);
	}
	
	public List<Instruction> getInstructions() {
		return instructions;
	}
	
	public String toString() {
		return "[Function]";
	}
}
