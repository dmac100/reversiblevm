package backend.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import backend.instruction.Instruction;
import backend.instruction.viz.StartVizInstruction;
import backend.instruction.viz.VizFilterInstruction;
import backend.instruction.viz.VizIterateInstruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueReadObserver;
import backend.runtime.OutputLine.OutputType;
import backend.value.ArrayValue;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.FunctionValue;
import backend.value.ObjectValue;
import backend.value.StringValue;
import backend.value.Value;

public class Runtime implements HasState, ValueReadObserver {
	private static final int MAX_OUTPUT_LENGTH = 10000;
	
	private UndoStack undoStack = new UndoStack();
	
	private Stack stack = new Stack(undoStack);
	private List<StackFrame> stackFrames = new ArrayList<>();
	private int nestedFunctionDefinitionCount = 0;
	private List<VizObject> vizObjects = new ArrayList<>();
	private List<VizObject> currentVizObjects = new ArrayList<>();
	private List<Object> currentVizObjectKey = new ArrayList<>();
	private Set<ValueReadObserver> valueReadObservers = new HashSet<>();
	private boolean vizUpdatesEnabled = true;
	private boolean vizObjectsDirty = false;
	
	private List<OutputLine> output = new ArrayList<>();
	
	/**
	 * Evaluates an expression then prints the result.
	 */
	public void evalAndPrintExpression(String infoMessage, List<Instruction> instructions) {
		String result = evalExpression(infoMessage, instructions);
		info(result);
	}
	
