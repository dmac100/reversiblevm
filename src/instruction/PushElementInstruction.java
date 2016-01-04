package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import value.ArrayValue;
import value.Value;

public class PushElementInstruction implements Instruction {
	public PushElementInstruction() {
	}
	
	public static Instruction PushElement() {
		return new PushElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Value value = runtime.getStack().popValue();
		ArrayValue array = runtime.popCheckedArrayValue();
		array.push(value);
	}
	
	public String toString() {
		return "PUSHELEMENT";
	}
}