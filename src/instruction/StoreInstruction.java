package instruction;

import runtime.Runtime;
import runtime.Scope;
import runtime.Stack;
import value.StringValue;

public class StoreInstruction implements Instruction {
	private StringValue name;

	public StoreInstruction(StringValue name) {
		this.name = name;
	}
	
	public String getName() {
		return name.getValue();
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