package runtime;

import instruction.Instruction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import value.ArrayValue;
import value.BooleanValue;
import value.DoubleValue;
import value.FunctionValue;
import value.ObjectValue;
import value.StringValue;
import value.Value;

public class Runtime implements HasState {
	private UndoStack undoStack = new UndoStack();
	
	private Stack stack = new Stack(undoStack);
	private List<StackFrame> stackFrames = new ArrayList<>();
	private FunctionValue currentFunctionDefinition = null;
	private int nestedFunctionDefinitionCount = 0;
	private List<VizObject> currentVizObjects = new ArrayList<>();
	private boolean inVizInstruction = false;
	private List<Instruction> vizInstructions = new ArrayList<>();
	
	private List<String> errors = new ArrayList<>();
	private List<String> output = new ArrayList<>();
	
	public void addStackFrame(FunctionValue function) {
		NonGlobalScope scope = new NonGlobalScope(function.getParentScope(), undoStack);
		addStackFrame(new StackFrame(function, scope, undoStack), true);
	}
	
	public void addStackFrame(StackFrame frame, boolean addToUndoStack) {
		stackFrames.add(frame);
		
		if(addToUndoStack) {
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					stackFrames.remove(stackFrames.size() - 1);
				}
			});
		}
	}
	
	public StackFrame getCurrentStackFrame() {
		return stackFrames.isEmpty() ? null : stackFrames.get(stackFrames.size() - 1);
	}
	
	public StackFrame popStackFrame() {
		if(stackFrames.isEmpty()) return null;
		undoStack.addInstructionCounterUndo(UndoStack.POPSTACKFRAME);
		undoStack.addPopStackFrameUndo(stackFrames.get(stackFrames.size() - 1));
		return stackFrames.remove(stackFrames.size() - 1);
	}
	
	public Scope getScope() {
		return getCurrentStackFrame().getScope();
	}
	
	public Scope getGlobalScope() {
		return stackFrames.get(0).getScope();
	}
	
	public List<VizObject> getVizObjects() {
		List<VizObject> vizObjects = new ArrayList<>();
		for(StackFrame stackFrame:stackFrames) {
			vizObjects.addAll(stackFrame.getFunction().getVizObjects());
		}
		return vizObjects;
	}
	
	public void throwError(String error) {
		System.err.println("Error: " + error);
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				errors.remove(output.size() - 1);
			}
		});
		errors.add(error);
	}
	
	public void print(String value) {
		System.out.println(value);
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				output.remove(output.size() - 1);
			}
		});
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
	
	public UndoStack getUndoStack() {
		return undoStack;
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
	
	public boolean isInVizInstruction() {
		return inVizInstruction;
	}
	
	public List<VizObject> getCurrentVizObjects() {
		return currentVizObjects;
	}
	
	public void setInVizInstruction(boolean inVizInstruction) {
		this.inVizInstruction = inVizInstruction;
	}
	
	public List<Instruction> getCurrentVizInstructions() {
		return vizInstructions;
	}

	public DoubleValue checkDoubleValue(Value value) throws ExecutionException {
		if(value instanceof DoubleValue) {
			return (DoubleValue) value;
		} else {
			throw new ExecutionException("TypeError: Not a double: " + value);
		}
	}

	public BooleanValue checkBooleanValue(Value value) throws ExecutionException {
		if(value instanceof BooleanValue) {
			return (BooleanValue) value;
		} else {
			throw new ExecutionException("TypeError: Not a boolean: " + value);
		}
	}

	public ObjectValue checkObjectValue(Value value) throws ExecutionException {
		if(value instanceof ObjectValue) {
			return (ObjectValue) value;
		} else {
			throw new ExecutionException("TypeError: Not an object: " + value);
		}
	}

	public ArrayValue checkArrayValue(Value value) throws ExecutionException {
		if(value instanceof ArrayValue) {
			return (ArrayValue) value;
		} else {
			throw new ExecutionException("TypeError: Not an array: " + value);
		}
	}

	public FunctionValue checkFunctionValue(Value value) throws ExecutionException {
		if(value instanceof FunctionValue) {
			return (FunctionValue) value;
		} else {
			throw new ExecutionException("TypeError: Not a function: " + value);
		}
	}

	public StringValue checkStringValue(Value value) throws ExecutionException {
		if(value instanceof StringValue) {
			return (StringValue) value;
		} else {
			throw new ExecutionException("TypeError: Not a string: " + value);
		}
	}
	
	public String getState() {
		return getState("", new HashSet<>());
	}
	
	public String getState(String prefix, Set<Object> used) {
		StringBuilder s = new StringBuilder();
		
		s.append("Runtime:").append("\n");
		s.append("  Stack: ").append("\n");
		s.append(stack.getState("    ", used));
		s.append("  StackFrames:").append("\n");
		for(StackFrame stackFrame:stackFrames) {
			s.append("    StackFrame:").append("\n");
			s.append(stackFrame.getState("      ", used)).append("\n");
		}
		if(currentFunctionDefinition != null) {
			s.append("  CurrentFunction: ").append("\n");
			s.append("  " + currentFunctionDefinition.getState("  ", new HashSet<>())).append("\n");
		}
		s.append("  NestedFunctionDefinitionCount: " + nestedFunctionDefinitionCount).append("\n");
		s.append("  InVizInstruction: " + inVizInstruction).append("\n");
		s.append("  Errors: " + errors).append("\n");
		s.append("  Output: " + output);
		
		return s.toString();
	}
}