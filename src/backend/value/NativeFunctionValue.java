package backend.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.runtime.UndoStack;

public abstract class NativeFunctionValue extends Value implements HasPropertiesObject {
	private final ObjectValue propertiesObject;
	
	public NativeFunctionValue(UndoStack undoStack) {
		propertiesObject = new ObjectValue(undoStack);
	}
	
	public String toString(Set<Value> used) {
		return "[NativeFunction]";
	}

	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		List<Value> params = getParams(runtime, stack);
		
		stack.push(execute(runtime, stack, params), true);
	}
	
	protected abstract Value execute(Runtime runtime, Stack stack, List<Value> params) throws ExecutionException;

	protected static List<Value> getParams(Runtime runtime, Stack stack) throws ExecutionException {
		List<Value> params = new ArrayList<>();
		int numParams = (int) runtime.checkDoubleValue(stack.popValue(true, false)).getValue();
		for(int x = 0; x < numParams; x++) {
			params.add(stack.popValue(true, false));
		}
		Collections.reverse(params);
		return params;
	}

	public ObjectValue getPropertiesObject() {
		return propertiesObject;
	}
}