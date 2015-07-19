package instruction;

import runtime.Scope;
import runtime.Runtime;
import runtime.Stack;
import value.StringValue;

public class StoreInstruction implements Instruction {
	private StringValue name;

	public StoreInstruction(StringValue name) {
		this.name = name;
	}
	
	public static Instruction Store(StringValue value) {
		return new StoreInstruction(value);
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Scope scope = runtime.getScope();
		scope.set(name.getValue(), stack.popValue());
	}
	
	public String toString() {
		return "STORE: " + name;
	}
}