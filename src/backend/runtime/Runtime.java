package backend.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import backend.instruction.Instruction;
import backend.instruction.viz.VizFilterInstruction;
import backend.instruction.viz.VizIterateInstruction;
import backend.observer.ValueChangeObservable;
import backend.observer.ValueReadObserver;
import backend.value.ArrayValue;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.FunctionValue;
import backend.value.ObjectValue;
import backend.value.StringValue;
import backend.value.Value;

public class Runtime implements HasState, ValueReadObserver {
	private UndoStack undoStack = new UndoStack();
	
	private Stack stack = new Stack(undoStack);
	private StackFrame lastStackFrame = null;
	private List<StackFrame> stackFrames = new ArrayList<>();
	private int nestedFunctionDefinitionCount = 0;
	private List<VizObject> currentVizObjects = new ArrayList<>();
	private Set<ValueReadObserver> valueReadObservers = new HashSet<>();
	
	private List<String> errors = new ArrayList<>();
	private List<String> output = new ArrayList<>();
	
	/**
	 * Runs and then undoes all the given instructions within a new stack frame,
	 * for the purposes of getting the visual objects for a block of instructions.
	 */
	public void runAndUndoInstructions(List<Instruction> instructions) {
		undoStack.saveUndoPoint(getCurrentStackFrame().getInstructionCounter());
		addStackFrame(new FunctionValue(getScope(), 0, instructions));
		
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
			
			checkArrayValue(getStack().peekValue(0));
			ArrayValue array = checkArrayValue(getStack().popValue(true, false));
			
			List<Instruction> instructions = function.getInstructions();
			
			popStackFrame();
			addStackFrame(new FunctionValue(getScope(), 0, new ArrayList<Instruction>()));
			getScope().create(name);
			
			for(Value value:array.values(this)) {
				getScope().set(name, value);
				runAndUndoInstructions(instructions.subList(frame.getInstructionCounter() + 1, instructions.size()));
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
			throw e;
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
		return getCurrentStackFrame() == null;
	}
	
	public StackFrame getCurrentStackFrame() {
		return stackFrames.isEmpty() ? null : stackFrames.get(stackFrames.size() - 1);
	}
	
	public StackFrame popStackFrame() {
		if(stackFrames.isEmpty()) return null;
		stackFrames.get(stackFrames.size() - 1).clearVizObjectInstructions();
		undoStack.addPopStackFrameUndo(stackFrames.get(stackFrames.size() - 1));
		StackFrame stackFrame = stackFrames.remove(stackFrames.size() - 1);
		if(stackFrames.isEmpty()) {
			lastStackFrame = stackFrame;
		}
		return stackFrame;
	}
	
	public Scope getScope() {
		return getCurrentStackFrame().getScope();
	}
	
	public Scope getGlobalScope() {
		return stackFrames.get(0).getScope();
	}
	
	public List<VizObject> getVizObjects() {
		if(stackFrames.isEmpty() && lastStackFrame != null) {
			// Restore last stack frame to display final visual.
			stackFrames.add(lastStackFrame);
			List<VizObject> vizObjects = lastStackFrame.getVizObjects();
			stackFrames.clear();
			return vizObjects;
		} else {
			List<VizObject> vizObjects = new ArrayList<>();
			for(StackFrame stackFrame:new ArrayList<>(stackFrames)) {
				vizObjects.addAll(stackFrame.getVizObjects());
			}
			return vizObjects;
		}
	}
	
	public void throwError(String error) {
		System.err.println("Error: " + error);
		undoStack.addCommandUndo(new Runnable() {
			public void run() {
				errors.remove(errors.size() - 1);
			}
			
			public String toString() {
				return "[THROW ERROR]";
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
			
			public String toString() {
				return "[PRINT]";
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
	
	/**
	 * Returns the viz objects that are currently being added within a STARTVIZ/ENDVIZ block.
	 */
	public List<VizObject> getCurrentVizObjects() {
		return currentVizObjects;
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
		s.append("  Errors: " + errors).append("\n");
		s.append("  Output: " + output).append("\n");
		s.append("  UndoStack: ").append("\n");
		s.append("  VizObjects: ").append(getVizObjects()).append("\n");
		s.append(undoStack.getState("    "));
		
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
}