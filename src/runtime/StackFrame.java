package runtime;

import java.util.HashSet;
import java.util.Set;

import value.FunctionValue;

public class StackFrame implements HasState {
	private final FunctionValue function;
	private final Scope scope;
	private int instructionCounter = 0;

	public StackFrame(FunctionValue function, Scope scope) {
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