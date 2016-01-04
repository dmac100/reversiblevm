package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import value.ArrayValue;
import value.DoubleValue;
import value.Value;

public class SetElementInstruction implements Instruction {
	public SetElementInstruction() {
	}
	
	public static Instruction GetElementInstruction() {
		return new SetElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Value value = runtime.getStack().popValue();
		DoubleValue index = runtime.popCheckedDoubleValue();
		ArrayValue array = runtime.popCheckedArrayValue();
		array.set(index, value);
	}
	
	public String toString() {
		return "SETELEMENT";
	}
}