package value;

import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

import runtime.NonGlobalScope;
import runtime.Scope;

public class FunctionValue implements Value {
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
	
	public String toString() {
		return "[Function]";
		//return "[" + instructions + "]";
	}
}
