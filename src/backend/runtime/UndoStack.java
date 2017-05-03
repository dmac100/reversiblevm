package backend.runtime;

import java.util.ArrayList;
import java.util.List;

import backend.instruction.Instruction;
import backend.util.IntStack;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.NullValue;
import backend.value.Value;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Stores values needed to restore previous state to enable undo of instructions.
 * Values are added to the undo stack when state is modified, and removed on undo.
 * Values are split across multiple stacks internally to allow for more efficient storage and compression.
 */
public class UndoStack {
	private final int RUNINSTRUCTION = 1;
	private final int POPSTACKFRAME = 2;
	private final int MARKVIZOBJECTSDIRTY = 4;
	private final static Runnable UNDOPOINT = null;
	
	private final short DOUBLETYPE = 0;
	private final short BOOLEANTRUETYPE = 1;
	private final short BOOLEANFALSETYPE = 2;
	private final short NULLTYPE = 3;
	private final short GENERALTYPE = 4;
	
	private final List<Runnable> commandUndos = new ArrayList<>();
	private final IntStack instructionCounterUndos = new IntStack();
	private final List<StackFrame> popStackFrameUndos = new ArrayList<>();
	
	private final List<Value> popValueUndos = new ArrayList<>();
	private final TDoubleArrayList popDoubleValueUndos = new TDoubleArrayList();
	private final IntStack popValueUndoTypes = new IntStack();
	
	private final IntStack flagStack = new IntStack();
	
	private boolean undoEnabled = true;

	/**
	 * Returns whether undo is enabled.
	 */
	public boolean isUndoEnabled() {
		return undoEnabled;
	}
	
	/**
	 * Enables undo.
	 */
	public void setUndoEnabled(boolean undoEnabled) {
		this.undoEnabled = undoEnabled;
	}
	
	/**
	 * Adds an undo command which will be run on undo.
	 */
	public void addCommandUndo(Runnable command) {
		if(!undoEnabled) return;
		
		commandUndos.add(command);
	}
	
	/**
	 * Stores that the a stack frame was popped since the previous undo point.
	 */
	public void addPopStackFrameUndo(StackFrame stackFrame) {
		if(!undoEnabled) return;
		
		flagStack.push(flagStack.pop() | POPSTACKFRAME);
		popStackFrameUndos.add(stackFrame);
	}
	
	/**
	 * Stores that there was an instruction executed since the previous undo point.
	 */
	public void addInstructionUndo() {
		if(!undoEnabled) return;
		
		flagStack.push(flagStack.pop() | RUNINSTRUCTION);
	}
	
	/**
	 * Stores that the viz objects were marked dirty since the previous undo point.
	 */
	public void addMarkVizObjectsDirtyUndo() {
		if(!undoEnabled) return;
		
		flagStack.push(flagStack.pop() | MARKVIZOBJECTSDIRTY);
	}
	
	/**
	 * Stores an undo of a stack pop where the given value is the value that was at the top of the stack.
	 */
	public void addPopValueUndo(Value value) {
		if(!undoEnabled) return;
		
		if(value instanceof DoubleValue) {
			popDoubleValueUndos.add(((DoubleValue)value).getValue());
			popValueUndoTypes.push(DOUBLETYPE);
		} else if(value instanceof BooleanValue) {
			popValueUndoTypes.push(((BooleanValue)value).getValue() ? BOOLEANTRUETYPE : BOOLEANFALSETYPE);
		} else if(value instanceof NullValue) {
			popValueUndoTypes.push(NULLTYPE);
		} else {
			popValueUndoTypes.push(GENERALTYPE);
			popValueUndos.add(value);
		}
	}

	/**
	 * Does an undo of a stack pop, restoring the previous value to the top of the stack.
	 */
	public void undoPopValue(Runtime runtime) {
		if(!undoEnabled) return;
		
		int type = popValueUndoTypes.pop();
		
		final Value value;
		if(type == DOUBLETYPE) {
			value = new DoubleValue(popDoubleValueUndos.removeAt(popDoubleValueUndos.size() - 1));
		} else if(type == BOOLEANTRUETYPE) {
			value = new BooleanValue(true);
		} else if(type == BOOLEANFALSETYPE) {
			value = new BooleanValue(false);
		} else if(type == NULLTYPE) {
			value = new NullValue();
		} else {
			value = popValueUndos.remove(popValueUndos.size() - 1);
		}
		
		runtime.getStack().push(value, false);
	}

