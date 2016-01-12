package value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;

public abstract class NativeFunctionValue implements Value {
	public NativeFunctionValue() {
	}
	
	public String toString() {
		return "[NativeFunction]";
	}

	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		List<Value> params = getParams(runtime, stack);
		
		stack.push(execute(runtime, stack, params));
	}
	
	protected abstract Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException;

	private static List<Value> getParams(Runtime runtime, Stack stack) throws ExecutionException {
		List<Value> params = new ArrayList<>();
		int numParams = (int) runtime.checkDoubleValue(stack.popValue()).getValue();
		for(int x = 0; x < numParams; x++) {
			params.add(stack.popValue());
		}
		Collections.reverse(params);
		return params;
	}
}