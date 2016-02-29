package runtime;

import java.util.HashSet;
import java.util.Set;

import value.FunctionValue;

public class StackFrame implements HasState {
	private final FunctionValue function;
	private final Scope scope;
	private final UndoStack undoStack;
	private int instructionCounter = 0;

	public StackFrame(FunctionValue function, Scope scope, UndoStack undoStack) {
		this.function = function;
		this.scope = scope;
		this.undoStack = undoStack;
	}
	
	public int getInstructionCounter() {
		return instructionCounter;
	}
	
	public void setInstructionCounter(int instructionCounter) {
		final int oldInstructionCounter = this.instructionCounter;
		undoStack.add(new Runnable() {
			public void run() {
				StackFrame.this.instructionCounter = oldInstructionCounter;
			}
		});
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