package runtime;

import value.FunctionValue;

public class StackFrame {
	private final FunctionValue function;
	private int instructionCounter = 0;

	public StackFrame(FunctionValue function) {
		this.function = function;
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
}