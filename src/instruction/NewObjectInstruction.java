package instruction;

import runtime.Runtime;
import value.ObjectValue;

public class NewObjectInstruction implements Instruction {
	public NewObjectInstruction() {
	}
	
	public static Instruction NewObject() {
		return new NewObjectInstruction();
	}
	
	public void execute(Runtime runtime) {
		runtime.getStack().push(new ObjectValue());
	}
	
	public String toString() {
		return "NEWOBJECT";
	}
}