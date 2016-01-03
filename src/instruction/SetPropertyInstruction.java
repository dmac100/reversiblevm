package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import value.ObjectValue;
import value.StringValue;
import value.Value;

public class SetPropertyInstruction implements Instruction {
	private StringValue name;
	
	public SetPropertyInstruction(StringValue name) {
		this.name = name;
	}
	
	public static Instruction SetProperty(StringValue name) {
		return new SetPropertyInstruction(name);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Value value = runtime.getStack().popValue();
		ObjectValue object = runtime.popCheckedObjectValue();
		object.set(name, value);
	}
	
	public String toString() {
		return "SETPROPERTY: " + name;
	}
}