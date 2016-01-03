package runtime;

import value.FunctionValue;

public class StackFrame {
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
}