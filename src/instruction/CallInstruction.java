package instruction;

import java.util.ArrayList;
import java.util.List;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.NativeFunctionValue;
import value.Value;

public class CallInstruction implements Instruction {
	public CallInstruction() {
	}
	
	public static Instruction Call() {
		return new CallInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		
		List<Value> params = new ArrayList<>();
		int numParams = (int) stack.popDoubleValue().getValue();
		for(int x = 0; x < numParams; x++) {
			params.add(stack.popValue());
		}
		
		NativeFunctionValue function = runtime.popCheckedFunctionValue();
		function.execute(runtime, params);
	}
	
	public String toString() {
		return "CALL";
	}
}