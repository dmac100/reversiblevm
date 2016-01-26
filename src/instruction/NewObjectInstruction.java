package instruction;

import runtime.Runtime;
import value.ObjectValue;
import value.StringValue;

public class NewObjectInstruction implements Instruction {
	public NewObjectInstruction() {
	}
	
	public static Instruction NewObject() {
		return new NewObjectInstruction();
	}
	
	public void execute(Runtime runtime) {
		ObjectValue value = new ObjectValue();
		value.set(new StringValue("prototype"), runtime.getGlobalScope().get("ObjectProto"));
		runtime.getStack().push(value);
	}
	
	public String toString() {
		return "NEWOBJECT";
	}
}