package instruction.variable;

import instruction.Instruction;
import runtime.Runtime;
import runtime.Scope;
import runtime.Stack;

public class StoreInstruction implements Instruction {
	private final String name;

	public StoreInstruction(String name) {
		this.name = name;
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