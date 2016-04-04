package backend.instruction.variable;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.Scope;
import backend.runtime.Stack;
import backend.value.Identifier;

public class LoadInstruction extends Instruction {
	private final Identifier identifier;

	public LoadInstruction(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public Instruction copy() {
		return new LoadInstruction(identifier);
	}
	
	public String getName() {
		return identifier.getName();
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public static Instruction Load(Identifier identifier) {
		return new LoadInstruction(identifier);
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Scope scope = runtime.getScope();
		stack.push(scope.get(identifier.getName(), runtime), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return super.toString() + "LOAD: " + identifier.getName();
	}
}