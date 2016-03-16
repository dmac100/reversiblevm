package backend.instruction.function;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.value.DoubleValue;
import backend.value.FunctionValue;
import backend.value.NativeFunctionValue;
import backend.value.NullValue;
import backend.value.Value;

public class CallInstruction extends Instruction {
	public CallInstruction() {
	}
	
	public static Instruction Call() {
		return new CallInstruction();
	}
	
	public Instruction copy() {
		return new CallInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Value value = runtime.getStack().popValue(true, false);
		if(value instanceof NativeFunctionValue) {
			((NativeFunctionValue)value).execute(runtime);
		} else if(value instanceof FunctionValue) {
			FunctionValue function = (FunctionValue) value;
			fixParamCount(runtime, function);
			runtime.addStackFrame(function);
		} else {
			throw new ExecutionException("TypeError: Not a function: " + value);
		}
	}

	/**
	 * If too few params are passed, add null values. If too many, remove these values.
	 */
	private void fixParamCount(Runtime runtime, FunctionValue function) throws ExecutionException {
		int passedParams = (int) runtime.checkDoubleValue(runtime.getStack().popValue(true, false)).getValue();
		int functionParams = function.getParamCount();
		
		for(int x = passedParams; x < functionParams; x++) {
			runtime.getStack().push(new NullValue(), true);
		}
		
		for(int x = functionParams; x < passedParams; x++) {
			runtime.getStack().popValue(true, false);
		}
		
		runtime.getStack().push(new DoubleValue(passedParams), true);
	}
	
	public void undo(Runtime runtime) {
	}

	public String toString() {
		return super.toString() + "CALL";
	}
}