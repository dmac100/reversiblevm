package backend.instruction.variable;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.Scope;
import backend.value.Identifier;

public class LocalInstruction extends Instruction {
	private final Identifier identifier;

	public LocalInstruction(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public Instruction copy() {
		return new LocalInstruction(identifier);
	}
	
	public static Instruction Local(Identifier identifier) {
		return new LocalInstruction(identifier);
	}
	
	public void execute(Runtime runtime) {
		Scope scope = runtime.getScope();
		scope.create(identifier.getName());
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "LOCAL: " + identifier.getName();
	}
}