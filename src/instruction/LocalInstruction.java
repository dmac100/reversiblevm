package instruction;

import runtime.Scope;
import runtime.Runtime;
import runtime.Stack;
import value.StringValue;

public class LocalInstruction implements Instruction {
	private StringValue name;

	public LocalInstruction(StringValue name) {
		this.name = name;
	}
	
	public static Instruction Local(StringValue value) {
		return new LocalInstruction(value);
	}
	
	public void execute(Runtime runtime) {
		Scope scope = runtime.getScope();
		scope.create(name.getValue());
	}
	
	public String toString() {
		return "LOCAL: " + name;
	}
}