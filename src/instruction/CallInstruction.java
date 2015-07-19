package instruction;

import runtime.ExecutionException;
import runtime.NonGlobalScope;
import runtime.Runtime;
import runtime.StackFrame;
import value.FunctionValue;
import value.NativeFunctionValue;
import value.Value;

public class CallInstruction implements Instruction {
	public CallInstruction() {
	}
	
	public static Instruction Call() {
		return new CallInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Value value = runtime.getStack().popValue();
		if(value instanceof NativeFunctionValue) {
			((NativeFunctionValue)value).execute(runtime);
		} else if(value instanceof FunctionValue) {
			FunctionValue function = (FunctionValue) value;
			runtime.addStackFrame(new StackFrame(function));
		} else {
			throw new ExecutionException("TypeError: Not a function");
		}
	}
	
	public String toString() {
		return "CALL";
	}
}