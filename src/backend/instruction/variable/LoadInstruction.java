package backend.instruction.variable;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.Scope;
import backend.runtime.Stack;

public class LoadInstruction extends Instruction {
	private final String name;

	public LoadInstruction(String name) {
		this.name = name;
	}
	
	public Instruction copy() {
		return new LoadInstruction(name);
	}
	
	public String getName() {
		return name;
	}
	
	public static Instruction Load(String value) {
		return new LoadInstruction(value);
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Scope scope = runtime.getScope();
		stack.push(scope.get(name, runtime), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return super.toString() + "LOAD: " + name;
	}
}