	/**
	 * Runs a full undo to restore state to the previous undo point.
	 */
	public void undo(Runtime runtime) {
		if(!undoEnabled) return;

		undoCommands();
		undoFlags(runtime);
		undoInstructionCounterChange(runtime);
	}
	
	/**
	 * Runs any undo commands added since the previous undo point.
	 */
	private void undoCommands() {
		while(!commandUndos.isEmpty()) {
			Runnable command = commandUndos.remove(commandUndos.size() - 1);
			if(command == UNDOPOINT) {
				break;
			}
			command.run();
		}
	}

	/**
	 * Does any undo needed based on the flags that were set. These flags are set at most once per instruction, and
	 * restore to the previous undo point.
	 */
	private void undoFlags(Runtime runtime) {
		if(!flagStack.isEmpty()) {
			int flag = flagStack.pop();
			
			if((flag & POPSTACKFRAME) > 0) {
				StackFrame stackFrame = popStackFrameUndos.remove(popStackFrameUndos.size() - 1);
				runtime.addStackFrame(stackFrame, false);
			}
			
			if((flag & RUNINSTRUCTION) > 0) {
				undoInstruction(runtime);
			}
			
			if((flag & MARKVIZOBJECTSDIRTY) > 0) {
				runtime.clearVizObjectsDirty();
			}
		}
	}
	
	/**
	 * Does an undo of instruction specific changes by finding the instruction that was executed and calls the undo method.
	 */
	private void undoInstruction(Runtime runtime) {
		if(!instructionCounterUndos.isEmpty()) {
			int instructionCounter = instructionCounterUndos.peek();
		
			// Undo instruction.
			StackFrame stackFrame = runtime.getCurrentStackFrame();
			if(stackFrame != null) {
				if(instructionCounter < stackFrame.getFunction().getInstructions().size()) {
					Instruction instruction = stackFrame.getFunction().getInstructions().get(instructionCounter);
					instruction.undo(runtime);
				}
			}
		}
	}

	/**
	 * Does an undo of any instruction counter changes.
	 */
	private void undoInstructionCounterChange(Runtime runtime) {
		// Undo instruction counter changes.
		if(!instructionCounterUndos.isEmpty()) {
			int instructionCounter = instructionCounterUndos.pop();
			if(runtime.getCurrentStackFrame() != null) {
				runtime.getCurrentStackFrame().setInstructionCounter(instructionCounter);
			}
		}
	}

	/**
	 * Saves an undo point before running an instruction, so the state can be restored the this state on undo.
	 */
	public void saveUndoPoint(int currentInstructionCounter) {
		if(!undoEnabled) return;
		
		commandUndos.add(UNDOPOINT);
		instructionCounterUndos.push(currentInstructionCounter);
		flagStack.push(0);
	}
	
	/**
	 * Returns the state of the undo stack as a String for testing purposes.
	 */
	public String getState(String prefix) {
		StringBuilder s = new StringBuilder();
		s.append(prefix).append("Command Undos: " + commandUndos.size()).append("\n");
		s.append(prefix).append("Instruction Counter Undos: " + instructionCounterUndos.size()).append("\n");
		s.append(prefix).append("Pop Stack Frame Undos: " + popStackFrameUndos.size()).append("\n");
		s.append(prefix).append("Pop Value Undos: " + popValueUndos.size()).append("\n");
		s.append(prefix).append("Pop Double Value Undos: " + popDoubleValueUndos.size()).append("\n");
		s.append(prefix).append("Pop Value Undo Types: " + popValueUndoTypes.size()).append("\n");
		s.append(prefix).append("Flags: " + flagStack.size());
		return s.toString();
	}

	/**
	 * Returns the total size of all the items in the undo stack.
	 */
	public int getSize() {
		int size = 0;
		size += commandUndos.size();
		size += instructionCounterUndos.size();
		size += popStackFrameUndos.size();
		size += popValueUndos.size();
		size += popDoubleValueUndos.size();
		size += popValueUndoTypes.size();
		size += flagStack.size();
		return size;
	}
	
	/**
	 * Clears the undo stack so that it is empty.
	 */
	public void clear() {
		commandUndos.clear();
		instructionCounterUndos.clear();
		popStackFrameUndos.clear();
		popValueUndos.clear();
		popDoubleValueUndos.clear();
		popValueUndoTypes.clear();
		flagStack.clear();
	}
	
	/**
	 * Returns the number of instructions that have been executed and saved on the undo stack. 
	 */
	public int getInstructionsExecuted() {
		return instructionCounterUndos.size();
	}
}