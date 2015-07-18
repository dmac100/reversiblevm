package instruction;

import java.util.ArrayList;
import java.util.List;

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
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		
		List<Value> params = new ArrayList<>();
		int numParams = (int) stack.popDoubleValue().getValue();
		for(int x = 0; x < numParams; x++) {
			params.add(stack.popValue());
		}
		
		Value function = stack.popValue();
		if(function instanceof NativeFunctionValue) {
			NativeFunctionValue functionValue = (NativeFunctionValue) function;
			functionValue.execute(runtime, params);
		} else {
			runtime.throwError("Not a function value: " + function);
		}
	}
	
	public String toString() {
		return "CALL";
	}
}