	/**
	 * Runs instructions at the current position in the runtime, and returns the last evaluated value.
	 */
	public String evalExpression(String infoMessage, List<Instruction> instructions) {
		// Get current stack frame.
		final StackFrame parentStackFrame = getCurrentStackFrame();
		
		undoStack.saveUndoPoint(parentStackFrame.getInstructionCounter());
		
		if(infoMessage != null) {
			info(infoMessage);
		}
		
		stack.resetLastPoppedValue();
		
		// Add new function with same scope as current function with instructions to execute.
		FunctionValue function = new FunctionValue(parentStackFrame.getScope(), undoStack, 0, instructions);
		StackFrame stackFrame = new StackFrame(function, parentStackFrame.getScope(), undoStack);
		addStackFrame(stackFrame, true);
		
		int stackFrameCount = stackFrames.size();
		
		while(stackFrames.size() >= stackFrameCount) {
			runNextInstruction();
		}
		
		// Copy and viz object instructions from new function into current function.
		Map<StartVizInstruction, VizObjectInstructions> vizObjectInstructionsList = stackFrame.getVizObjectInstructions();
		for(final StartVizInstruction startVizInstruction:vizObjectInstructionsList.keySet()) {
			final VizObjectInstructions vizObjectInstructions = vizObjectInstructionsList.get(startVizInstruction);
			parentStackFrame.addVizObjectInstructions(startVizInstruction, vizObjectInstructions);
			
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					parentStackFrame.removeVizObjectInstructions(startVizInstruction, vizObjectInstructions);
					parentStackFrame.updateVizObjects();
				}
				
				public String toString() {
					return "COPY VIZ OBJECT";
				}
			});
		}
		
		return stack.getLastPoppedValue().inspect();
	}
	
	/**
	 * Runs and then undoes all the given instructions within a new stack frame,
	 * for the purposes of getting the visual objects for a block of instructions.
	 */
	public void runAndUndoInstructions(Scope scope, List<Instruction> instructions) {
		undoStack.saveUndoPoint(getCurrentStackFrame().getInstructionCounter());
		addStackFrame(new FunctionValue(scope, undoStack, 0, instructions));
		
		int stackFrameCount = stackFrames.size();
		
		try {
			while(stackFrames.size() >= stackFrameCount) {
				runNextInstruction();
			}
		} finally {
			while(getInstruction() != instructions.get(0)) {
				undoStack.undo(this);
			}
			undoStack.undo(this);
		}
	}
	
	/**
	 * Runs the next instruction and advances the instruction counter to the
	 * instruction to execute after that.
	 */
	public void runNextInstruction() {
		StackFrame frame = getCurrentStackFrame();
		FunctionValue function = frame.getFunction();
		
		undoStack.saveUndoPoint(frame.getInstructionCounter());
		
		// Return from current function if we've reached the end of the instruction list.
		if(frame.getInstructionCounter() >= function.getInstructions().size()) {
			popStackFrame();
			return;
		}
		
		Instruction instruction = function.getInstructions().get(frame.getInstructionCounter());
		
		// Run any viz iterate instructions by running and undoing the remaining instructions for each value in the array.
		if(instruction instanceof VizIterateInstruction) {
			String name = ((VizIterateInstruction)instruction).getName();
			
			// Set array to the value at the top of the stack if it's an array, or else wrap it in one.
			ArrayValue array;
			Value iteratedValue = getStack().popValue(true, false);
			if(iteratedValue instanceof ArrayValue) {
				array = checkArrayValue(iteratedValue);
			} else {
				array = new ArrayValue(Arrays.asList(iteratedValue), undoStack);
			}
			
			List<Instruction> instructions = function.getInstructions();
			
			Scope scope = getScope();
			popStackFrame();
			addStackFrame(new FunctionValue(scope, undoStack, 0, new ArrayList<Instruction>()));
			getScope().create(name);
			
			for(Value value:array.values(this)) {
				List<Object> key = getCurrentVizObjectKey();
				getScope().set(name, value);
				key.add(value.getKey());
				runAndUndoInstructions(getScope(), instructions.subList(frame.getInstructionCounter() + 1, instructions.size()));
				key.remove(key.size() - 1);
			}
			
			return;
		}
		
		// Run any viz filter instruction by returning if the stack value is false.
		if(instruction instanceof VizFilterInstruction) {
			checkBooleanValue(getStack().peekValue(0));
			BooleanValue condition = checkBooleanValue(getStack().popValue(true, false));
			
			if(!condition.getValue()) {
				undoStack.saveUndoPoint(getCurrentStackFrame().getInstructionCounter());
				popStackFrame();
				return;
			}
			
			frame.setInstructionCounter(frame.getInstructionCounter() + 1);

			return;
		}

		// Execute normal instruction.
		try {
			instruction.execute(this);
			undoStack.addInstructionUndo();
		} catch(ExecutionException e) {
			undoStack.undo(this);
			throw new ExecutionException(e.getMessage() + " (at line " + instruction.getLineNumber() + ")", e);
		}
		
		frame.setInstructionCounter(frame.getInstructionCounter() + 1);
	}
	
	public void addStackFrame(FunctionValue function) {
		NonGlobalScope scope = new NonGlobalScope(function.getParentScope(), undoStack);
		addStackFrame(new StackFrame(function, scope, undoStack), true);
	}
	
	public void addStackFrame(StackFrame frame, boolean addUndoCommand) {
		stackFrames.add(frame);
		
		frame.updateVizObjects();
		
		if(addUndoCommand) {
			undoStack.addCommandUndo(new Runnable() {
				public void run() {
					stackFrames.remove(stackFrames.size() - 1);
				}
				
				public String toString() {
					return "[ADD STACK FRAME]";
				}
			});
		}
	}
	
	/**
	 * Returns all the instructions from startInstruction to endInstruction assuming the current
	 * instruction counter is at startInstruction. Handles any nested start and end instructions
	 * and advances the instruction pointer to after the end instruction.
	 */
	public List<Instruction> getInstructionsUpTo(Class<? extends Instruction> startInstruction, Class<? extends Instruction> endInstruction) {
		StackFrame frame = getCurrentStackFrame();
		List<Instruction> instructions = new ArrayList<>();
		int nestedCount = 1;
		do {
			frame.setInstructionCounter(frame.getInstructionCounter() + 1);
			Instruction instruction = frame.getFunction().getInstructions().get(frame.getInstructionCounter());
			if(startInstruction.isInstance(instruction)) {
				nestedCount++;
			} else if(endInstruction.isInstance(instruction)) {
				nestedCount--;
			}
			instructions.add(instruction);
		} while(nestedCount > 0);
		instructions.remove(instructions.size() - 1);
		return instructions;
	}
	
	/**
	 * Returns whether the current instruction position is at the start of the program.
	 */
	public boolean atStart() {
		return undoStack.getSize() == 0;
	}

	/**
	 * Returns whether the current instruction position is at the end of the program.
	 */
	public boolean atEnd() {
		if(stackFrames.size() > 1) return false;
		StackFrame stackFrame = stackFrames.get(0);
		return stackFrame.getInstructionCounter() >= stackFrame.getFunction().getInstructions().size();
	}
	
	public StackFrame getCurrentStackFrame() {
		return stackFrames.isEmpty() ? null : stackFrames.get(stackFrames.size() - 1);
	}
	
	public void clearStackFrames() {
		stackFrames.clear();
	}
	
	public void popStackFrame() {
		if(stackFrames.size() > 1) {
			stackFrames.get(stackFrames.size() - 1).clearVizObjectInstructions();
			undoStack.addPopStackFrameUndo(stackFrames.get(stackFrames.size() - 1));
			stackFrames.remove(stackFrames.size() - 1);
			markVizObjectsDirty();
		}
	}
	
	public Scope getScope() {
		return getCurrentStackFrame().getScope();
	}
	
	public Scope getGlobalScope() {
		return stackFrames.get(0).getScope();
	}
	
	public boolean getVizUpdatesEnabled() {
		return vizUpdatesEnabled;
	}
	
	public void setVizUpdatesEnabled(boolean vizUpdatesEnabled) {
		this.vizUpdatesEnabled = vizUpdatesEnabled;
	}
	
	/**
	 * Mark viz objects dirty so they will be recreated when they are next retrieved.
	 */
	public void markVizObjectsDirty() {
		if(!vizObjectsDirty) {
			vizObjectsDirty = true;
			undoStack.addMarkVizObjectsDirtyUndo();
		}
	}
	
	/**
	 * Clear the viz objects dirty state, undoing any markVizObjectsDirty call.
	 */
	public void clearVizObjectsDirty() {
		vizObjectsDirty = false;
	}
	
	public List<VizObject> getVizObjects() {
		if(vizUpdatesEnabled) {
			do {
				vizObjectsDirty = false;
				refreshVizObjects(true);
			} while(vizObjectsDirty);
		}
		return vizObjects;
	}
	
	public void refreshVizObjects(boolean dirty) {
		try {
			List<VizObject> vizObjects = new ArrayList<>();
			for(StackFrame stackFrame:new ArrayList<>(stackFrames)) {
				vizObjects.addAll(stackFrame.getVizObjects(dirty));
			}
			this.vizObjects = applyVizObjectFilters(vizObjects);
		} catch(ExecutionException e) {
			throwError(e.getMessage());
		}
	}

	/**
	 * Filter earlier viz objects based on any filters defined in later ones.
	 */
	private static List<VizObject> applyVizObjectFilters(List<VizObject> vizObjectsWithFilters) {
		List<VizObject> vizObjects = new ArrayList<>();
		for(int i = 0; i < vizObjectsWithFilters.size(); i++) {
			VizObject filter = vizObjectsWithFilters.get(i);
			if(!filter.isFilterEnabled()) {
				// Object without filter - add directly to list.
				vizObjects.add(filter);
			} else {
				// Object with filter - apply filter to previous items.
				for(int j = 0; j < i; j++) {
					vizObjectsWithFilters.get(j).applyFilter(filter);
				}
			}
		}
		return vizObjects;
	}

	public void throwError(String error) {
		error = StringUtils.abbreviate(error, MAX_OUTPUT_LENGTH);
		System.err.println("Error: " + error);
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				output.remove(output.size() - 1);
			}
			
			public String toString() {
				return "[THROW ERROR]";
			}
		});
		output.add(new OutputLine(error, OutputType.ERROR));
	}
	
	public void info(String command) {
		command = StringUtils.abbreviate(command, MAX_OUTPUT_LENGTH);
		System.out.println(command);
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				output.remove(output.size() - 1);
			}
			
			public String toString() {
				return "[INFO]";
			}
		});
		output.add(new OutputLine(command, OutputType.INFO));
	}
	
	public void print(String value) {
		value = StringUtils.abbreviate(value, MAX_OUTPUT_LENGTH);
		System.out.println(value);
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				output.remove(output.size() - 1);
			}
			
			public String toString() {
				return "[PRINT]";
			}
		});
		output.add(new OutputLine(value, OutputType.STANDARD));
	}

	public Stack getStack() {
		return stack;
	}

	public List<OutputLine> getOutput() {
		return output;
	}
	
	public UndoStack getUndoStack() {
		return undoStack;
	}
	
	/**
	 * Returns the viz objects that are currently being added within a STARTVIZ/ENDVIZ block.
	 */
	public List<VizObject> getCurrentVizObjects() {
		return currentVizObjects;
	}

	/**
	 * Returns the key for the next viz object to be created with.
	 */
	public List<Object> getCurrentVizObjectKey() {
		return currentVizObjectKey;
	}
	
	public void addValueReadObserver(ValueReadObserver observer) {
		valueReadObservers.add(observer);
	}
	
	public void clearValueReadObservers() {
		valueReadObservers.clear();
	}
	
	public void onValueRead(ValueChangeObservable valueChangeObservable) {
		for(ValueReadObserver valueReadObserver:valueReadObservers) {
			valueReadObserver.onValueRead(valueChangeObservable);
		}
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
		s.append("  NestedFunctionDefinitionCount: " + nestedFunctionDefinitionCount).append("\n");
		s.append("  Output: " + output).append("\n");
		s.append("  UndoStack: ").append("\n");
		s.append(undoStack.getState("    "));
		s.append("  VizObjects: ").append(getVizObjects()).append("\n");
		s.append("  VizUpdatesEnabled: " + vizUpdatesEnabled).append("\n");
		
		return s.toString();
	}

	/**
	 * Returns the current line number, or -1 if there is none.
	 */
	public int getLineNumber() {
		StackFrame stackFrame = getCurrentStackFrame();
		if(stackFrame == null) return -1;
		List<Instruction> instructions = stackFrame.getFunction().getInstructions();
		if(stackFrame.getInstructionCounter() >= instructions.size()) return -1;
		Instruction instruction = instructions.get(stackFrame.getInstructionCounter());
		return instruction.getLineNumber();
	}
	
	/**
	 * Returns the current instruction, or null if there is none.
	 */
	public Instruction getInstruction() {
		StackFrame stackFrame = getCurrentStackFrame();
		if(stackFrame == null) return null;
		List<Instruction> instructions = stackFrame.getFunction().getInstructions();
		if(stackFrame.getInstructionCounter() >= instructions.size()) return null;
		return instructions.get(stackFrame.getInstructionCounter());
	}

	/**
	 * Returns the value of the variable at lineNumber and columnNumber, or null if there is none.
	 */
	public String getValueAt(int lineNumber, int columnNumber) {
		for(int i = stackFrames.size() - 1; i >= 0; i--) {
			String value = stackFrames.get(i).getValueAt(lineNumber, columnNumber);
			if(value != null) {
				return value;
			}
		}
		return null;
	}
	
	public void clearIdentifierValues() {
		for(StackFrame stackFrame:stackFrames) {
			stackFrame.clearIdentifierValues();
		}
	}

	public int getInstructionsExecutedCount() {
		return undoStack.getInstructionsExecuted();
	}
}