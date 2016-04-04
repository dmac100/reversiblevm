package backend.instruction.variable;

import backend.instruction.Instruction;
import backend.instruction.jump.LabeledJumpIfTrueInstruction;
import backend.runtime.Runtime;
import backend.runtime.Scope;
import backend.runtime.Stack;
import backend.value.Identifier;

public class StoreInstruction extends Instruction {
	private final Identifier identifier;

	public StoreInstruction(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public Instruction copy() {
		return new StoreInstruction(identifier);
	}
	
	public String getName() {
		return identifier.getName();
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public static Instruction Store(Identifier identifier) {
		return new StoreInstruction(identifier);
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Scope scope = runtime.getScope();
		scope.set(identifier.getName(), stack.popValue(false, true));
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "STORE: " + identifier.getName();
	}
}