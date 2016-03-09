package backend.instruction.variable;

import backend.instruction.Instruction;
import backend.instruction.jump.LabeledJumpIfTrueInstruction;
import backend.runtime.Runtime;
import backend.runtime.Scope;
import backend.runtime.Stack;

public class StoreInstruction extends Instruction {
	private final String name;

	public StoreInstruction(String name) {
		this.name = name;
	}
	
	public Instruction copy() {
		return new StoreInstruction(name);
	}
	
	public String getName() {
		return name;
	}
	
	public static Instruction Store(String value) {
		return new StoreInstruction(value);
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Scope scope = runtime.getScope();
		scope.set(name, stack.popValue(false, true));
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "STORE: " + name;
	}
}