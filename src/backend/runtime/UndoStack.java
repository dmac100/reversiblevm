package backend.runtime;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TShortArrayList;

import java.util.ArrayList;
import java.util.List;

import backend.instruction.Instruction;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.NullValue;
import backend.value.Value;

public class UndoStack {
	public final static int POPSTACKFRAME = -1;
	public final static Runnable UNDOPOINT = null;
	
	private final short DOUBLETYPE = 0;
	private final short BOOLEANTRUETYPE = 1;
	private final short BOOLEANFALSETYPE = 2;
	private final short NULLTYPE = 3;
	private final short GENERALTYPE = 4;
	
	private final List<Runnable> commandUndos = new ArrayList<>();
	private final TIntArrayList instructionCounterUndos = new TIntArrayList();
	private final List<StackFrame> popStackFrameUndos = new ArrayList<>();
	
	private final List<Value> popValueUndos = new ArrayList<>();
	private final TDoubleArrayList popDoubleValueUndos = new TDoubleArrayList();
	private final TShortArrayList popValueUndoTypes = new TShortArrayList();
	
	private boolean undoEnabled = true;

	public boolean isUndoEnabled() {
		return undoEnabled;
	}
	
	public void setUndoEnabled(boolean undoEnabled) {
		this.undoEnabled = undoEnabled;
	}
	
	public void addCommandUndo(Runnable command) {
		if(!undoEnabled) return;
		
		commandUndos.add(command);
	}
	
	public void addPopStackFrameUndo(StackFrame stackFrame) {
		if(!undoEnabled) return;
		
		instructionCounterUndos.add(POPSTACKFRAME);
		popStackFrameUndos.add(stackFrame);
	}
	
	public void addPopValueUndo(Value value) {
		if(!undoEnabled) return;
		
		if(value instanceof DoubleValue) {
			popDoubleValueUndos.add(((DoubleValue)value).getValue());
			popValueUndoTypes.add(DOUBLETYPE);
		} else if(value instanceof BooleanValue) {
			popValueUndoTypes.add(((BooleanValue)value).getValue() ? BOOLEANTRUETYPE : BOOLEANFALSETYPE);
		} else if(value instanceof NullValue) {
			popValueUndoTypes.add(NULLTYPE);
		} else {
			popValueUndoTypes.add(GENERALTYPE);
			popValueUndos.add(value);
		}
	}
	
	public void undoPopValue(Runtime runtime) {
		if(!undoEnabled) return;
		
		short type = popValueUndoTypes.removeAt(popValueUndoTypes.size() - 1);
		
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

	public void undo(Runtime runtime, boolean undoInstruction) {
		if(!undoEnabled) return;
		
		undoCommands();
		undoPopStackFrames(runtime);
		if(undoInstruction) {
			undoInstruction(runtime);
		}
		undoInstructionCounterChange(runtime);
	}
	
	private void undoCommands() {
		while(!commandUndos.isEmpty()) {
			Runnable command = commandUndos.remove(commandUndos.size() - 1);
			if(command == UNDOPOINT) {
				break;
			}
			command.run();
		}
	}

	private void undoPopStackFrames(Runtime runtime) {
		if(!instructionCounterUndos.isEmpty()) {
			// Undo any pop stack frame.
			if(instructionCounterUndos.get(instructionCounterUndos.size() - 1) == POPSTACKFRAME) {
				instructionCounterUndos.removeAt(instructionCounterUndos.size() - 1);
				StackFrame stackFrame = popStackFrameUndos.remove(popStackFrameUndos.size() - 1);
				runtime.addStackFrame(stackFrame, false);
			}
		}
	}
	
	private void undoInstruction(Runtime runtime) {
		if(!instructionCounterUndos.isEmpty()) {
			int instructionCounter = instructionCounterUndos.get(instructionCounterUndos.size() - 1);
		
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
	
	private void undoInstructionCounterChange(Runtime runtime) {
		// Undo instruction counter changes.
		if(!instructionCounterUndos.isEmpty()) {
			int instructionCounter = instructionCounterUndos.removeAt(instructionCounterUndos.size() - 1);
			if(runtime.getCurrentStackFrame() != null) {
				runtime.getCurrentStackFrame().setInstructionCounter(instructionCounter);
			}
		}
	}

	public void saveUndoPoint(int currentIstructionPointer) {
		if(!undoEnabled) return;
		
		commandUndos.add(UNDOPOINT);
		instructionCounterUndos.add(currentIstructionPointer);
	}
	
	public String getState(String prefix) {
		StringBuilder s = new StringBuilder();
		s.append(prefix).append("Command Undos: " + commandUndos.size()).append("\n");
		s.append(prefix).append("Instruction Counter Undos: " + instructionCounterUndos.size()).append("\n");
		s.append(prefix).append("Pop Stack Frame Undos: " + popStackFrameUndos.size()).append("\n");
		s.append(prefix).append("Pop Value Undos: " + popValueUndos.size()).append("\n");
		s.append(prefix).append("Pop Double Value Undos: " + popDoubleValueUndos.size()).append("\n");
		s.append(prefix).append("Pop Value Undo Types: " + popValueUndoTypes.size());
		return s.toString();
	}
	
	public int getSize() {
		int size = 0;
		size += commandUndos.size();
		size += instructionCounterUndos.size();
		size += popStackFrameUndos.size();
		size += popValueUndos.size();
		size += popDoubleValueUndos.size();
		size += popValueUndoTypes.size();
		return size;
	}
	
	public void clear() {
		commandUndos.clear();
		instructionCounterUndos.clear();
		popStackFrameUndos.clear();
		popValueUndos.clear();
		popDoubleValueUndos.clear();
		popValueUndoTypes.clear();
	}
}