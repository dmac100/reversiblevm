package instruction;

import runtime.Runtime;
import runtime.Scope;

public class LocalInstruction implements Instruction {
	private final String name;

	public LocalInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction Local(String value) {
		return new LocalInstruction(value);
	}
	
	public void execute(Runtime runtime) {
		Scope scope = runtime.getScope();
		scope.create(name);
	}
	
	public String toString() {
		return "LOCAL: " + name;
	}
}