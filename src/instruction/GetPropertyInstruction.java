package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import value.ObjectValue;
import value.StringValue;

public class GetPropertyInstruction implements Instruction {
	private StringValue name;
	
	public GetPropertyInstruction(StringValue name) {
		this.name = name;
	}
	
	public static Instruction GetProperty(StringValue name) {
		return new GetPropertyInstruction(name);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		ObjectValue object = runtime.popCheckedObjectValue();
		runtime.getStack().push(object.get(name));
	}
	
	public String toString() {
		return "GETPROPERTY: " + name;
	}
}