package backend.instruction.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Scope;
import backend.value.ArrayValue;
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
			runtime.addStackFrame(function);
			addArgumentsVariable(runtime);
			fixParamCount(runtime, function);
		} else {
			throw new ExecutionException("TypeError: Not a function: " + value);
		}
	}

	/**
	 * Adds an arguments variable to the current scope containing the arguments to the function (excluding this).
	 */
	private void addArgumentsVariable(Runtime runtime) {
		int length = (int) runtime.checkDoubleValue(runtime.getStack().peekValue(0)).getValue() - 1;
		
		List<Value> values = new ArrayList<>();
		for(int x = 0; x < length; x++) {
			Value value = runtime.getStack().peekValue(x + 1);
			values.add(value);
		}
		
		Collections.reverse(values);
		
		Scope scope = runtime.getCurrentStackFrame().getScope();
		scope.create("arguments");
		scope.set("arguments", new ArrayValue(values, runtime.getUndoStack()));
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