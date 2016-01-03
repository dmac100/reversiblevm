package runtime;

import java.util.ArrayList;
import java.util.List;

import value.BooleanValue;
import value.DoubleValue;
import value.FunctionValue;
import value.Value;

public class Runtime {
	private Stack stack = new Stack();
	private List<StackFrame> stackFrames = new ArrayList<>();
	private FunctionValue currentFunctionDefinition = null;
	private int nestedFunctionDefinitionCount = 0;
	
	private List<String> errors = new ArrayList<>();
	private List<String> output = new ArrayList<>();
	
	public Runtime() {
		Scope parentScope = new GlobalScope();
		addStackFrame(new FunctionValue(parentScope), parentScope);
	}
	
	public void addStackFrame(FunctionValue function) {
		addStackFrame(function, getScope());
	}
	
	private void addStackFrame(FunctionValue function, Scope parentScope) {
		stackFrames.add(new StackFrame(function, new NonGlobalScope(parentScope)));
	}
	
	public StackFrame getCurrentStackFrame() {
		return stackFrames.isEmpty() ? null : stackFrames.get(stackFrames.size() - 1);
	}
	
	public StackFrame popStackFrame() {
		return stackFrames.isEmpty() ? null : stackFrames.remove(stackFrames.size() - 1);
	}
	
	public Scope getScope() {
		return getCurrentStackFrame().getScope();
	}
	
	public void throwError(String error) {
		System.err.println("Error: " + error);
		errors.add(error);
	}
	
	public void print(String value) {
		System.out.println(value);
		output.add(value);
	}

	public Stack getStack() {
		return stack;
	}

	public List<String> getOutput() {
		return output;
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public FunctionValue getCurrentFunctionDefinition() {
		return currentFunctionDefinition;
	}

	public void setCurrentFunctionDefinition(FunctionValue currentFunctionDefinition) {
		this.currentFunctionDefinition = currentFunctionDefinition;
	}

	public int getNestedFunctionDefinitionCount() {
		return nestedFunctionDefinitionCount;
	}

	public void setNestedFunctionDefinitionCount(int nestedFunctionDefinitionCount) {
		this.nestedFunctionDefinitionCount = nestedFunctionDefinitionCount;
	}

	public DoubleValue popCheckedDoubleValue() throws ExecutionException {
		Value value = stack.popValue();
		if(value instanceof DoubleValue) {
			return (DoubleValue) value;
		} else {
			throw new ExecutionException("TypeError: Not a double: " + value);
		}
	}

	public BooleanValue popCheckedBooleanValue() throws ExecutionException {
		Value value = stack.popValue();
		if(value instanceof BooleanValue) {
			return (BooleanValue) value;
		} else {
			throw new ExecutionException("TypeError: Not a boolean: " + value);
		}
	}
}