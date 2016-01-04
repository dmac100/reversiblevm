package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import value.ArrayValue;
import value.DoubleValue;

public class GetElementInstruction implements Instruction {
	public GetElementInstruction() {
	}
	
	public static Instruction GetElementInstruction() {
		return new GetElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		DoubleValue index = runtime.popCheckedDoubleValue();
		ArrayValue array = runtime.popCheckedArrayValue();
		runtime.getStack().push(array.get(index));
	}
	
	public String toString() {
		return "GETELEMENT";
	}
}