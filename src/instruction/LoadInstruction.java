package instruction;

import runtime.Runtime;
import runtime.Scope;
import runtime.Stack;
import value.StringValue;

public class LoadInstruction implements Instruction {
	private StringValue name;

	public LoadInstruction(StringValue name) {
		this.name = name;
	}
	
	public String getName() {
		return name.getValue();
	}
	
	public static Instruction Load(StringValue value) {
		return new LoadInstruction(value);
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Scope scope = runtime.getScope();
		stack.push(scope.get(name.getValue()));
	}
	
	public String toString() {
		return "LOAD: " + name;
	}
}